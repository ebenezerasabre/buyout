package asabre.com.buyout.view.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProductOfTheDayFragment extends Fragment implements BaseFragment {
    private static final String TAG = ProductOfTheDayFragment.class.getSimpleName();


    private TextView todayTopWord;
    private ProductImageAdapter mProductImageAdapter;
    private TextView todayName;
    private TextView todayNewPrice;
    private ImageView todayAddCart;
    private ImageView todayAddWishList;
    private TextView todayDescription;
    private ExtendedFloatingActionButton todayCloseFAB;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressBar indeterminateProductOfTheDayFragment;
    private LinearLayout itemContainer;

    // show product detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];

    private ArrayList<Product> mWishLists = new ArrayList<>();

    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();
    String PRODUCT_ID = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_today, container, false);
        init(view);
        setProductOfTheDayViewModel();
        return view;
    }


    private void init(View view){
        todayTopWord = view.findViewById(R.id.todayTopWord);
        todayName = view.findViewById(R.id.todayName);
        todayNewPrice = view.findViewById(R.id.todayNewPrice);
        todayAddCart = view.findViewById(R.id.todayAddCart);
        todayAddWishList = view.findViewById(R.id.todayAddWishList);
        todayDescription = view.findViewById(R.id.todayDescription);
        todayCloseFAB = view.findViewById(R.id.todayCloseFAB);
        indeterminateProductOfTheDayFragment = view.findViewById(R.id.indeterminateProductOfTheDayFragment);
        mDotsLayout = view.findViewById(R.id.todayImageCount);
        itemContainer = view.findViewById(R.id.proTodayItems);
        itemContainer.setVisibility(View.GONE);

        // for recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.holderTodayImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mProductImageAdapter = new ProductImageAdapter(this.getContext(), new ArrayList<ProductImage>());
        recyclerView.setAdapter(mProductImageAdapter);

    }

    private void setProductOfTheDayViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
            mViewModelHomeFragment.getProductOfTheDay().observe(this, new Observer<Product>() {
                @Override
                public void onChanged(Product product) {
                    indeterminateProductOfTheDayFragment.setVisibility(View.GONE);
                    itemContainer.setVisibility(View.VISIBLE);
                    setValues(product);
                    setDots();
                }
            });
        }
    }

    private void setValues(Product product){
        PRODUCT_ID = product.get_id();
        ViewModelHomeFragment.productDetails = product;

        // set images
        ArrayList<ProductImage> photoList = new ArrayList<>();
        for(String str : product.getImages()){
            photoList.add(new ProductImage(str));
        }
        // load new photos
        mProductImageAdapter.loadNewData(photoList);

        // check if product is part of wishList
        setAddWIshListColor(productIsPartOfWishList(PRODUCT_ID));

        // get number out of discount
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(product.getDiscount());
        String num = "";
        while (m.find()){ num = m.group(); }

        // get the new price
        double oldPrice = Double.parseDouble(product.getPrice());
        double disD = Double.parseDouble(num);
        double newPrice = (100 - disD) * oldPrice / 100.00;

        todayName.setText(product.getName());
        todayNewPrice.setText(String.format(Locale.US, "GH¢ %.2f", newPrice));
        todayDescription.setText(product.getAbout());


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


    private boolean productIsPartOfWishList(String productId){
        if(mViewModelHomeFragment.getCustomerWishList() != null){
            mWishLists.addAll(Objects.requireNonNull(mViewModelHomeFragment.getCustomerWishList().getValue()));
            boolean isPart = false;
            for(Product pr : mWishLists){
                if (productId.equals(pr.get_id())) {
                    isPart = true;
                    break;
                }
            }
            Log.d(TAG, "isProductPartOfWishList: isPart ? " + isPart);
            return isPart;
        }
        return false;
    }

    private void setAddWIshListColor(boolean isPart){
        if(isPart){
            todayAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }


    private void setWishList(final String productId){
        if(userIsSignedIn()){
            if(getActivity() != null){
                // make addWishList add color onClick
                 todayAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
                if(!productIsPartOfWishList(productId)){
                    mViewModelHomeFragment.createWishList(createWishList(productId)).observe(getActivity(), new Observer<Product>() {
                        @Override
                        public void onChanged(Product product) {
                            List<Product> products = new ArrayList<>();
                            products.add(product);
                            mViewModelHomeFragment.productToWishList(products);
                            mViewModelHomeFragment.mCreateWishList = new MutableLiveData<>();
                        }
                    });
                } else { Toast.makeText(getContext(), "Part of wishList", Toast.LENGTH_SHORT).show(); }
            }
        } else { showSignIn(); }

    }


    private HashMap<String, String> createWishList(String productId){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", productId);
        obj.put("customerId", mCustomerId);
        obj.put("thirdPartyId", "");
        return obj;
    }
    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null && !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }

    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }


    private void loadProductsFragment(Fragment fragment){
        if(fragment != null && getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private View.OnClickListener wishListListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWishList(PRODUCT_ID);
            }
        };
    }

    private View.OnClickListener cartListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCartAddDialog();
            }
        };
    }

    private void loadCartAddDialog(){
        FragmentManager fm = getFragmentManager();
        CartAddDialog cd = CartAddDialog.getInstance();
        cd.setTargetFragment(ProductOfTheDayFragment.this, 300);
        assert fm != null;
        cd.show(fm, "CartAddDialog");
    }


    private void setVisibility(){
        if(getActivity() != null){
            MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
            getActivity().findViewById(R.id.containerProducts).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
        }
        removeThisFragment();
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                Log.d(TAG, "removeThisFragment: called");
            }
        }
    }

    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        todayAddWishList.setOnClickListener(wishListListener());
        todayAddCart.setOnClickListener(cartListener());
        todayCloseFAB.setOnClickListener(goBackListener());
        todayTopWord.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        todayAddWishList.setOnClickListener(null);
        todayAddCart.setOnClickListener(null);
        todayCloseFAB.setOnClickListener(null);
        todayTopWord.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            setVisibility();
        }
    }

}
