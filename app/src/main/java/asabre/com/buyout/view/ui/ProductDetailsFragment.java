package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.materialspinner.MaterialSpinner;
import com.google.android.material.button.MaterialButton;
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
import asabre.com.buyout.view.callback.AddFpToWishListCallback;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.view.callback.BoughtTogetherCallback;
import asabre.com.buyout.view.callback.CallCartAddFromHomeCallback;
import asabre.com.buyout.view.callback.ColorCallback;
import asabre.com.buyout.view.callback.ProductClickCallback;
import asabre.com.buyout.view.callback.ProductDetailCallback;
import asabre.com.buyout.view.callback.SizeCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProductDetailsFragment extends Fragment implements
        BaseFragment,
        SizeCallback,
        ColorCallback,
        ProductDetailCallback,
        ProductClickCallback,
        AddFpToWishListCallback,
        AddBTToWishListCallback,
        CallCartAddFromHomeCallback,
        BoughtTogetherCallback

{
    private static final String TAG = ProductDetailsFragment.class.getSimpleName();

    TextView topWord;
//  TextView productDetailsName;
    TextView priceAfterDiscount;
    TextView priceBeforeDiscount;
    RatingBar productDetailsRatingBar;

    ImageView addToWishList;
    TextView productDetailsDescription;
//  TextView productDetailsInStock;
//  TextView productDetailsSold;
    MaterialSpinner mMaterialSpinnerSpinner;

//    Button productDetailsReviews;
    MaterialButton productDetailsReviews;
    TextView productDetailsTotalPrice;
    private ExtendedFloatingActionButton addToCartFAB;

    TextView theColor;
    TextView theSize;

    private Product mProduct;
    private String mInstance;
    private String PRODUCT_ID;
    private ProductImageAdapter mProductImageAdapter;
    double mNewPrice = 0.0;
    private SizeAdapter mSizeAdapter;
    private ColorAdapter mColorAdapter;

    // show product detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];
    private int mDotsCount;

    private ViewModelHomeFragment mViewModelHomeFragment;
    private Cart mCart = new Cart();
    private ProductDetailCallback mProDetailsCallback = this;
    private ArrayList<Product> mWishLists = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();

    AdapterProduct mSimilarProductsAdapter;  // for similar products
    AdapterProduct mSellerRecommendationAdapter; // for products from the same seller
    AdapterProduct mUsersAlsoViewedAdapter; // for products from the same seller
    AdapterProductBT mBoughtTogetherProductsAdapter;    // for Bought together products

    LinearLayout boughtTogetherContainer;
//    LinearLayout sellerRecommendationContainer;
//    LinearLayout usersAlsoViewedContainer;

    TextView boughtTogetherValue;
    Button productDetailsAddAllToWishList;
    Button productDetailsAddAllToCart;

    // get see all
    private TextView usersAlsoViewedSeeAll;
    private TextView fromSellerSeeAll;
    private TextView similarProductsSeeAll;

    // get count, value and productIds from  AdapterProductBT
    HashMap<String, String> MBT = new HashMap<>();
    List<Product> BT = new ArrayList<>();
    private BoughtTogetherCallback mBoughtTogetherCallback = this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        init(view);
        setDots();
        setViewModel();
        getBundle();
        return view;
    }


    private void init(View view){

        topWord = view.findViewById(R.id.productDetailsTopWord);
//        topWord.getBackground().setAlpha(90);
        priceAfterDiscount = view.findViewById(R.id.priceAfterDiscount);
        priceBeforeDiscount = view.findViewById(R.id.priceBeforeDiscount);
        productDetailsRatingBar = view.findViewById(R.id.productDetailsRatingBar);

        addToWishList = view.findViewById(R.id.productDetailsAddWishList);
        productDetailsDescription = view.findViewById(R.id.productDetailsDescription);
        mMaterialSpinnerSpinner = view.findViewById(R.id.quantitySpinner);

        productDetailsReviews = view.findViewById(R.id.productDetailsReviews);
        productDetailsTotalPrice = view.findViewById(R.id.productDetailsTotalPrice);
        addToCartFAB = view.findViewById(R.id.productDetailsAddToCartFAB);

        theColor = view.findViewById(R.id.theColor);
        theSize = view.findViewById(R.id.theSize);

        // see all
        usersAlsoViewedSeeAll = view.findViewById(R.id.usersAlsoViewedSeeAll);
        fromSellerSeeAll = view.findViewById(R.id.fromSellerSeeAll);
        similarProductsSeeAll = view.findViewById(R.id.similarProductsSeeAll);


        // for boughtTogether
         boughtTogetherContainer = view.findViewById(R.id.boughtTogetherContainer);
         boughtTogetherValue = view.findViewById(R.id.boughtTogetherValue);
         productDetailsAddAllToWishList = view.findViewById(R.id.productDetailsAddAllToWishList);
         productDetailsAddAllToCart = view.findViewById(R.id.productDetailsAddAllToCart);

        // recyclerView for productImages
        RecyclerView recyclerView = view.findViewById(R.id.holderProductDetailsImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mProductImageAdapter = new ProductImageAdapter(this.getContext(), new ArrayList<ProductImage>());
        recyclerView.setAdapter(mProductImageAdapter);

        // recyclerView for sizes
        RecyclerView sizeRecyclerView = view.findViewById(R.id.holderSelectSize);
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSizeAdapter = new SizeAdapter(this.getContext(), new ArrayList<ProductImage>(), this);
        sizeRecyclerView.setAdapter(mSizeAdapter);

        // recyclerView for color
        RecyclerView colorRecyclerView = view.findViewById(R.id.holderSelectColor);
        colorRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorAdapter = new ColorAdapter(this.getContext(), new ArrayList<ProductImage>(), this);
        colorRecyclerView.setAdapter(mColorAdapter);

        // dots count
        mDotsLayout = view.findViewById(R.id.productDetailsImageCount);


        // for users also viewed
        RecyclerView recyclerViewUsersAlsoViewed = view.findViewById(R.id.holderUsersAlsoViewed);
        recyclerViewUsersAlsoViewed.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mUsersAlsoViewedAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        recyclerViewUsersAlsoViewed.setAdapter(mUsersAlsoViewedAdapter);

        // recyclerView for similar products
        // make it grid
        RecyclerView recyclerViewSimilarProducts = view.findViewById(R.id.holderSimilarProducts);
        recyclerViewSimilarProducts.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSimilarProductsAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        recyclerViewSimilarProducts.setAdapter(mSimilarProductsAdapter);

        // recyclerView for seller recommended products
        RecyclerView recyclerViewSellerRecommended = view.findViewById(R.id.holderSellerRecommendation);
        recyclerViewSellerRecommended.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSellerRecommendationAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        recyclerViewSellerRecommended.setAdapter(mSellerRecommendationAdapter);


        // recyclerView for bought together products
        RecyclerView recyclerViewBoughtTogether = view.findViewById(R.id.holderBoughtTogether);
        recyclerViewBoughtTogether.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mBoughtTogetherProductsAdapter = new AdapterProductBT(this.getContext(), new ArrayList<Product>(), this, this, this);
        recyclerViewBoughtTogether.setAdapter(mBoughtTogetherProductsAdapter);


    }


    private void setSimilarProductsViewModel(){
        Log.d(TAG, "setSimilarProductsViewModel: called");
        // get similar products from productGroup
        String[] productGroup = mProduct.getProductGroup().split(",");
        String similar = productGroup[2];
        String status = "similar";
        List<HashMap<String, String>> productList = new ArrayList<>();
        productList.add(hashObject(status, similar));
        Log.d(TAG, "setSimilarProductsViewModel: the code " + productList);

        similarProductsViewModel(productList);
    }


    private void setBoughtTogetherViewModel(){
        String[] productGroup = mProduct.getProductGroup().split(",");
        String boughtTogether = productGroup[3];

        HashMap<String, String> obj = new HashMap<>();
        obj.put("boughtTogether", boughtTogether);

        List<HashMap<String, String>> productList = new ArrayList<>();
        productList.add(obj);

        Log.d(TAG, "setBoughtTogetherViewModel: bought together " + productList);

        mViewModelHomeFragment.initBoughtTogether(productList);
        mViewModelHomeFragment.getBoughtTogether().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {

                if(checkBoughtTogether(products.size() != 0)){
                    ArrayList<Product> pro = new ArrayList<>();
                    pro.add(mProduct);
                    pro.addAll(products);

                    BT = pro;
                    MBT = mBoughtTogetherProductsAdapter.loadNewData(pro);
                    String count = MBT.get("count");
                    String value = MBT.get("value");
                    productDetailsAddAllToWishList.setText(String.format(Locale.US, "Add all %s to list", count));
                    productDetailsAddAllToCart.setText(String.format(Locale.US, "Add all %s to cart", count));
                    boughtTogetherValue.setText(String.format(Locale.US, "GH¢ %s", value));
                }
            }
        });
    }

    private void setSellerRecommendationViewModel(String sellerId){
        List<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", "seller");
        obj.put("part", "1");
        obj.put("other", sellerId);
        list.add(obj);
        sellerRecommendationViewModel(list);
    }

    private void setUserAlsoViewedViewModel(String subCategory){
        List<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", "viewed");
        obj.put("part", "1");
        obj.put("other", subCategory);
        list.add(obj);
        usersAlsoViewedViewModel(list);
    }

    private boolean checkBoughtTogether(boolean notEmpty){
        if(notEmpty){ boughtTogetherContainer.setVisibility(View.VISIBLE); }
        return notEmpty;
    }

    private void similarProductsViewModel(List<HashMap<String, String>> body){
            mViewModelHomeFragment.initSimilarProducts(body);
            mViewModelHomeFragment.getSimilarProducts().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    if(products != null && products.size() != 0){

                        mSimilarProductsAdapter.loadNewData(products);
//                        mSimilarProductsAdapter.loadNewData(removeCurrentProductFromList(products));
                    } else {similarProductsSeeAll.setVisibility(View.GONE); }
                }
            });
    }

    private void wishListSimilarProductsViewModel(List<HashMap<String, String>> body){
//        if(MainActivity.mTrackMain == MainActivity.TrackMain.WISHLIST){
//            // similar products for home products
//            mViewModelHomeFragment.initSimilarWProducts(body);
//            mViewModelHomeFragment.getSimilarWProducts().observe(this, new Observer<List<Product>>() {
//                @Override
//                public void onChanged(List<Product> products) {
//                    Log.d(TAG, "onChanged: similar products baby " + products);
//                    mSimilarProductsAdapter.loadNewData(products);
//                }
//            });
//        }
    }

    private void sellerRecommendationViewModel(List<HashMap<String, String>> body){
        mViewModelHomeFragment.initSellerRecommendation(body);
        mViewModelHomeFragment.getSellerRecommendation().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                if(productList != null && productList.size() != 0){
                    mSellerRecommendationAdapter.loadNewData(productList);
//                    mSellerRecommendationAdapter.loadNewData(removeCurrentProductFromList(productList));
                } else { fromSellerSeeAll.setVisibility(View.GONE); }
            }
        });
    }

    private void usersAlsoViewedViewModel(List<HashMap<String, String>> body){
        mViewModelHomeFragment.initUsersAlsoViewed(body);
        mViewModelHomeFragment.getUsersAlsoViewed().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                if(null != productList && productList.size() != 0){
                    mUsersAlsoViewedAdapter.loadNewData(productList);
//                    mUsersAlsoViewedAdapter.loadNewData(removeCurrentProductFromList(productList));
                } else { usersAlsoViewedSeeAll.setVisibility(View.GONE); }
            }
        });
    }

    private List<Product> removeCurrentProductFromList(List<Product> products){
            for(Product pr : products){
                if(pr.get_id().contains(PRODUCT_ID)){
                    products.remove(pr);
                }
            }
        return products;
    }

    private HashMap<String, String> hashObject(String status, String other){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", status);
        obj.put("part", "1");
        obj.put("other", other);
        return obj;
    }

    @Override
    public void selectSize(String sizeNumber) {
        Toast.makeText(getContext(),sizeNumber,Toast.LENGTH_SHORT).show();
        theSize.setText(sizeNumber);
        mCart.setSize(sizeNumber);
    }

    @Override
    public void selectColor(String colorName) {
        Toast.makeText(getContext(),colorName,Toast.LENGTH_SHORT).show();
        theColor.setText(colorName);
        mCart.setColor(colorName);
    }


    // from ProductDetailCallback starts
    @Override
    public void addToCart(Cart cart) {
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart);
        mViewModelHomeFragment.cartToCart(cartList);
    }

    @Override
    public void checkProductReview(String productId) {
        Toast.makeText(getContext(),"check review ?", Toast.LENGTH_SHORT).show();
        ViewModelHomeFragment.productReviewId = productId;
        mViewModelHomeFragment.setProductReviewNull();
        loadReviewDialog();
    }


    @Override
    public void onUsersAlsoViewedSeeAll() {
        // searching from this productsFragment should be as from home
        loadProducts("viewed");
        MainActivity.searchRef = "home";
        MainActivity.searchOther = "viewed";
    }

    @Override
    public void onFromSellerSeeAll() {
        // searching from this productsFragment should be as from home
        String sellerId = mProduct.getSellerId();
        loadProducts("seller");
        MainActivity.searchRef = "home";
        MainActivity.searchOther = sellerId;
    }

    @Override
    public void onSimilarProductsSeeAll() {
        // searching from this productsFragment should be as from home
        String[] productGroup = mProduct.getProductGroup().split(",");
        String similar = productGroup[2];

        loadProducts("similar");
        MainActivity.searchRef = "home";
        MainActivity.searchOther = similar;
    }

    // from ProductDetailCallback ends

    // from productClickCallback
    @Override
    public void productClickCallback(Product product) {
        // product details can be opened in productDetailsContainer when called from homeFragment or
        loadProductDetailsFromHome(product);
        // wishListDetailsContainer when called from wishListFragment
        loadProductDetailsFromWishList(product);
    }

    private void loadProductDetailsFromHome(Product product){
        if(MainActivity.mTrackMain == MainActivity.TrackMain.HOME){
            MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCT_DETAILS;
            MainActivity.stepOne = true;
            MainActivity.mStepsTracking = MainActivity.Steps.STEP_ONE;

            // open product details
            Bundle arguments = new Bundle();
            arguments.putSerializable(Product.class.getSimpleName(), product);

            ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
            productDetailsFragment.setArguments(arguments);
            loadProductDetailsFragmentHome(productDetailsFragment);
        }
    }

    private void loadProductDetailsFromWishList(Product product) {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.WISHLIST){
            // open product details
            Bundle arguments = new Bundle();
            arguments.putSerializable(Product.class.getSimpleName(), product);
            ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
            productDetailsFragment.setArguments(arguments);
            loadProductDetailsFragmentWishList(productDetailsFragment);
        }
    }


    // from Add featured product callback
    @Override
    public void addFpToWishList(Product product, AdapterProduct.ViewHolder holder) {
        if(userIsSignedIn()){
            holder.homeFpAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
            setWishList(product.get_id());
        } else { showSignIn(); }
    }

    // from  CallCartAddFromHomeCallback
    @Override
    public void callCartAddDialog(Product product) {
//        Toast.makeText(getContext(), "Calling cart", Toast.LENGTH_SHORT).show();
        ViewModelHomeFragment.productDetails = product;
        loadCartAddDialog();
    }

    private void loadCartAddDialog(){
        FragmentManager fm = getFragmentManager();
        CartAddDialog cd = CartAddDialog.getInstance();
        cd.setTargetFragment(ProductDetailsFragment.this, 300);
        assert fm != null;
        cd.show(fm, "CartAddDialog");
    }

    // from AddBtToWishList
    @Override
    public void addBTToWishList(Product product, AdapterProductBT.ViewHolder holder) {
//        if(userIsSignedIn()){
            holder.homeFpAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
            setWishList(product.get_id());
//        } else { showSignIn(); }
    }

    private void loadProducts(String seeAllString){
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerProductDetails).setVisibility(View.GONE);
        }
        // call products in parts
        MainActivity.seeAll = seeAllString;
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        MainActivity.mStepsTracking = MainActivity.Steps.STEP_TWO;
        ProductsFragment productsFragment = new ProductsFragment();
        loadProductsFragment(productsFragment);
        Log.d(TAG, "loadProducts: MainActivity.seeAll value is " + MainActivity.seeAll);
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();
        if(fragment != null && getActivity() != null){
            findHomeFrameLayout().setVisibility(View.GONE);
            findProductDetailsFrameLayout().setVisibility(View.GONE);
            findProductFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void loadProductDetailsFragmentHome(Fragment fragment){
        if(fragment != null && getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProductDetails, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    private void loadProductDetailsFragmentWishList(Fragment fragment){
        if(fragment != null && getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerWishListDetails, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    private void loadReviewDialog(){
        FragmentManager fm = getFragmentManager();
        ReviewDailog rd = ReviewDailog.getInstance();
//        rd.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen); // for fullscreen
        rd.setTargetFragment(ProductDetailsFragment.this, 300);
        assert fm != null;
        rd.show(fm, "Review Dialog");
    }


    // for the similar products
    private HashMap<String, String> createWishList(String productId){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", productId);
        obj.put("customerId", mCustomerId);
        obj.put("thirdPartyId", "");
        return obj;
    }

    private HashMap<String, String> createCart(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", PRODUCT_ID);
        obj.put("customerId", mCustomerId);
        obj.put("quantity", mCart.getQuantity());
        obj.put("color", mCart.getColor());
        obj.put("size", mCart.getSize());
        obj.put("userGpsLocation", mCart.getUserGpsLocation());
        obj.put("thirdPartyId", "");
        return obj;
    }

    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }

    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null && !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }

    private void setViewModel(){
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();
    }

    // for similar products
    private void setWishList(final String productId){
        if(userIsSignedIn()){
            if(getActivity() != null){
                // make addWishList add color onClick
                if(PRODUCT_ID.equals(productId)){ addToWishList.setImageResource(R.drawable.ic_baseline_favorite_24); }
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

    private View.OnClickListener createWishListListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWishList(PRODUCT_ID);
            }
        };
    }

    private View.OnClickListener createCartListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIsSignedIn()){
                    if(colorAndSizeSelected()){
                        showProgressDialog();
                        mViewModelHomeFragment
                                .createCart(createCart())
                                .observe(Objects.requireNonNull(getActivity()), new Observer<Cart>() {
                                    @Override
                                    public void onChanged(Cart cart) {
                                        mProDetailsCallback.addToCart(cart);
                                        mViewModelHomeFragment.mCreateCart = new MutableLiveData<>();
                                        dismissProgressDialog();
                                    }
                                });
                    } else { infoDialog("To add product to cart, please select product size and color"); }
                } else {
                    showSignIn();
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

    private View.OnClickListener productReviewListener(final String productId){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProDetailsCallback.checkProductReview(productId);
                Toast.makeText(getContext(), PRODUCT_ID, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void selectQuantity(){
        // when product quantity is selected from the spinner
        mMaterialSpinnerSpinner.setItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String quantity = parent.getItemAtPosition(position).toString();
                mCart.setQuantity(quantity);
                Double total = mNewPrice * Double.parseDouble(quantity);
                productDetailsTotalPrice.setText(String.format(Locale.US, "GH¢ %.2f", total));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCart.setQuantity("1");
            }
        });
    }

    private boolean colorAndSizeSelected(){
        boolean selected = true;
        if(mCart.getColor().isEmpty() || mCart.getSize().isEmpty()){
            selected = false;
        }
        return selected;
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

    private void getBundle(){
        Bundle arguments = getArguments();
        if(arguments != null){
            mProduct = (Product) arguments.getSerializable(Product.class.getSimpleName());
            mInstance = arguments.getString("instance");
            if(mProduct != null){
                PRODUCT_ID = mProduct.get_id();
                int stock = Integer.parseInt(mProduct.getStock());

                loadNewData();
                setValues();
                setArrayAdapters(stock);

                // set viewModels dependent of the availability of products
                setBoughtTogetherViewModel();
                setSimilarProductsViewModel();
                setSellerRecommendationViewModel(mProduct.getSellerId());
                setUserAlsoViewedViewModel(mProduct.getSubCategory());
            }
        }
    }


    private void loadNewData(){
        ArrayList<ProductImage> photoList = new ArrayList<>();
        ArrayList<ProductImage> sizeList = new ArrayList<>();
        ArrayList<ProductImage> colorList = new ArrayList<>();

        for(String str : mProduct.getImages()){
            ProductImage productImage = new ProductImage(str);
            photoList.add(productImage);
        }

        String[] sizes = mProduct.getAvailSize().split(",");
        for(String str : sizes){
            sizeList.add(new ProductImage(str));
        }

        String[] colors = mProduct.getAvailColors().split(",");
        for(String str : colors){
            colorList.add(new ProductImage(str));
        }

        // load new data
        mProductImageAdapter.loadNewData(photoList);
        mSizeAdapter.loadNewData(sizeList);
        mColorAdapter.loadNewData(colorList);
    }

    private void setValues(){
        // get number out of discount
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(mProduct.getDiscount());
        String num = "";
        while (m.find()){ num = m.group(); }

        // get the new price
        double oldPrice = Double.parseDouble(mProduct.getPrice());
        double disD = Double.parseDouble(num);
        double newPrice = (100 - disD) * oldPrice / 100.00;
        mNewPrice = newPrice;

        String lastThreeOfProductId = PRODUCT_ID.substring(PRODUCT_ID.length() - 4);
        topWord.setText(String.format(Locale.US, "%s - %s", mProduct.getName(), lastThreeOfProductId));
        priceAfterDiscount.setText(String.format(Locale.US, "GH¢ %.2f", mNewPrice));
        priceBeforeDiscount.setText(String.format(Locale.US, "GH¢ %.2f", Double.parseDouble(mProduct.getPrice())));

        productDetailsDescription.setText(mProduct.getAbout());
        String quantity = String.valueOf(mProduct.getSales()[0]); //quantity sold
//      productDetailsSold.setText(String.format(Locale.US,"%s orders",quantity));
//      productDetailsInStock.setText(mProduct.getStock());
        productDetailsTotalPrice.setText(String.format(Locale.US,"GH¢ %.2f",newPrice));

    }

    private void setArrayAdapters(int stock){
        // user cannot select more than 30
        ArrayList<String> quantity = new ArrayList<>();
        for(int j=0;j<stock; j++){
            if(j > 29){ break; }
            quantity.add(String.valueOf(j + 1));
        }
        // convert arrayList to array
        String[] QuantityAdd = new String[quantity.size()];
        String[] quantitySelect = quantity.toArray(QuantityAdd);

        ArrayAdapter<String> quantitySpinner = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), android.R.layout.simple_spinner_item, quantitySelect);
        quantitySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaterialSpinnerSpinner.setAdapter(quantitySpinner);
        mMaterialSpinnerSpinner.setError("Please select quantity");
        mMaterialSpinnerSpinner.setLabel("Quantity");
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
            addToWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }



    // from BoughtTogetherCallback
    @Override
    public void addAllToWishList(List<Product> productList) {
//        setImageResource(R.drawable.ic_baseline_favorite_24);
        mViewModelHomeFragment.createWishLists(proToList(productList)).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                mViewModelHomeFragment.productToWishList(productList);
            }
        });
    }

    @Override
    public void addAllToCart(List<Product> productList) {
        // when adding all to cart select automatically
        // first color and size, and 1 as quantity

    }

    private List<HashMap<String, String>> proToList(List<Product> productList){
        List<HashMap<String, String>> list = new ArrayList<>();
        for(Product pro : productList){
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("productId", pro.get_id());
            hashMap.put("customerId", mCustomerId);
            hashMap.put("thirdPartyId", "");
            list.add(hashMap);
        }
        return list;
    }

    private View.OnClickListener boughtTogetherAddAllToWishList(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoughtTogetherCallback.addAllToWishList(BT);
            }
        };
    }

    private View.OnClickListener boughtTogetherAddAllToCart(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoughtTogetherCallback.addAllToCart(BT);
            }
        };
    }

    private View.OnClickListener usersViewedListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProDetailsCallback.onUsersAlsoViewedSeeAll();
            }
        };
    }
    private View.OnClickListener similarProductsListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProDetailsCallback.onSimilarProductsSeeAll();
            }
        };
    }
    private View.OnClickListener fromSellerListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProDetailsCallback.onFromSellerSeeAll();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        topWord.setOnClickListener(GoBackListener());
        addToWishList.setOnClickListener(createWishListListener());
        addToCartFAB.setOnClickListener(createCartListener());
        productDetailsReviews.setOnClickListener(productReviewListener(PRODUCT_ID));
        selectQuantity();
        setAddWIshListColor(productIsPartOfWishList(PRODUCT_ID));

        // for boughtTogether
        productDetailsAddAllToWishList.setOnClickListener(boughtTogetherAddAllToWishList());
        productDetailsAddAllToCart.setOnClickListener(boughtTogetherAddAllToCart());

        usersAlsoViewedSeeAll.setOnClickListener(usersViewedListener());
        fromSellerSeeAll.setOnClickListener(fromSellerListener());
        similarProductsSeeAll.setOnClickListener(similarProductsListener());
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: called");
        topWord.setOnClickListener(null);
        addToWishList.setOnClickListener(null);
        addToCartFAB.setOnClickListener(null);
        productDetailsReviews.setOnClickListener(null);

        // for boughtTogether
        productDetailsAddAllToWishList.setOnClickListener(null);
        productDetailsAddAllToCart.setOnClickListener(null);

        // see all
        usersAlsoViewedSeeAll.setOnClickListener(null);
        fromSellerSeeAll.setOnClickListener(null);
        similarProductsSeeAll.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        setVisibility();
    }

    private View.OnClickListener GoBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
            }
        };
    }

    private void setVisibility(){
        // reset values
        mViewModelHomeFragment.resetBoughtTogether();
        mViewModelHomeFragment.resetUserAlsoViewed();
        mViewModelHomeFragment.resetSellerRecommendation();
        mViewModelHomeFragment.resetSimilarProducts();

        if(getActivity() != null){
            if(MainActivity.mTrackMain == MainActivity.TrackMain.HOME && MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCT_DETAILS){
                Log.d(TAG, "setVisibility: the main is the main shit buddy");
                getActivity().findViewById(R.id.containerProductDetails).setVisibility(View.GONE);
                stepOneVisibility();
                stepTwoVisibility();
                stepThreeVisibility();
                removeThisFragmentFromHome();
            }
            productDetailsFromWishList();
        }
    }

    private void stepOneVisibility(){

        if(MainActivity.mStepsTracking == MainActivity.Steps.STEP_ONE){
            // reset similar products
//            mViewModelHomeFragment.resetSimilarProducts();

            MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
            Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.containerProducts).setVisibility(View.GONE);
            Log.d(TAG, "stepOneVisibility: called");
        }
    }
    private void stepTwoVisibility(){
        if(MainActivity.mStepsTracking == MainActivity.Steps.STEP_TWO){
            // reset similar products
//            mViewModelHomeFragment.resetSimilarProducts();

            MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
            Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerProductReview).setVisibility(View.GONE);
            Log.d(TAG, "stepTwoVisibility: called");
            Log.d(TAG, "stepTwoVisibility:  Track main is " + MainActivity.mTrackMain);
        }

    }

    private void stepThreeVisibility(){
        if( MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCT_DETAILS){
            if(MainActivity.mStepsTracking == MainActivity.Steps.STEP_THREE){
                // reset similar products
//                mViewModelHomeFragment.resetSimilarProducts();

                MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
                Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.GONE);
                getActivity().findViewById(R.id.containerProductReview).setVisibility(View.GONE);
                Log.d(TAG, "stepThreeVisibility: called");
                Log.d(TAG, "stepThreeVisibility: Track main is " + MainActivity.mTrackMain);
            }
        }
    }


    private void productDetailsFromWishList(){
        // if product details was opened from wishListFragment
        if( MainActivity.mTrackMain == MainActivity.TrackMain.WISHLIST &&
                MainActivity.mWishListTrack == MainActivity.WishListTrack.WISH_LIST_DETAILS){
            // reset similar products
//            mViewModelHomeFragment.resetSimilarWProducts();

            Log.d(TAG, "setVisibility: wish back");
            MainActivity.mWishListTrack = MainActivity.WishListTrack.WISH_LIST;
            Objects.requireNonNull(getActivity()).findViewById(R.id.containerWishListDetails).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerWishList).setVisibility(View.VISIBLE);
            removeThisFragmentFromWishDetails();
        }
    }

    private void removeThisFragmentFromHome(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProductDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                Log.d(TAG, "removeThisFragmentFromHome: called");
            }
        }
    }

    private void removeThisFragmentFromWishDetails(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerWishListDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
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
                mProgressDialog.dismiss(); }
        }
    }

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProductDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private View findHomeFrameLayout(){ return getActivity().findViewById(R.id.containerHomeFragment); }
    private View findProductFrameLayout(){ return getActivity().findViewById(R.id.containerProducts); }
    private View findProductDetailsFrameLayout(){ return getActivity().findViewById(R.id.containerProductDetails); }



}
