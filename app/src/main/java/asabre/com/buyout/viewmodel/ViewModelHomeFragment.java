package asabre.com.buyout.viewmodel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import asabre.com.buyout.service.model.AboutUs;
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.Category;
import asabre.com.buyout.service.model.Customer;
import asabre.com.buyout.service.model.News;
import asabre.com.buyout.service.model.Order;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.service.model.Review;
import asabre.com.buyout.service.model.SubCategory;
import asabre.com.buyout.service.model.UserEntity;
import asabre.com.buyout.service.repository.HomeRepository;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

// simi smile for me
// take a chance on me don't you believe am worth the risk
public class ViewModelHomeFragment extends ViewModel {
    private static final String TAG = ViewModelHomeFragment.class.getSimpleName();
    private HomeRepository mHomeRepository;

    private MutableLiveData<List<Category>> mCategoryListObservable;
    private MutableLiveData<List<SubCategory>> mSubCategories;
    private MutableLiveData<List<Product>> mProducts;
    private MutableLiveData<List<Product>> mSimilarProducts;
    private MutableLiveData<List<Product>> mSimilarWProducts;
    private MutableLiveData<List<Product>> mBoughtTogetherProducts;
    private MutableLiveData<List<Product>> mSellerProducts;
    private MutableLiveData<List<Product>> mUsersViewed;
    private MutableLiveData<List<Product>> mHotSales;
    private MutableLiveData<List<Product>> mTrendingProducts;
    private MutableLiveData<List<Product>> mExploreProducts;
    private MutableLiveData<List<Product>> mSearchedProducts;
    private MutableLiveData<List<Product>> mCategoryProducts;
    private MutableLiveData<List<Product>> mProductsByCatAndSubCat;

    private MutableLiveData<List<Product>> mWishLists;
    public static MutableLiveData<List<Cart>> mCarts;
    private MutableLiveData<List<Order>> mOrders;

    private MutableLiveData<String> mCustomerImage;
    private MutableLiveData<List<Address>> mCustomerAddress;
    private MutableLiveData<Address> mCreateUserAddress;


    public MutableLiveData<Product> mCreateWishList;
    public MutableLiveData<Cart> mCreateCart;
    public static MutableLiveData<List<Review>> mProductReview;
    private MutableLiveData<List<Product>> mCustomerHistory;

    // home filter
    private MutableLiveData<List<AboutUs>> mAboutUs;
    private MutableLiveData<List<News>> mNews;
    private MutableLiveData<Product> mProductOfTheDay;

    // track adding more products
    private List<Product> moreProducts = new ArrayList<>();
    public MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    public MutableLiveData<Boolean> userSignedIn = new MutableLiveData<>();
    public static MutableLiveData<Boolean> mediaPermission = new MutableLiveData<>();
    public static MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    public static String selectedAddressId = "";
//    public static List<Address> moreUserAddress;
    public static UserEntity userEntity = null;
    public static MutableLiveData<Customer> userIsEdited = new MutableLiveData<>();
    public static MutableLiveData<Boolean> updatingUser = new MutableLiveData<>();
    public static MutableLiveData<Boolean> cartIsEmpty = new MutableLiveData<>();
    public static Order orderDetails;
    public static Cart cartDetails;
    public static Product productDetails;

    // when adding more products to cart at once
    // eg bought together
//    public static List<Product> addAllToCart;

    // product id of current product user wishes to view it's reviews
    public static String productReviewId = "";

    // variables for fetching extra data
    public static int productIncrease = 1;
    public static int hotSalesIncrease = 1;
    public static int trendingIncrease = 1;
    public static int searchIncrease = 1;
    public static int categoryProductIncrease = 1;
    public static int exploreIncrease = 1;
    public static int wishListIncrease = 1;
    public static int historyIncrease = 1;
    public static int subCatProductsIncrease = 1;
    public static int usersAlsoViewedIncrease = 1;
    public static int fromSellerIncrease = 1;
    public static int similarProductsIncrease = 1;

    public void init(){ mHomeRepository = HomeRepository.getInstance(); }

    public void initCategories(){
        if(mCategoryListObservable != null){ return; }
        mCategoryListObservable = mHomeRepository.getCategories();
    }


    public void initProducts(List<HashMap<String, String>> body){
        if(mProducts != null){ return; }
        mProducts = mHomeRepository.getProducts(body);
    }

    public void initSimilarProducts(List<HashMap<String, String>> body){
        if(mSimilarProducts != null){ return; }
        mSimilarProducts = mHomeRepository.getSimilarProducts(body);
    }
    public void resetSimilarProducts(){ mSimilarProducts = null; }

    public void initSellerRecommendation(List<HashMap<String, String>> body){
        if(mSellerProducts != null){ return ; }
        mSellerProducts = mHomeRepository.getSellerRecommendation(body);
    }
    public void resetSellerRecommendation(){ mSellerProducts = null; }

    public void initUsersAlsoViewed(List<HashMap<String, String>> body){
        if(mUsersViewed != null){ return; }
        mUsersViewed = mHomeRepository.getUsersViewed(body);
    }
    public void resetUserAlsoViewed(){
        mUsersViewed = null;
    }

    // similar products for wishList
    public void initSimilarWProducts(List<HashMap<String, String>> body){
        if(mSimilarWProducts != null){ return; }
        mSimilarWProducts = mHomeRepository.getSimilarWProducts(body);
    }
    public void resetSimilarWProducts(){ mSimilarWProducts = null; }

    public void initBoughtTogether(List<HashMap<String, String>> body){
        if(mBoughtTogetherProducts != null){ return; }
        mBoughtTogetherProducts = mHomeRepository.getBoughtTogether(body);
    }
    public void resetBoughtTogether(){ mBoughtTogetherProducts = null;}

    public void initHotSales(List<HashMap<String, String>> body){
        if(mHotSales != null){ return; }
        mHotSales = mHomeRepository.getHotSales(body);
    }

    public void initTrendingProducts(List<HashMap<String, String>> body){
        if(mTrendingProducts != null){ return; }
        mTrendingProducts = mHomeRepository.getTrendingProducts(body);
    }

    public void initExploreProducts(List<HashMap<String, String>> body){
        if(mExploreProducts != null){return; }
        mExploreProducts = mHomeRepository.getExploreProducts(body);
    }

    public void initSearch(List<HashMap<String, String>> body){
        if(mSearchedProducts != null){ return;}
        mSearchedProducts = mHomeRepository.getSearchProducts(body);
    }
    public void setSearchNull(){ mSearchedProducts = null; }

    public void initCategoryProducts(List<HashMap<String, String>> body){
        if(mCategoryProducts != null){ return; }
        mCategoryProducts = mHomeRepository.getCategoryProducts(body);
    }
    public void setCategoryProductsNull(){ mCategoryProducts = null; }

    public void initWishLists(List<HashMap<String, String>> body){
        if(mWishLists != null){ return; }
        mWishLists = mHomeRepository.getCustomerWishList(body);
    }
    public void setWishListNull(){
        mWishLists = null;
    }

    public void initCart(List<HashMap<String, String>> body){
        if(mCarts != null){ return;}
        mCarts = mHomeRepository.getCustomerCart(body);
    }
    // cart is set null eg when a user is signed in
    public void setCartNull(){ mCarts = null; }

    public void initOrders(List<HashMap<String, String>> body){
        if(mOrders != null){return;}
        mOrders = mHomeRepository.getCustomerOrder(body);
    }
    public void setOrdersNull(){
        mOrders = null;
    }



    public void initUserIsEdited(HashMap<String, String> body){
        userIsEdited = mHomeRepository.findCustomerByNP(body);
    }


    public void initCustomerImage(MultipartBody.Part file, RequestBody customerId){
        mCustomerImage = mHomeRepository.setCustomerImage(file, customerId);
    }

    public void initCustomerAddress(List<HashMap<String, String>> body){
        if(mCustomerAddress != null){ return; }
        mCustomerAddress = mHomeRepository.getCustomerAddress(body);
    }
    public void resetCustomerAddress(){
        mCustomerAddress = null;
    }

    public LiveData<OutLaw> updateCustomer(HashMap<String, String> body){
        return mHomeRepository.updateCustomer(body);
    }

    public LiveData<OutLaw> resetCustomerPassword(HashMap<String, String> body){
        return mHomeRepository.resetCustomerPassword(body);
    }

    public LiveData<Address> createUserAddress(HashMap<String, String> body){
        mCreateUserAddress = mHomeRepository.createUserAddress(body);
        return mCreateUserAddress;
    }
    public LiveData<OutLaw> updateUserAddress(HashMap<String, String> body){
        return mHomeRepository.updateUserAddress(body);
    }
    public LiveData<OutLaw> deleteCustomerAddress(HashMap<String, String> body){
        return mHomeRepository.deleteCustomerAddress(body);
    }

    public void initProductReview(List<HashMap<String, String>> body){
        if(mProductReview != null){ return;}
        mProductReview = mHomeRepository.getProductReview(body);
    }

    public LiveData<List<Review>> getProductReview(){
        return mProductReview;
    }
    public void setProductReviewNull(){
        mProductReview = null;
    }
    public void reviewThumbs(HashMap<String, String> body){
        mHomeRepository.reviewThumb(body);
    }
    public LiveData<OutLaw> createProductReview(HashMap<String, String> body){
        return mHomeRepository.createProductReview(body);
    }

    public void initSubCategories(List<HashMap<String, String>> body){
        if(mSubCategories != null){ return ; }
        mSubCategories = mHomeRepository.getSubCategories(body);
    }
    public void resetSubCategories(){ mSubCategories = null; }

    public void initProductsByCatAndSub(List<HashMap<String, String>> body){
        if(mProductsByCatAndSubCat != null){return;}
        mProductsByCatAndSubCat = mHomeRepository.getProductsByCategoryAndSubCategory(body);
    }
    public void resetProductByCatAndSub(){ mProductsByCatAndSubCat = null; }


    /*
       * Expose the LiveData query for the UI to observe it
     */
    public LiveData<List<Category>> getCategoriesObservable(){ return mCategoryListObservable; }
    public LiveData<List<Product>> getProducts(){ return mProducts; }
    public LiveData<List<Product>> getSimilarProducts(){ return mSimilarProducts; }
    public LiveData<List<Product>> getSimilarWProducts(){ return mSimilarWProducts; }
    public LiveData<List<Product>> getSellerRecommendation(){ return mSellerProducts; }
    public LiveData<List<Product>> getUsersAlsoViewed(){ return mUsersViewed; }
    public LiveData<List<Product>> getBoughtTogether(){ return mBoughtTogetherProducts; }
    public LiveData<List<Product>> getHotSales(){ return mHotSales; }
    public LiveData<List<Product>> getTrendingProducts(){ return mTrendingProducts; }
    public LiveData<List<Product>> getExploreProducts(){ return mExploreProducts; }
    public LiveData<List<Product>> getSearchedProducts(){ return mSearchedProducts; }
    public LiveData<List<Product>> getCategoryProducts(){ return mCategoryProducts; }
    public LiveData<List<Address>> getCustomerAddress(){ return mCustomerAddress; }
    public LiveData<List<Product>> getCustomerWishList(){ return mWishLists; }
    public LiveData<List<Cart>> getCustomerCart(){ return mCarts; }
    public LiveData<List<Order>> getCustomerOrders(){return mOrders; }
    public LiveData<List<SubCategory>> getSubCategories(){ return mSubCategories; }
    public LiveData<List<Product>> getProductsByCatAndSubCat(){ return mProductsByCatAndSubCat; }


    public LiveData<OutLaw> signUpCustomer(HashMap<String, String> body){
        return mHomeRepository.signUpCustomer(body);
    }
    public LiveData<Customer> initSignInCustomer(HashMap<String, String> body){
        return mHomeRepository.signInCustomerByPhoneAndPwd(body);
    }

    public LiveData<Customer> getUserIsEdited(){ return userIsEdited; }
    public LiveData<String> setCustomerImage(){return mCustomerImage;}
    public LiveData<Boolean> checkIfCartIsEmpty(){ return cartIsEmpty; }


    public LiveData<Product> createWishList(HashMap<String, String> body){
        return mHomeRepository.createWishList(body);
    }

    public LiveData<List<Product>> createWishLists(List<HashMap<String, String>> body){
        return mHomeRepository.createWishLists(body);
    }

    public LiveData<OutLaw> deleteCustomerWishList(HashMap<String, String> body){
        return mHomeRepository.deleteCustomerWishList(body);
    }

    public LiveData<Cart> createCart(HashMap<String, String> body){
        return mHomeRepository.createCart(body);
    }

    public LiveData<List<Cart>> createCarts(List<HashMap<String, String>> body){
        return mHomeRepository.createCarts(body);
    }

    public LiveData<List<String>> deleteCustomerCart(List<HashMap<String, String>> body){
        return mHomeRepository.deleteCustomerCart(body);
    }
    public void cartThumbs(HashMap<String, String> body){
        mHomeRepository.cartThumbs(body);
    }

    public LiveData<String[]> createOrder(List<HashMap<String, String>> body){
        return mHomeRepository.createOrder(body);
    }
    public LiveData<OutLaw> deleteCustomerOrder(HashMap<String, String> body){
        return mHomeRepository.deleteCustomerOrder(body);
    }


    public void initCustomerHistory(List<HashMap<String, String>> body){
        if(mCustomerHistory != null){ return; }
        mCustomerHistory = mHomeRepository.getCustomerHistoryInParts(body);
    }
    public void resetCustomerHistory(){ mCustomerHistory = null; }
    public LiveData<List<Product>> getCustomerHistory(){
        return mCustomerHistory;
    }


    // home filter ( should be download once )
    public LiveData<List<AboutUs>> getAboutUs(){
        if(mAboutUs != null){ return mAboutUs;}
        mAboutUs = mHomeRepository.getAboutUs();
        return mAboutUs;
    }
    public LiveData<List<News>> getNews(){
        if(mNews != null){ return mNews; }
        mNews = mHomeRepository.getNews();
        return mNews;
    }
    public void newsThumbs(HashMap<String, String> body){ mHomeRepository.newsThumbs(body); }

    public LiveData<Product> getProductOfTheDay(){
        if(mProductOfTheDay != null){ return mProductOfTheDay; }
        mProductOfTheDay = mHomeRepository.getProductOfTheDay();
        return mProductOfTheDay;
    }

    // TODO adding more products

    public LiveData<List<Product>> addMoreProducts(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mProducts.getValue();         // save current products
        return mHomeRepository.getProducts(body);// load new products
    }
    public void addToProducts(List<Product> products){
        isUpdating.setValue(false);
        moreProducts.addAll(products);
        mProducts.setValue(moreProducts);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreProductsByCatAndSubCat(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        if(null != mProductsByCatAndSubCat.getValue()){ moreProducts = mProductsByCatAndSubCat.getValue(); }
        return mHomeRepository.getProductsByCategoryAndSubCategory(body);
    }
    public void addToProductsByCatAndSubCat(List<Product> products){
        isUpdating.setValue(false);
        moreProducts.addAll(products);
        mProductsByCatAndSubCat.setValue(moreProducts);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreHotSales(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mHotSales.getValue();
        return mHomeRepository.getHotSales(body); // load new hotsales
    }
    public void addToHotSales(List<Product> hotSales){
        moreProducts.addAll(hotSales);
        mHotSales.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreUsersAlsoViewed(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mUsersViewed.getValue();
        return mHomeRepository.getUsersViewed(body);
    }
    public void addToUsersViewed(List<Product> productList){
        moreProducts.addAll(productList);
        mUsersViewed.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreFromSeller(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mSellerProducts.getValue();
        return mHomeRepository.getSellerRecommendation(body);
    }
    public void addToFromSeller(List<Product> productList){
        moreProducts.addAll(productList);
        mSellerProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreSimilarProducts(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mSimilarProducts.getValue();
        return mHomeRepository.getSimilarProducts(body);
    }
    public void addToSimilarProducts(List<Product> productList){
        moreProducts.addAll(productList);
        mSimilarProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreTrendingProducts(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mTrendingProducts.getValue();
        return mHomeRepository.getTrendingProducts(body);
    }
    public void addToTrendingProducts(List<Product> trendingProducts){
        moreProducts.addAll(trendingProducts);
        mTrendingProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreExploreProducts(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mExploreProducts.getValue();
        return mHomeRepository.getExploreProducts(body);
    }
    public void addToExploreProducts(List<Product> exploreProducts){
        moreProducts.addAll(exploreProducts);
        mExploreProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreHistory(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mCustomerHistory.getValue();
        return mHomeRepository.getCustomerHistoryInParts(body);
    }
    public void addToHistory(List<Product> history){
        moreProducts.addAll(history);
        mCustomerHistory.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreSearchedProducts(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mSearchedProducts.getValue();
        return mHomeRepository.getSearchProducts(body);
    }
    public void addToSearchProducts(List<Product> searchedItems){
        moreProducts.addAll(searchedItems);
        mSearchedProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public LiveData<List<Product>> addMoreCategoryProducts(List<HashMap<String, String>> body){
         isUpdating.setValue(true);
         moreProducts = mCategoryProducts.getValue();
         return mHomeRepository.getCategoryProducts(body);
    }
    public void addToCategoryProducts(List<Product> categoryProducts){
        moreProducts.addAll(categoryProducts);
        mCategoryProducts.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    // loading more of users wishLists from wishListFragment
    public LiveData<List<Product>> addMoreWishList(List<HashMap<String, String>> body){
        isUpdating.setValue(true);
        moreProducts = mWishLists.getValue();
        return mHomeRepository.getCustomerWishList(body);
    }
    public void addToWishList(List<Product> wishList){
        moreProducts.addAll(wishList);
        mWishLists.setValue(moreProducts);
        isUpdating.setValue(false);
        moreProducts = new ArrayList<>();
    }

    public void addMoreUserAddress(Address address){
        ArrayList<Address> moreUserAddress = new ArrayList<>(Objects.requireNonNull(mCustomerAddress.getValue()));
        moreUserAddress.add(address);
        mCustomerAddress.setValue(moreUserAddress);
    }
    public void removeFromAddress(Address address){
        ArrayList<Address> moreUserAddress = new ArrayList<>(Objects.requireNonNull(mCustomerAddress.getValue()));
        moreUserAddress.remove(address);
        mCustomerAddress.setValue(moreUserAddress);
    }

    public void addToBoughtTogether(Product product){
        ArrayList<Product> morePro = new ArrayList<>();
        if(null != mBoughtTogetherProducts.getValue()){
            morePro.add(product);
            morePro.addAll(mBoughtTogetherProducts.getValue());
            mBoughtTogetherProducts.setValue(morePro);
        }
    }


    // TODO deleting items/products

    // adding created wishLists to data
    public void productToWishList(List<Product> productList){
        ArrayList<Product> mProducts = new ArrayList<>();
        if(null != mWishLists.getValue()){
            mProducts.addAll(mWishLists.getValue());
        }
        mProducts.addAll(productList);
        mWishLists.setValue(mProducts);
    }

    // when remove wishList is clicked from wishList page
    public void removeFromWishList(Product product){
        ArrayList<Product> mProducts = new ArrayList<>();
        if(null != mWishLists.getValue()){ mProducts.addAll(mWishLists.getValue()); }
        mProducts.remove(product);
        mWishLists.setValue(mProducts);
    }

    // when a cart is created from productDetails page
    public void cartToCart(List<Cart> cartList){
        ArrayList<Cart> moreCarts = new ArrayList<>();
        if(null != mCarts.getValue()){ moreCarts.addAll(mCarts.getValue()); }
        moreCarts.addAll(cartList);
        mCarts.setValue(moreCarts);
    }

    // when remove cart is clicked from cart page
    public void removeFromCart(Cart cart){
        ArrayList<Cart> moreCarts = new ArrayList<>();
        if(null != mCarts.getValue()){ moreCarts.addAll(mCarts.getValue()); }
        moreCarts.remove(cart);
        mCarts.setValue(moreCarts);
    }




    public void removeFromOrder(Order order){
        ArrayList<Order> moreOrder = new ArrayList<>();
//        ArrayList<Order> moreOrder = new ArrayList<>(Objects.requireNonNull(mOrders.getValue()));
        if(null != mOrders.getValue()){ moreOrder.addAll(mOrders.getValue()); }
        moreOrder.remove(order);
        mOrders.setValue(moreOrder);
    }


}



























