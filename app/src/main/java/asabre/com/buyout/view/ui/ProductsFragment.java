package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.callback.AddFpToWishListCallback;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.view.callback.CallCartAddFromHomeCallback;
import asabre.com.buyout.view.callback.ProductClickCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProductsFragment extends Fragment implements
        BaseFragment,
        ProductClickCallback,
        AddFpToWishListCallback,
        CallCartAddFromHomeCallback {
    private static final String TAG = ProductsFragment.class.getSimpleName();

    private ImageView goBackButton;
    private String status = "";

    // getViewModel
    private ViewModelHomeFragment mViewModelHomeFragment;
    private RecyclerView mProductsRecyclerView;
//  private FeaturedProductsAdapter mProductsAdapter;
    private AdapterProduct mProductsAdapter;

//  private ProgressBar indeterminateProductsFragment;
    private ExtendedFloatingActionButton productsFAB;
    private ExtendedFloatingActionButton productDetailsCloseFAB;
    private ProgressBar indeterminateProductsFragment;

    private ProgressDialog mProgressDialog;
    private SearchView productSearchView;
    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        goBackButton.setOnClickListener(GoBackListener());
        productDetailsCloseFAB.setOnClickListener(byForceGoHome());
        OnAddMoreFloatingButtonClick();
        OnScrollToBottom();
        mProductsRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();
        goBackButton.setOnClickListener(null);
        productsFAB.setOnClickListener(null);
        productDetailsCloseFAB.setOnClickListener(null);
    }

    private void OnAddMoreFloatingButtonClick(){
        productsFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AddMoreProducts();
            }
        });
    }

    private View.OnClickListener byForceGoHome(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
                    getActivity().findViewById(R.id.containerProducts).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
                }
                // only nestedFragment could be called before opening productsFragment
                // close it if opened
                removePreviousNestedFragment();
            }
        };
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

    private void OnScrollToBottom(){
//        mProductsRecyclerView.getViewTreeObserver()
//                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                    @Override
//                    public void onScrollChanged() {
//                        if(mProductsRecyclerView.getChildAt(0).getBottom() <= ( mProductsRecyclerView.getHeight() + mProductsRecyclerView.getScrollY())){
//                            // scrollView is at bottom
//                            AddMoreProducts();
//                        } else {
//                            // scrollView is not at bottom
//                            // do nothing
//                        }
//                    }
//
//                });


//        if(!(mProductsRecyclerView.canScrollVertically(1))){
//            // scrollView is at bottom
//            AddMoreProducts();
//        }

//        View view = (View) mProductsRecyclerView.getChildAt(mProductsRecyclerView.getChildCount() - 1);
//        int diff = (view.getBottom() + mProductsRecyclerView.getPaddingBottom() - (mProductsRecyclerView.getHeight() + mProductsRecyclerView.getScrollY()));
//        if(diff <= 10){
//            // at bottom
//            AddMoreProducts();
//        }

    }

    private void AddMoreProducts(){
        String seeAll = MainActivity.seeAll;
        switch (seeAll){
            case "explore":
                addMoreExploreProducts(returnIncrease(seeAll));
                break;
            case "category":
                addMoreCategoryProducts(returnIncrease(seeAll));
                break;
            case "product":
                addMoreProducts(returnIncrease(seeAll));
                break;
            case "hotsales":
                addMoreHotSales(returnIncrease(seeAll));
                break;
            case "trending":
                addMoreTrending(returnIncrease(seeAll));
                break;
            case "search":
                Log.d(TAG, "onClick: searching called");
                addMoreSearch(returnIncrease(seeAll));
                break;
            case "history":
                if(userIsSignedIn()){ addMoreHistory(returnIncrease(seeAll)); }
                else { Toast.makeText(getContext(), "Please sign in",Toast.LENGTH_SHORT).show(); }
                break;
            case "nested":
                addMoreNested(returnIncrease(seeAll));
                break;
            case "viewed":
                addMoreUsersViewedAlso(returnIncrease(seeAll));
                break;
            case "seller":
                addMoreFromSeller(returnIncrease(seeAll));
                break;
            case "similar":
                addMoreSimilarProducts(returnIncrease(seeAll));
                break;
            default:
                // do nothing
                break;
        }
    }


    private void init(View view){

        productSearchView = view.findViewById(R.id.productSearchView);
        goBackButton = view.findViewById(R.id.goBack);
        int numOfColumns = 2;
        // setting adapter for products
        mProductsAdapter = new AdapterProduct(this.getContext(), new ArrayList<Product>(), this, this, this);
        mProductsRecyclerView = view.findViewById(R.id.holderProducts);
        mProductsRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), numOfColumns));
        mProductsRecyclerView.setAdapter(mProductsAdapter);

        productsFAB = view.findViewById(R.id.productsFab);
        productDetailsCloseFAB = view.findViewById(R.id.productDetailsCloseFAB);
        indeterminateProductsFragment = view.findViewById(R.id.indeterminateProductsFragment);

        sortSeeAll(MainActivity.seeAll);
        setProductSearchView();
        showProgressBar();
    }

    private void setProductSearchView(){
//        configureSubCategorySearch();
//        productSearchView.setQueryHint(String.format("Search %s", MainActivity.searchOther));
        productSearchView.setQueryHint(String.format("Search %s", configureSearchHintText()));
        productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: text submit");
                showProgressBar();
                mProductsRecyclerView.setVisibility(View.VISIBLE);
                setUpSearchItem(query);
                sortSeeAll(MainActivity.seeAll);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: text changed");
                mProductsRecyclerView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        // set text focus handler
        productSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: focus change");
                mProductsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }


//    private void configureSubCategorySearch(){
//        // When productsFragment is called from nestedFragment
//        // search only the category and not subCategory
//        if(MainActivity.searchRef.equals("subCategory")){
//            String[] cAndSub = MainActivity.searchOther.split(",");
//            MainActivity.searchOther = cAndSub[0]; // get category
//            MainActivity.searchRef = "category";
//        }
//    }


    private String configureSearchHintText(){
        if(MainActivity.seeAll.equals("similar") ||
                MainActivity.seeAll.equals("viewed") ||
                MainActivity.seeAll.equals("seller") ||
                MainActivity.seeAll.equals("trending") ||
                MainActivity.seeAll.equals("history") ){
            return "home";
        }
        if(MainActivity.searchRef.equals("subCategory")){
            String[] cAndSub = MainActivity.searchOther.split(",");
            MainActivity.searchOther = cAndSub[0]; // get category
            MainActivity.searchRef = "category";
        }
        return MainActivity.searchOther;
    }

    private void setUpSearchItem(String query){
        mViewModelHomeFragment.setSearchNull();
        ViewModelHomeFragment.searchIncrease = 1;
        MainActivity.search = generalHashMap(MainActivity.searchRef, String.valueOf(ViewModelHomeFragment.searchIncrease), query, MainActivity.searchOther);
        MainActivity.seeAll = "search";
    }

    private void sortSeeAll(String seeAll){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
        }
        switch (seeAll){
            case "explore": sortExplore(); break;
            case "product": sortProducts(); break;
            case "hotsales": sortHotSales(); break;
            case "trending": sortTrending(); break;
            case "history": sortHistory(); break;
            case "category": sortCategoryClick();break;
            case "search": sortSearch();break;
            case "nested": sortNested();break;
            case "viewed": sortUsersViewed(); break;
            case "similar": sortSimilar(); break;
            case "seller": sortFromSeller(); break;
            default:
                // do nothing
                break;
        }
    }

    private HashMap<String, String> generalHashMap(String ref, String part, String word, String other) {
        HashMap<String, String> obj = new HashMap<>();
        obj.put("ref", ref);
        obj.put("part", part);
        obj.put("word", word);
        obj.put("other", other);
        obj.put("customerId", mCustomerId);
        return obj;
    }

    private void sortExplore(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1", "", MainActivity.searchOther));

            mViewModelHomeFragment.initExploreProducts(list);
            mViewModelHomeFragment.getExploreProducts().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortProducts(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initProducts(list);
            mViewModelHomeFragment.getProducts().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    Log.d(TAG, "onChanged: observing " + products);
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortHotSales(){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initHotSales(list);
            mViewModelHomeFragment.getHotSales()
                    .observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    Log.d(TAG, "onChanged: observing " + products);
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
    }

    private void sortTrending(){
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(generalHashMap(MainActivity.searchRef, "1", "", MainActivity.searchOther));

        mViewModelHomeFragment.initTrendingProducts(list);
        mViewModelHomeFragment.getTrendingProducts()
                .observe(this, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        mProductsAdapter.loadNewData(products);
                        hideProgressBar();
                    }
                });
    }


    private void sortHistory(){
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(generalHashMap(MainActivity.searchRef, "1", "", MainActivity.searchOther));

        mViewModelHomeFragment.initCustomerHistory(list);
        mViewModelHomeFragment.getCustomerHistory()
                .observe(this, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        mProductsAdapter.loadNewData(products);
                        hideProgressBar();
                    }
                });
    }

    private void sortSearch(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, MainActivity.search.get("part"), MainActivity.search.get("word"), MainActivity.searchOther));

            mViewModelHomeFragment.initSearch(list);
            mViewModelHomeFragment.getSearchedProducts()
                    .observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortCategoryClick(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initCategoryProducts(list);
            mViewModelHomeFragment.getCategoryProducts()
                    .observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    Log.d(TAG, "onChanged: category products changed " + products);
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortNested(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1", "", MainActivity.searchOther));

            mViewModelHomeFragment.resetProductByCatAndSub();
            mViewModelHomeFragment.initProductsByCatAndSub(list);
            mViewModelHomeFragment.getProductsByCatAndSubCat().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    mProductsAdapter.loadNewData(products);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortSimilar(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initSimilarProducts(list);
            mViewModelHomeFragment.getSimilarProducts()
                    .observe(this, new Observer<List<Product>>() {
                        @Override
                        public void onChanged(List<Product> productList) {
                            mProductsAdapter.loadNewData(productList);
                            hideProgressBar();
                        }
                    });
        }
    }

    private void sortFromSeller(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initSellerRecommendation(list);
            mViewModelHomeFragment.getSellerRecommendation()
                    .observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> productList) {
                    mProductsAdapter.loadNewData(productList);
                    hideProgressBar();
                }
            });
        }
    }

    private void sortUsersViewed(){
        if(getActivity() != null){
            List<HashMap<String, String>> list = new ArrayList<>();
            list.add(generalHashMap(MainActivity.searchRef, "1","", MainActivity.searchOther));

            mViewModelHomeFragment.initUsersAlsoViewed(list);
            mViewModelHomeFragment.getUsersAlsoViewed()
                    .observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> productList) {
                    mProductsAdapter.loadNewData(productList);
                    hideProgressBar();
                }
            });
        }
    }


    private List<HashMap<String, String>> returnIncrease(String status){
        List<HashMap<String, String>> list = new ArrayList<>();
        String part = "";
        switch (status){
            case "explore":
                ViewModelHomeFragment.exploreIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.exploreIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
            case "category":
                ViewModelHomeFragment.categoryProductIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.categoryProductIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "product":
                ViewModelHomeFragment.productIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.productIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "hotsales":
                ViewModelHomeFragment.hotSalesIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.hotSalesIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "trending":
                ViewModelHomeFragment.trendingIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.trendingIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "nested":
                ViewModelHomeFragment.subCatProductsIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.subCatProductsIncrease);
                list.add(generalHashMap(MainActivity.searchRef, part, "", MainActivity.searchOther));
                break;
            case "history":
               ViewModelHomeFragment.historyIncrease += 1;
               part = String.valueOf(ViewModelHomeFragment.historyIncrease);
               list.add(generalHashMap(MainActivity.searchRef, part, "", MainActivity.searchOther));
                break;
            case "search":
                ViewModelHomeFragment.searchIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.searchIncrease);
                list.add(generalHashMap(MainActivity.searchRef, part,  MainActivity.search.get("word"),  MainActivity.searchOther));
                break;
            case "viewed":
                ViewModelHomeFragment.usersAlsoViewedIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.usersAlsoViewedIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "seller":
                ViewModelHomeFragment.fromSellerIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.fromSellerIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            case "similar":
                ViewModelHomeFragment.similarProductsIncrease += 1;
                part = String.valueOf(ViewModelHomeFragment.similarProductsIncrease);
                list.add(generalHashMap(status, part, "", MainActivity.searchOther));
                break;
            default:
                // do nothing
                break;
        }
        return list;
    }

    private void
     addMoreUsersViewedAlso(List<HashMap<String, String>> list){
        mViewModelHomeFragment.addMoreUsersAlsoViewed(list).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                mViewModelHomeFragment.addToUsersViewed(productList);
            }
        });

        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getUsersAlsoViewed().getValue()){
                        int size = mViewModelHomeFragment.getUsersAlsoViewed().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });

    }

    private void addMoreFromSeller(List<HashMap<String, String>> list){
        mViewModelHomeFragment.addMoreFromSeller(list).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                mViewModelHomeFragment.addToFromSeller(productList);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getSellerRecommendation().getValue()){
                        int size = mViewModelHomeFragment.getSellerRecommendation().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });
    }

    private void addMoreSimilarProducts(List<HashMap<String, String>> list){
        mViewModelHomeFragment.addMoreSimilarProducts(list).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                mViewModelHomeFragment.addToSimilarProducts(productList);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getSimilarProducts().getValue()){
                        int size = mViewModelHomeFragment.getSimilarProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });
    }

    private void addMoreExploreProducts(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreExploreProducts(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mViewModelHomeFragment.addToExploreProducts(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getExploreProducts().getValue()){
                        int size = mViewModelHomeFragment.getExploreProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });

    }

    private void addMoreCategoryProducts(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreCategoryProducts(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "onChanged: adding category products " + products);
                mViewModelHomeFragment.addToCategoryProducts(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getCategoryProducts().getValue()){
                        int size = mViewModelHomeFragment.getCategoryProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });
    }

    private void addMoreProducts(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreProducts(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "onChanged: adding products " + products);
                mViewModelHomeFragment.addToProducts(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getProducts().getValue()){
                        int size = mViewModelHomeFragment.getProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });

    }

    private void addMoreHotSales(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreHotSales(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "onChanged: adding hotsales");
                mViewModelHomeFragment.addToHotSales(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    if(null != mViewModelHomeFragment.getHotSales().getValue()){
                        int size = mViewModelHomeFragment.getHotSales().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
                    }
                }
            }
        });

    }

    private void addMoreTrending(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreTrendingProducts(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mViewModelHomeFragment.addToTrendingProducts(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    // scroll recyclerView to bottom position
                    if(null != mViewModelHomeFragment.getTrendingProducts().getValue()){
                        int size = mViewModelHomeFragment.getTrendingProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
//                        mProductsRecyclerView.smoothScrollToPosition(mViewModelHomeFragment.getTrendingProducts().getValue().size() - 1);
                    }
                }
            }
        });

    }

    private void addMoreHistory(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreHistory(body)
                .observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mViewModelHomeFragment.addToHistory(products);
            }
        });
        mViewModelHomeFragment.isUpdating
                .observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    // scroll recyclerView to bottom position
                    if(null != mViewModelHomeFragment.getCustomerHistory().getValue()){
                        int size = mViewModelHomeFragment.getCustomerHistory().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
//                        mProductsRecyclerView.smoothScrollToPosition(mViewModelHomeFragment.getCustomerHistory().getValue().size() - 1);
                    }
                }
            }
        });
    }

    private void addMoreSearch(List<HashMap<String, String>> body){
        Log.d(TAG, "addMoreSearch: adding more search");
        mViewModelHomeFragment.addMoreSearchedProducts(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "onChanged: adding more searched products");
                mViewModelHomeFragment.addToSearchProducts(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    // scroll recyclerView to bottom position
                    if(null != mViewModelHomeFragment.getSearchedProducts().getValue()){
                        int size = mViewModelHomeFragment.getSearchedProducts().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
//                        mProductsRecyclerView.smoothScrollToPosition(mViewModelHomeFragment.getSearchedProducts().getValue().size() - 1);
                    }
                }
            }
        });
    }

    private void addMoreNested(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreProductsByCatAndSubCat(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mViewModelHomeFragment.addToProductsByCatAndSubCat(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    // scroll recyclerView to bottom position
                    if(null != mViewModelHomeFragment.getProductsByCatAndSubCat().getValue()){
                        int size = mViewModelHomeFragment.getProductsByCatAndSubCat().getValue().size();
                        if(size > 1){
                            mProductsRecyclerView.smoothScrollToPosition(size - 1);
                        }
//                        mProductsRecyclerView.smoothScrollToPosition(mViewModelHomeFragment.getProductsByCatAndSubCat().getValue().size() - 1);
                    }
                }
            }
        });
    }

    private void showProgressBar(){
        indeterminateProductsFragment.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        indeterminateProductsFragment.setVisibility(View.GONE);
    }


    @Override
    public void productClickCallback(Product product) {

        Log.d(TAG, "productClickCallback: called ");
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCT_DETAILS;
        MainActivity.stepOne = false;
//        MainActivity.mStepsTracking = MainActivity.Steps.STEP_TWO;

        // open product details
        Bundle arguments = new Bundle();
        arguments.putSerializable(Product.class.getSimpleName(), product);
        arguments.putString("HOME", "HOME");

        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(arguments);
        loadProductDetailsFragment(productDetailsFragment);
    }

    // from AddFpToWishListCallback
    @Override
    public void addFpToWishList(Product product, AdapterProduct.ViewHolder holder) {
        if(userIsSignedIn()){
            holder.homeFpAddWishList.setImageResource(R.drawable.ic_baseline_favorite_24);
            setWishListViewModel(product.get_id());
        } else { showSignIn(); }
    }

    // from AddCartFromHomeCallback
    @Override
    public void callCartAddDialog(Product product) {
        ViewModelHomeFragment.productDetails = product;
        loadCartAddDialog();
    }

    private void loadCartAddDialog(){
        FragmentManager fm = getFragmentManager();
        CartAddDialog cd = CartAddDialog.getInstance();
        cd.setTargetFragment(ProductsFragment.this, 300);
        assert fm != null;
        cd.show(fm, "CartAddDialog");
    }

    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null &&
                !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }

    private void setWishListViewModel(String productId){
        mViewModelHomeFragment
                .createWishList(createWishList(productId)).observe(Objects.requireNonNull(
                        getActivity()), new Observer<Product>() {
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


    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();

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

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            Log.d(TAG, "removePreviousProductsFameLayoutData: removing");
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

    private void loadProductDetailsFragment(Fragment fragment){
        removePreviousProductDetailsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findProductFrameLayout().setVisibility(View.GONE);
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

    private void resetIncrease(){
        if(MainActivity.seeAll.equals("similar") ||
                MainActivity.seeAll.equals("viewed") ||
                MainActivity.seeAll.equals("seller") ||
                MainActivity.seeAll.equals("history") ){
            ViewModelHomeFragment.usersAlsoViewedIncrease = 1;
            ViewModelHomeFragment.fromSellerIncrease = 1;
            ViewModelHomeFragment.similarProductsIncrease = 1;
        }
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
    private void setVisibility(){
        stepTwoVisibility();
        stepThreeVisibility();
        // reset product increase of viewed,from seller,similar
        resetIncrease();
    }

    private void stepTwoVisibility(){
        if(MainActivity.mStepsTracking == MainActivity.Steps.STEP_TWO){
            MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
            Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
            Log.d(TAG, "stepTwoVisibility: called");
        }
    }

    private void stepThreeVisibility(){
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            if(MainActivity.mStepsTracking == MainActivity.Steps.STEP_THREE){
                MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCT_REVIEW;
                Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts).setVisibility(View.GONE);
                getActivity().findViewById(R.id.containerProductReview).setVisibility(View.VISIBLE);
                Log.d(TAG, "stepThreeVisibility: called");
            }
        }
    }

    private void GoBack(){
        setVisibility();
        removeThisFragment();
    }

    private View.OnClickListener GoBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoBack();
            }
        };
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            Log.d(TAG, "onBackPressed: Home track is products");
            GoBack();
        }
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading " + MainActivity.searchOther);
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

    private View findHomeFrameLayout(){ return getActivity().findViewById(R.id.containerHomeFragment);}
    private View findProductDetailsFrameLayout(){ return getActivity().findViewById(R.id.containerProductDetails);}
    private View findProductFrameLayout(){ return getActivity().findViewById(R.id.containerProducts);}
    private ExtendedFloatingActionButton findProductsFloatingButton(){ return getActivity().findViewById(R.id.productsFab);}


}
