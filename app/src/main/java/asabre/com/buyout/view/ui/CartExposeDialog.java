package asabre.com.buyout.view.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class CartExposeDialog extends DialogFragment {
    private static final String TAG = CartExposeDialog.class.getSimpleName();


    private TextView cartExposeTopWord;
    private TextView exposeCartSize;
    private TextView exposeCartColor;
    private TextView exposeCartQuantity;
    private TextView exposeCartMaterial;
    private TextView exposeCartSubTotal;
    private TextView exposeCartName;
    private ExtendedFloatingActionButton exposeCartCloseFAB;

    private ProductImageAdapter mProductImageAdapter;
    private Cart mCart = ViewModelHomeFragment.cartDetails;

    // show cart detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];

    public static CartExposeDialog getInstance(){
        return new CartExposeDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_expse_cart, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
       setCartDetails();
    }

    private void init(View view){
        cartExposeTopWord = view.findViewById(R.id.cartExposeTopWord);
         exposeCartSize = view.findViewById(R.id.exposeCartSize);
         exposeCartColor = view.findViewById(R.id.exposeCartColor);
         exposeCartQuantity = view.findViewById(R.id.exposeCartQuantity);
         exposeCartMaterial = view.findViewById(R.id.exposeCartMaterial);
         exposeCartSubTotal = view.findViewById(R.id.exposeCartSubTotal);
//         exposeCartName = view.findViewById(R.id.exposeCartName);
         exposeCartCloseFAB = view.findViewById(R.id.exposeCartCloseFAB);
         mDotsLayout = view.findViewById(R.id.exposeCartImageCount);
        setRecyclerView(view);
    }

    private void setRecyclerView(View view){
        mProductImageAdapter = new ProductImageAdapter(getContext(), new ArrayList<ProductImage>());
        RecyclerView holderCartExposeImages = view.findViewById(R.id.holderExposeCartImages);
        holderCartExposeImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holderCartExposeImages.setAdapter(mProductImageAdapter);
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

    private void setCartDetails(){
        ArrayList<ProductImage> photoList = new ArrayList<>();
        for(String str : mCart.getImages()){
            ProductImage pi = new ProductImage(str);
            photoList.add(pi);
        }
        mProductImageAdapter.loadNewData(photoList);
        setTextViews();
    }

    private void setTextViews(){
        exposeCartName.setText(mCart.getProductName());
        exposeCartSize.setText(mCart.getSize());
        exposeCartColor.setText(mCart.getColor());
        exposeCartQuantity.setText(mCart.getQuantity());
        exposeCartMaterial.setText(mCart.getMaterial());
        exposeCartSubTotal.setText(String.format(Locale.US, "GH¢ %s",  mCart.getOrderValue()));
    }


    @Override
    public void onResume() {
        //         Full screen dialog
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);

        super.onResume();
    }

    private View.OnClickListener closeCartExposeListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        exposeCartCloseFAB.setOnClickListener(closeCartExposeListener());
        cartExposeTopWord.setOnClickListener(closeCartExposeListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        exposeCartCloseFAB.setOnClickListener(null);
    }

}























