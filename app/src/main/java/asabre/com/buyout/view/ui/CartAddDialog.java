package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.materialspinner.MaterialSpinner;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ColorAdapter;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.view.adapter.SizeAdapter;
import asabre.com.buyout.view.callback.AddCartFromHomeCallback;
import asabre.com.buyout.view.callback.ColorCallback;
import asabre.com.buyout.view.callback.SizeCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class CartAddDialog extends DialogFragment implements
        SizeCallback,
        ColorCallback,
        AddCartFromHomeCallback {
    private static final String TAG = CartAddDialog.class.getSimpleName();

    private TextView addCartName;
    private TextView addCartSubTotal;
    private TextView addCartTheSize;
    private TextView addCartTheColor;
    private ExtendedFloatingActionButton addCartCloseFAB;
    private ExtendedFloatingActionButton addCartAddToCartFAB;

    // show cart detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];
    private ProductImageAdapter mProductImageAdapter;
    private SizeAdapter mSizeAdapter;
    private ColorAdapter mColorAdapter;
    private MaterialSpinner mMaterialSpinner;
    private AddCartFromHomeCallback mAddCartFromHomeCallback = this;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;

    private Product mProduct = ViewModelHomeFragment.productDetails;
    private Cart mCart = new Cart();
    private double mNewPrice = 0.0;


    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();

    public static CartAddDialog getInstance(){
        return new CartAddDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setViewModel();
        setValues();
    }

    // from AddCartFromHomeCallback
    @Override
    public void addCartFromHome(Cart cart) {
        // add the created cart to the existing carts
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart);
        mViewModelHomeFragment.cartToCart(cartList);
    }

    // from SizeCallback
    @Override
    public void selectColor(String colorName) {
        mCart.setColor(colorName);
        addCartTheColor.setText(colorName);
    }

    // from ColorCallback
    @Override
    public void selectSize(String sizeNumber) {
        mCart.setSize(sizeNumber);
        addCartTheSize.setText(sizeNumber);
    }

    @Override
    public void onResume() {
//      Full screen dialog
//      Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);

        super.onResume();
    }

    private void setViewModel(){
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();
    }

    private void setValues(){
        // get number out of discount
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(mProduct.getDiscount());
        String num = "";
        while (m.find()){ num = m.group();}

        // get the new price
        double oldPrice = Double.parseDouble(mProduct.getPrice());
        double disD = Double.parseDouble(num);
//        double newPrice = (100 - disD) * oldPrice / 100.00;
        mNewPrice = (100 - disD) * oldPrice / 100.00;

        addCartSubTotal.setText(String.format(Locale.US, "GH¢ %.2f", mNewPrice));
    }

    private void setProductDetails(){
        setTextViews();
        mProductImageAdapter.loadNewData(photoList());
        mColorAdapter.loadNewData(colorList());
        mSizeAdapter.loadNewData(sizeList());
        setMaterialSpinnerValues(Integer.parseInt(mProduct.getStock()));
    }

    private void setTextViews(){
        addCartName.setText(mProduct.getName());
        addCartSubTotal.setText(String.format(Locale.US, "GH¢ %.2f", Double.parseDouble(mProduct.getPrice())));
    }
    private ArrayList<ProductImage> photoList(){
        ArrayList<ProductImage> photoList = new ArrayList<>();
        for(String str : mProduct.getImages()){
            photoList.add(new ProductImage(str));
        }
        return photoList;
    }
    private ArrayList<ProductImage> colorList(){
        ArrayList<ProductImage> colorList = new ArrayList<>();
        String[] colors = mProduct.getAvailColors().split(",");
        for (String str : colors){
            colorList.add(new ProductImage(str));
        }
        return colorList;
    }
    private ArrayList<ProductImage> sizeList(){
        ArrayList<ProductImage> sizeList = new ArrayList<>();
        String[] sizes = mProduct.getAvailSize().split(",");
        for (String str : sizes){
            sizeList.add(new ProductImage(str));
        }
        return sizeList;
    }

    private void setMaterialSpinnerValues(int stock){
        // user can't select more than 30 products
        ArrayList<String> quantity = new ArrayList<>();
        for(int j=0; j<stock; j++){
            if(j > 29){break;}
            quantity.add(String.valueOf(j + 1));
        }
        // convert arrayList to array
        String[] QuantityAdd = new String[quantity.size()];
        String[] quantitySelect = quantity.toArray(QuantityAdd);

        ArrayAdapter<String> quantitySpinner = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item, quantitySelect);
        quantitySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaterialSpinner.setAdapter(quantitySpinner);
        mMaterialSpinner.setError("Please select quantity");
        mMaterialSpinner.setLabel("Quantity");
    }

    private void selectQuantity(){
        // when product quantity is selected from the spinner
        mMaterialSpinner.setItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String quantity = parent.getItemAtPosition(position).toString();
                mCart.setQuantity(quantity);
                Double total = mNewPrice * Double.parseDouble(quantity);
                addCartSubTotal.setText(String.format(Locale.US, "GH¢ %.2f", total));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCart.setQuantity("1");
            }
        });

    }

    private HashMap<String, String> createCart(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", mProduct.get_id());
        obj.put("customerId", mCustomerId);
        obj.put("quantity", mCart.getQuantity());
        obj.put("color", mCart.getColor());
        obj.put("size", mCart.getSize());
        obj.put("userGpsLocation", mCart.getUserGpsLocation());
        obj.put("thirdPartyId", "");
        return obj;

    }

    private boolean colorAndSizeSelected(){
        return (!mCart.getColor().isEmpty() && !mCart.getSize().isEmpty());
    }

    private boolean userIsSignedIn(){
        return !mCustomerId.isEmpty();
    }

    private View.OnClickListener createCartListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){

                    if(userIsSignedIn()){
                        if(colorAndSizeSelected()){
                            showProgressDialog();
                            mViewModelHomeFragment.createCart(createCart()).observe(getActivity(), new Observer<Cart>() {
                                @Override
                                public void onChanged(Cart cart) {
                                    mAddCartFromHomeCallback.addCartFromHome(cart);
                                    mViewModelHomeFragment.mCreateCart = new MutableLiveData<>();
                                    dismissProgressDialog();
                                    dismiss();
                                }
                            });
                        } else {infoDialog("To add product to cart, please select product size and color"); }
                    } else {infoDialog("Please sign in to add product to cart");
                    }

                }
            }
        };
    }


    private void infoDialog(String msg){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setMessage(msg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }

    private void init(View view){
        mDotsLayout = view.findViewById(R.id.addCartImageCount);
        addCartName = view.findViewById(R.id.addCartName);
        addCartSubTotal = view.findViewById(R.id.addCartSubTotal);
        addCartTheSize = view.findViewById(R.id.addCartTheSize);
        addCartTheColor = view.findViewById(R.id.addCartTheColor);
        addCartCloseFAB = view.findViewById(R.id.addCartCloseFAB);
        mMaterialSpinner = view.findViewById(R.id.addCartQuantitySpinner);
        addCartAddToCartFAB = view.findViewById(R.id.addCartAddToCartFAB);

        setRecyclerView(view);
        setProductDetails();
    }

    private void setRecyclerView(View view){
        // for images
        mProductImageAdapter = new ProductImageAdapter(getContext(), new ArrayList<ProductImage>());
        RecyclerView holderAddCartImages = view.findViewById(R.id.holderAddCartImages);
        holderAddCartImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holderAddCartImages.setAdapter(mProductImageAdapter);

        // for size
        RecyclerView holderAddCartSelectSize = view.findViewById(R.id.holderAddCartSelectSize);
        holderAddCartSelectSize.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSizeAdapter = new SizeAdapter(this.getContext(), new ArrayList<ProductImage>(), this);
        holderAddCartSelectSize.setAdapter(mSizeAdapter);

        // for color
        RecyclerView holderAddCartSelectColor = view.findViewById(R.id.holderAddCartSelectColor);
        holderAddCartSelectColor.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorAdapter = new ColorAdapter(this.getContext(), new ArrayList<ProductImage>(), this);
        holderAddCartSelectColor.setAdapter(mColorAdapter);

        setDots();
    }

    private void setDots(){
        mDotsText = new TextView[3];  // set dots
        for(int z = 0; z< 3; z++){    // here we get the dots
            mDotsText[z] = new TextView(getContext());
            mDotsText[z].setText(". ");
            mDotsText[z].setTextSize(35);
            mDotsText[z].setTypeface(null, Typeface.BOLD);
            mDotsText[z].setTextColor(Color.parseColor("#909090"));
            mDotsLayout.addView(mDotsText[z]);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        selectQuantity();
        addCartCloseFAB.setOnClickListener(closeDialog());
        addCartAddToCartFAB.setOnClickListener(createCartListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        addCartCloseFAB.setOnClickListener(null);
        addCartAddToCartFAB.setOnClickListener(null);
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Adding to cart");
        mProgressDialog.setMessage("Please wait.");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    }

    private View.OnClickListener closeDialog(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }



}
