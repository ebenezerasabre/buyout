package asabre.com.buyout.view.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Category;
import asabre.com.buyout.service.model.Customer;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.service.model.UserEntity;
import asabre.com.buyout.service.repository.DatabaseClient;
import asabre.com.buyout.view.adapter.CategoryAdapterCircular;
import asabre.com.buyout.view.callback.AddFpToWishListCallback;
import asabre.com.buyout.view.callback.AddTrendingToWishListCallback;
import asabre.com.buyout.view.callback.CallCartAddFromHomeCallback;
import asabre.com.buyout.view.callback.FilterDialogCallback;
import asabre.com.buyout.view.callback.HomeFragmentCallback;
import asabre.com.buyout.view.callback.ProductClickCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;


public class HomeFragment extends Fragment implements
        HomeFragmentCallback,
        ProductClickCallback,
        FilterDialogCallback,
        AddTrendingToWishListCallback,
        AddFpToWishListCallback,
        CallCartAddFromHomeCallback {
    private static final String TAG = HomeFragment.class.getSimpleName();


    // get recyclerView
    private RecyclerView mRecyclerViewCategories;
    private ImageView homeFilter;
    private SearchView mHomeSearchView;

    //  get adapters
    private CategoryAdapterCircular mCategoryAdapterCircular;
//    private ProductImageAdapter mProductImageAdapter;
//    private TrendingProductsAdapter mTrendingProductsAdapter;
    private AdapterTrendingProducts mAdapterTrendingProducts;

//    private CategoryAdapter mCategoryAdapter;
//    private FeaturedProductsAdapter mHotSalesAdapter;
//    private FeaturedProductsAdapter mFeaturedProductsAdapter;
//    private FeaturedProductsAdapter mHistoryAdapter;


    private AdapterProduct mHotSalesAdapter;
    private AdapterProduct mFeaturedProductsAdapter;
    private AdapterProduct mHistoryAdapter;

    private HomeFragmentCallback mHomeFragmentCallback = this;
    private FilterDialogCallback mFilterDialogCallback = this;

    // get viewModel
    private ViewModelHomeFragment mViewModelHomeFragment;

    String mCustomerId = "";

//    private RelativeLayout seeAllFeaturedProducts;
//    private RelativeLayout seeAllHotSales;
//    private RelativeLayout seeAllHistory;
//    private RelativeLayout seeAllCategories;

    private TextView getAllCategories;
    private TextView getAllTrending;
    private TextView getAllHotSales;
    private TextView getAllFeatured;
    private TextView getAllHistory;
    private ExtendedFloatingActionButton homeExploreFAB;


    // track which status was clicked eg product, hotsales,history
    /**
     * Save the trackStatus on saveInstanceState
     */

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: homeFragment create view");
        View view = inflater.inflate(R.layout.fragment_home1, container, false);
        init(view);
        setHomeFragmentViewModel();
        getCustomerData();
//        loadFragLater();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        createNotificationChannel();
//        createNotification();
    }


    // creating notification

    String CHANNEL_ID = "112233";
    private void createNotification(){
        Log.d(TAG, "createNotification: called");
        if(getContext() != null){
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_favorite_purple_end_24dp)
//                    .setContentTitle("The title here")
//                    .setContentText("Trying to put some content here to see how it all comes together")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            mBuilder.build();

            NotificationManager notif = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder
                    (getContext())
                    .setContentTitle("my title")
                    .setContentText("My content body")
                    .setSmallIcon(R.drawable.ic_favorite_purple_end_24dp).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);

        }
    }

    private void createNotificationChannel(){
        Log.d(TAG, "createNotificationChannel: called");
        // create the notificationChannel but only on API 26+ because
        // the NotificationChannel is new and not in the supported library
        if(getActivity() != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = "No name";
                String description = "The description goes here";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: homeFragment start called");
        homeFilter.setOnClickListener(HomeFilter());
//        seeAllFeaturedProducts.setOnClickListener(getFeaturedProducts());
//        seeAllHotSales.setOnClickListener(getHotSales());
//        seeAllHistory.setOnClickListener(getHistory());
//        seeAllCategories.setOnClickListener(getCategories());

        getAllCategories.setOnClickListener(getCategories());
        getAllTrending.setOnClickListener(getTrendingProducts());
        getAllHotSales.setOnClickListener(getHotSales());
        getAllFeatured.setOnClickListener(getFeaturedProducts());
        getAllHistory.setOnClickListener(getHistory());
        homeExploreFAB.setOnClickListener(getExplore());

        userIsEdited();

        createNotificationChannel();
        createNotification();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
        homeFilter.setOnClickListener(null);
//        seeAllFeaturedProducts.setOnClickListener(null);
//        seeAllHotSales.setOnClickListener(null);
//        seeAllHistory.setOnClickListener(null);
//        seeAllCategories.setOnClickListener(null);

        getAllCategories.setOnClickListener(null);
        getAllTrending.setOnClickListener(null);
        getAllHotSales.setOnClickListener(null);
        getAllFeatured.setOnClickListener(null);
        getAllHistory.setOnClickListener(null);
        homeExploreFAB.setOnClickListener(null);
    }

    private View.OnClickListener getFeaturedProducts(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onFeaturedProductsSeeAllClick();
            }
        };
    }

    private View.OnClickListener getHotSales(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onHotSalesSeeAllClick();
            }
        };
    }

    private View.OnClickListener getTrendingProducts(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onTrendingSeeAllClick();
            }
        };
    }

    private View.OnClickListener getHistory(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onHistorySeeAllClick();
            }
        };
    }

    private View.OnClickListener getCategories(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onCategorySeeAllClick();
            }
        };
    }

    private View.OnClickListener getExplore(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onExploreClick();
            }
        };
    }


    private View.OnClickListener HomeFilter(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeDialog();
            }
        };
    }

    private void homeDialog(){
        if(getContext() != null){
             String[] options;
            // check if user is signed-in
            if(ViewModelHomeFragment.userEntity != null){
                options = new String[]{"Sign-out", "Help and tutorials", "Product of the day", "News", "About us"};
            } else {
                options = new String[]{"Sign-in", "Help and tutorials", "Product of the day", "News", "About us"};
            }
            final String[] finalOptions = options;
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Welcome from ewQuest")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFilterDialogCallback.filterDialogSelect(finalOptions[which]);
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }

    @Override
    public void filterDialogSelect(String filterName) {
        switch (filterName){
            case "Sign-in":
                showSignIn();
                break;
            case "Sign-out":
                showSignOut();
                break;
            case "Help and tutorials":
                // get tutorials
                break;
            case "Product of the day":
                showProductOfTheDay();
                break;
            case "News":
                showNews();
                break;
            case "About us":
                showAboutUs();
                break;
            default:
                // do nothing
                break;
        }
    }

    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }
    private void showSignOut(){
        signCustomerOut();
    }

    private void showNews(){
        NewsFragment newsFragment = new NewsFragment();
        loadProductsFragment(newsFragment);
    }

    private void showProductOfTheDay(){
        ProductOfTheDayFragment productOfTheDayFragment = new ProductOfTheDayFragment();
        loadProductsFragment(productOfTheDayFragment);
    }

    private void showAboutUs(){
        AboutUsFragment aboutUsFragment = new AboutUsFragment();
        loadProductsFragment(aboutUsFragment);
    }


    private void signCustomerOut(){
        class SignCustomerOut extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                // clear database
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .deleteAll();
                // reset
                resetViewModelArguments();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadHomeFragment();
                loadFragments();
            }
        }
        SignCustomerOut signCustomerOut = new SignCustomerOut();
        signCustomerOut.execute();
    }

    private void resetViewModelArguments(){
        ViewModelHomeFragment.userEntity = null;
        ViewModelHomeFragment.wishListIncrease = 1;
        mViewModelHomeFragment.setWishListNull();
        mViewModelHomeFragment.setCartNull();
        mViewModelHomeFragment.setOrdersNull();
        mViewModelHomeFragment.resetCustomerAddress();
        Log.d(TAG, "resetViewModelArguments: resetting");
    }

    private void loadHomeFragment(){
        HomeFragment homeFragment = new HomeFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerHomeFragment, homeFragment)
                    .commit();
            Log.d(TAG, "loadHomeFragment: loading home fragments");
        }
    }

    private void signOutDialog(){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("You've signed out")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            loadHomeFragment();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("sign-in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }


    private void loadFragLater(){

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "run: a little late ");
//                Log.d(TAG, "run: ViewModel.userEntity " + ViewModelHomeFragment.userEntity.getFirstName());
//            }
//        }, 5000);
    }



    private void init(View view){

//        seeAllFeaturedProducts = view.findViewById(R.id.seeAllFeaturedProducts);
//        seeAllHotSales = view.findViewById(R.id.seeAllHotSales);
//        seeAllHistory = view.findViewById(R.id.seeAllHistory);
//        seeAllCategories = view.findViewById(R.id.seeAllCategories);

        getAllCategories = view.findViewById(R.id.getAllCategories);
        getAllTrending = view.findViewById(R.id.getAllTrending);
        getAllHotSales = view.findViewById(R.id.getAllHotSales);
        getAllFeatured = view.findViewById(R.id.getAllFeatured);
        getAllHistory = view.findViewById(R.id.getAllHistory);

        homeExploreFAB = view.findViewById(R.id.homeExploreFAB);
        homeFilter = view.findViewById(R.id.homeFilter);
        mHomeSearchView = view.findViewById(R.id.homeSearchView);

        // for circular category
        mCategoryAdapterCircular = new CategoryAdapterCircular(this.getContext(), new ArrayList<Category>(), this);
        RecyclerView recyclerViewCircular = view.findViewById(R.id.holderCat);
        recyclerViewCircular.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCircular.setAdapter(mCategoryAdapterCircular);

        // for categories
//        mCategoryAdapter = new CategoryAdapter(this.getContext(), new ArrayList<Category>(), this);
//        mRecyclerViewCategories = view.findViewById(R.id.holderCategoriesHome);
//        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
//        mRecyclerViewCategories.setAdapter(mCategoryAdapter);

        // for hot sales
        mHotSalesAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        RecyclerView recyclerViewHotSales = view.findViewById(R.id.holderHotSalesHome);
        recyclerViewHotSales.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHotSales.setAdapter(mHotSalesAdapter);

        // for featured products
        mFeaturedProductsAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        RecyclerView recyclerViewFeaturedProducts = view.findViewById(R.id.holderFeaturedProductsHome);
        recyclerViewFeaturedProducts.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFeaturedProducts.setAdapter(mFeaturedProductsAdapter);

        // for history
        mHistoryAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        RecyclerView recyclerViewHistory = view.findViewById(R.id.holderHistoryHome);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHistory.setAdapter(mHistoryAdapter);

        // for trending
        mAdapterTrendingProducts = new AdapterTrendingProducts(this.getContext(), new ArrayList<Product>(), this, this, this);
        RecyclerView recyclerViewTrending = view.findViewById(R.id.holderTrending);
        recyclerViewTrending.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTrending.setAdapter(mAdapterTrendingProducts);

        setHomeSearchView();

    }

    private HashMap<String, String> hashObject(String status){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", status);
        obj.put("part", "1");
        obj.put("other", status);
        return obj;
    }

    private void setHomeFragmentViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
            mViewModelHomeFragment.initCategories();

            List<HashMap<String, String>> productList = new ArrayList<>();
            productList.add(hashObject("product"));
            mViewModelHomeFragment.initProducts(productList);

            List<HashMap<String, String>> hotSalesList = new ArrayList<>();
            hotSalesList.add(hashObject("hotsales"));
            mViewModelHomeFragment.initHotSales(hotSalesList);

            List<HashMap<String, String>> trendingProductList = new ArrayList<>();
            trendingProductList.add(hashObject("trending"));
            mViewModelHomeFragment.initTrendingProducts(trendingProductList);

            /**
             * Even though explore is not shown on the homeFragment page it is downloaded in the background
             * So that I can set MainActivity.searchRef when explore is clicked to 'status'
             * This in turn will make searching possible when explore is clicked
             * Note -- To be able to search, the searchRef must be set to
             * Home, category or status
             * If explore is not downloaded in the background it has to be downloaded on productFragment call
             * Which we need that the MainActivity.searchRef be set to explore, making further searching
             * complex. Hence download explore in background and productFragment will only observe
             */

            // for explore
            List<HashMap<String, String>> exploreList = new ArrayList<>();
            exploreList.add(hashObject("explore"));
            mViewModelHomeFragment.initExploreProducts(exploreList);


            // for Categories adapter
            mViewModelHomeFragment.getCategoriesObservable().observe(this, new Observer<List<Category>>() {
                @Override
                public void onChanged(List<Category> list) {
                    if(null != list && list.size() != 0){
                        getAllCategories.setVisibility(View.VISIBLE);
                        mCategoryAdapterCircular.loadNewData(list);
                    }

                }
            });

            // for hotsales
            mViewModelHomeFragment.getHotSales().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    if(null != products && products.size() != 0){
                        getAllHotSales.setVisibility(View.VISIBLE);
                        mHotSalesAdapter.loadNewData(products);
                        Log.d(TAG, "onChanged: hotsales in here");
                    }

                }
            });

            // for products
            mViewModelHomeFragment.getProducts().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    if(null != products && products.size() != 0){
                        getAllFeatured.setVisibility(View.VISIBLE);
                        mFeaturedProductsAdapter.loadNewData(products);
                        Log.d(TAG, "onChanged: products in here");
                    }

                }
            });

            mViewModelHomeFragment.getTrendingProducts().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    if(null != products && products.size() != 0){
                        getAllTrending.setVisibility(View.VISIBLE);
                        mAdapterTrendingProducts.loadNewData(products);
                    }

                }
            });
            // for explore
        }
    }

    private List<HashMap<String, String>> cusHistoryObj(){
        String customerId = ViewModelHomeFragment.userEntity == null ?
                "" : ViewModelHomeFragment.userEntity.getCustomerId();

        HashMap<String, String> obj = new HashMap<>();
        if(ViewModelHomeFragment.userEntity != null){
            obj.put("customerId", customerId);
            obj.put("part", String.valueOf(ViewModelHomeFragment.historyIncrease));
            Log.d(TAG, "cusHistoryObj: from history " + customerId);
            Log.d(TAG, "cusHistoryObj: history increase " + ViewModelHomeFragment.historyIncrease);
        }
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(obj);
//        ViewModelHomeFragment.historyIncrease += 1;
        return list;
    }

    private void setCustomerHistoryViewModel(){
        Log.d(TAG, "setCustomerHistoryViewModel: called");
        mViewModelHomeFragment.initCustomerHistory(cusHistoryObj());
        mViewModelHomeFragment.getCustomerHistory().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if(null != products && products.size() != 0){
                    getAllHistory.setVisibility(View.VISIBLE);
                    mHistoryAdapter.loadNewData(products);
                }

            }
        });

    }


    private void setHomeSearchView(){
        mHomeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               Toast.makeText(getContext(), query,Toast.LENGTH_SHORT).show();
               findHomeScrollView().setVisibility(View.VISIBLE);

               // TODO make the search post request here
                setUpSearchItem(query);
                loadProducts("search");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findHomeScrollView().setVisibility(View.INVISIBLE);
                return false;
            }
        });

        // set text focus handler
        mHomeSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: the focus has changed");
                // hide scrollView
                findHomeScrollView().setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpSearchItem(String query){
        MainActivity.searchRef = "home";
        MainActivity.searchOther = "all categories";
        mViewModelHomeFragment.setSearchNull();
        ViewModelHomeFragment.searchIncrease = 1;
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", MainActivity.searchRef);
        obj.put("part", String.valueOf(ViewModelHomeFragment.searchIncrease));
        obj.put("word", query);
        obj.put("other", "");
        MainActivity.search = obj;
    }


    // from ProductClickCallback
    @Override
    public void productClickCallback(Product product) {
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCT_DETAILS;
        MainActivity.stepOne = true;
        MainActivity.mStepsTracking = MainActivity.Steps.STEP_ONE;

        // open product details
        Bundle arguments = new Bundle();
        arguments.putSerializable(Product.class.getSimpleName(), product);
        arguments.putString("instance", "HOME");

        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(arguments);
        loadProductDetailsFragment(productDetailsFragment);
    }


    // from CallCartAddFromHomeCallback;
    @Override
    public void callCartAddDialog(Product product) {
        ViewModelHomeFragment.productDetails = product;
        loadCartAddDialog();
    }

    private void loadCartAddDialog(){
        FragmentManager fm = getFragmentManager();
        CartAddDialog cd = CartAddDialog.getInstance();
        cd.setTargetFragment(HomeFragment.this, 300);
        assert fm != null;
        cd.show(fm, "CartAddDialog");
    }

    // from AddWishListFromHomeCallback
    @Override
    public void addTrendingToWishList(Product product, AdapterTrendingProducts.ViewHolder holder) {
        if(userIsSignedIn()){
            holder.homeTrendAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
            setWishListViewModel(product.get_id());
        } else { showSignIn(); }
    }

    // from AddFpToWishListCallback (Fp - featured products)
    @Override
    public void addFpToWishList(Product product, AdapterProduct.ViewHolder holder) {
        if(userIsSignedIn()){
            holder.homeFpAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
            setWishListViewModel(product.get_id());
        } else { showSignIn(); }
    }

    private void setWishListViewModel(String productId){
        mViewModelHomeFragment
                .createWishList(createWishList(productId))
                .observe(Objects.requireNonNull(getActivity()), new Observer<Product>() {
                    @Override
                    public void onChanged(Product product) {
                        List<Product> products = new ArrayList<>();
                        products.add(product);
                        mViewModelHomeFragment.productToWishList(products);
                        mViewModelHomeFragment.mCreateWishList = new MutableLiveData<>();
                    }
                });
    }

    private HashMap<String, String> createWishList(String productId){
        mCustomerId = ViewModelHomeFragment.userEntity == null ?
                "" : ViewModelHomeFragment.userEntity.getCustomerId();

        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", productId);
        obj.put("customerId", mCustomerId);
        obj.put("thirdPartyId", "");
        Log.d(TAG, "createWishList: productId " + productId);
        Log.d(TAG, "createWishList: customerId is " + mCustomerId);
        return obj;
    }

    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null && !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }


    /*
        For the fields that are downloaded on homeFragment
     * The MainActivity.searchRef is only used during searching from that fields
        For the fields that are not download on homeFragment
         * The MainActivity.searchRef is used to download products when productFragment launches
         * ge trending, explore, their  * The MainActivity.searchRef has to be set to status to be able to search from them

         ** MainActivity.searchOther and MainActivity.seeAll remains constant
     */


    // callbacks from HomeFragmentCallback starts
    @Override
    public void onCategoryClick(String category) {
        if(!MainActivity.searchOther.equals(category.toLowerCase())){
            mViewModelHomeFragment.setCategoryProductsNull();
            ViewModelHomeFragment.categoryProductIncrease = 1;
        }
        MainActivity.searchOther = category.toLowerCase();
        MainActivity.searchRef = "category";
        CategoryParentFragment.selectedCategory = category;
//        MainActivity.nestFirstOrder = category.toLowerCase();

//        loadProducts("category");
        // i want to call nestedFragments instead of loading the products
        // of the clicked category

        loadNestedFragment();
    }

    @Override
    public void onCategorySeeAllClick() {
        MainActivity.seeAll = "category";
        MainActivity.searchRef = "category";
        Toast.makeText(getContext(), "All cats", Toast.LENGTH_SHORT).show();
        loadNestedFragment();
    }

    @Override
    public void onFeaturedProductsSeeAllClick() {
        loadProducts("product");
        MainActivity.searchRef = "status";
        MainActivity.searchOther = "product";

    }

    @Override
    public void onHotSalesSeeAllClick() {
        loadProducts("hotsales");
        MainActivity.searchRef = "status";
        MainActivity.searchOther = "hotsales";

    }

    @Override
    public void onTrendingSeeAllClick() {
        loadProducts("trending");
        MainActivity.searchRef = "status";
        MainActivity.searchOther = "trending";
    }

    @Override
    public void onHistorySeeAllClick() {
        loadProducts("history");
        MainActivity.searchRef = "history";
        MainActivity.searchOther = "history";
    }

    @Override
    public void onExploreClick() {
        loadProducts("explore");
        MainActivity.searchRef = "status";
        MainActivity.searchOther = "explore";
    }

    // callbacks from HomeFragmentCallback ends

    private void loadProducts(String seeAllString){
        // call products in parts
        MainActivity.seeAll = seeAllString;
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        MainActivity.mStepsTracking = MainActivity.Steps.STEP_TWO;
        ProductsFragment productsFragment = new ProductsFragment();
        loadProductsFragment(productsFragment);
        Log.d(TAG, "loadProducts: MainActivity.seeAll value is " + MainActivity.seeAll);
    }

    private void loadNestedFragment(){
        removePreviousNestedFragment();

        if(getActivity() != null){
            MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCT_REVIEW;
            MainActivity.mStepsTracking = MainActivity.Steps.STEP_THREE;
            findHomeFrameLayout().setVisibility(View.GONE);
            findProductReviewFrameLayout().setVisibility(View.VISIBLE);

            CategoryParentFragment categoryParentFragment = new CategoryParentFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProductReview, categoryParentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    private void removePreviousNestedFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProductReview);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }


    }


    // when user make changes in his info this method is called
    // to refresh the fragments, get the new info from the server
    // and save it
    private void userIsEdited(){

        Log.d(TAG, "userIsEdited: HomeFragment updatingUser observe is called");
        ViewModelHomeFragment.updatingUser.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "onChanged: observing from userIsEdited method");
                if(aBoolean){
                    getUserByNP();
                }
            }
        });

    }

    private void getUserByNP(){
        Log.d(TAG, "getUserByNP: HomeFragment called");
        String lastName = ViewModelHomeFragment.userEntity.getLastName();
        String phoneNumber = ViewModelHomeFragment.userEntity.getPhoneNumber();
        HashMap<String, String > editedObj = new HashMap<>();
        editedObj.put("lastName", lastName);
        editedObj.put("phoneNumber", phoneNumber);
        mViewModelHomeFragment.initUserIsEdited(editedObj);
        mViewModelHomeFragment.getUserIsEdited().observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(Customer customer) {
                Log.d(TAG, "onChanged: observing from getUserByNP method");
                saveCustomerData(setEntity(customer));
            }
        });
    }

    // method to save user data after user has edited his info
    private void saveCustomerData(final UserEntity entity){
        Log.d(TAG, "saveCustomerData: called here");
        class SaveCustomer extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

//                 clear database
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .deleteAll();

//                 save user info
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .insert(entity);

//                 add entity to viewModel
                ViewModelHomeFragment.userEntity = entity;

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(),"Saved edited customer data", Toast.LENGTH_SHORT).show();
                loadProfileFragment();
                ViewModelHomeFragment.updatingUser = new MutableLiveData<>();
                ViewModelHomeFragment.userIsEdited = new MutableLiveData<>();
            }
        }
        SaveCustomer saveCustomer = new SaveCustomer();
        saveCustomer.execute();
    }


    // method to retrieve user data from database when app is first switched on
    // else user has to sign-in or sign-up
    private void getCustomerData(){
        Log.d(TAG, "getCustomerData: starting customer data");

        if(ViewModelHomeFragment.userEntity == null){
            Log.d(TAG, "getCustomerData: ViewModelHomeFragment.userEntity == null");
            Log.d(TAG, "getCustomerData: running called ");
            class GetCustomerData extends AsyncTask<Void, Void, List<UserEntity>>{
                @Override
                protected List<UserEntity> doInBackground(Void... voids) {
                    Log.d(TAG, "doInBackground: started");
                    List<UserEntity> customerList = DatabaseClient
                            .getInstance(getContext())
                            .getAppDatabase()
                            .mUserDao()
                            .getAll();
                    return customerList;
                }
                @Override
                protected void onPostExecute(List<UserEntity> userEntities) {
                    super.onPostExecute(userEntities);
                    Log.d(TAG, "onPostExecute: called");
                    if(userEntities.size() != 0) {
                        UserEntity entity = userEntities.get(0);

                        Log.d(TAG, "onPostExecute: the firsName was " + entity.getFirstName());
                        ViewModelHomeFragment.userEntity = entity;
                        mCustomerId = ViewModelHomeFragment.userEntity.getCustomerId();
                        loadFragments();
                    } else { homeDialog(); }
                }
            }
            GetCustomerData gc = new GetCustomerData();
            gc.execute();
        } else {
            Log.d(TAG, "getCustomerData: entity is not null ");
            loadFragments();
        }
    }

    private void hideVirtualKeyboard(){
        if(getActivity() != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private UserEntity setEntity(Customer customer){
        return new UserEntity(
                customer.get_id(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getUserImage()
        );
    }

    private void loadFragments(){
        loadWishListFragment();
        loadCartFragment();
        loadOrderFragment();
        loadProfileFragment();
        setCustomerHistoryViewModel();
    }

    private void loadWishListFragment(){
        Log.d(TAG, "loadWishListFragment: called");
        WishListFragment wishListFragment = new WishListFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerWishList, wishListFragment)
                    .commit();
        }
    }
    private void loadCartFragment(){
        Log.d(TAG, "loadCartFragment: called");
        CartFragment cartFragment = new CartFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerCart, cartFragment)
                    .commit();
        }
    }

    private void loadOrderFragment(){
        Log.d(TAG, "loadOrderFragment: called");
        OrderFragment orderFragment = new OrderFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerOrder, orderFragment)
                    .commit();
        }
    }

    private void loadProfileFragment(){
        Log.d(TAG, "loadProfileFragment: called");
        ProfileFragment profileFragment = new ProfileFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerProfile, profileFragment)
                    .commit();
        }
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;

        if(fragment != null && getActivity() != null){
            findHomeFrameLayout().setVisibility(View.GONE);
            findProductFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void loadProductDetailsFragment(Fragment fragment){
        removePreviousProductDetailsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findHomeFrameLayout().setVisibility(View.GONE);
            findProductDetailsFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProductDetails, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousProductDetailsFameLayoutData(){
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

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private View findHomeFrameLayout(){ return getActivity().findViewById(R.id.containerHomeFragment);}
    private View findProductDetailsFrameLayout(){ return getActivity().findViewById(R.id.containerProductDetails);}
    private View findProductFrameLayout(){ return getActivity().findViewById(R.id.containerProducts);}
    private View findHomeScrollView(){ return getActivity().findViewById(R.id.homeScrollView);}
    private View findProductReviewFrameLayout(){ return getActivity().findViewById(R.id.containerProductReview); }




}





