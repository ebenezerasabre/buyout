package asabre.com.buyout.service.repository;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
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
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private static final String TAG = HomeRepository.class.getSimpleName();
    private static BuyOutService mBuyOutService;
    private static HomeRepository homeRepository;

    // implementing singleton
    public static HomeRepository getInstance(){
        mBuyOutService = RetroClient.getInstance().create(BuyOutService.class);
        Log.d(TAG, "getInstance: HomeRepository instance called");
        if(homeRepository == null){
            homeRepository = new HomeRepository();
        }
        return homeRepository;
    }

    public MutableLiveData<List<Category>> getCategories(){
        final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
        Call<List<Category>> call = mBuyOutService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.isSuccessful()){
                    categories.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: categories request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d(TAG, "onFailure: categories request failure" + t.getMessage());
            }
        });
        return categories;
    }

    public MutableLiveData<List<SubCategory>> getSubCategories(List<HashMap<String, String>> body){
        final MutableLiveData<List<SubCategory>> subCategories = new MutableLiveData<>();
        Call<List<SubCategory>> call = mBuyOutService.getSubCategories(body);
        call.enqueue(new Callback<List<SubCategory>>() {
            @Override
            public void onResponse(Call<List<SubCategory>> call, Response<List<SubCategory>> response) {
                if(response.isSuccessful()){
                    subCategories.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting subCategories " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<SubCategory>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting subCategories " + t.getMessage());
            }
        });
        return subCategories;
    }

    public MutableLiveData<List<Product>> getProductsByCategoryAndSubCategory(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> products = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getProductsByCategoryAndSubCategory(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    products.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting products " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting products " + t.getMessage());
            }
        });
        return products;
    }

    public MutableLiveData<List<Product>> getHotSales(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> hotSales = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getHotSales(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    hotSales.setValue(response.body());
                }else {
                    Log.d(TAG, "onResponse: hotsales request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: hotsales request failure" + t.getMessage());
            }
        });
        return hotSales;
    }

    public MutableLiveData<List<Product>> getTrendingProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> trending = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getTrendingProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    trending.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting trending products " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting trending products " + t.getMessage());
            }
        });
        return trending;
    }

    public MutableLiveData<List<Product>> getExploreProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> explore = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getExploreProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    explore.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting explore products " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting explore products " + t.getMessage());
            }
        });
        return explore;
    }

    public MutableLiveData<List<Product>> getProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> products = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: products in parts");
                    products.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: products request failure" + t.getMessage());
            }
        });
        return products;
    }

    public MutableLiveData<List<Product>> getSimilarProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> similarProducts = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getSimilarProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    similarProducts.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: similar products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: similar products request failure" + t.getMessage());
            }
        });
        return similarProducts;
    }

    public MutableLiveData<List<Product>> getSellerRecommendation(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> sellerRecommendation = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getSellerRecommendation(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    sellerRecommendation.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting seller recommendation ");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: seller recommendation request failure " + t.getMessage());
            }
        });
        return sellerRecommendation;
    }

    public MutableLiveData<List<Product>> getUsersViewed(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> usersViewed = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getUsersViewed(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    usersViewed.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting users also viewed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: users also viewed request failure " + t.getMessage());
            }
        });
        return usersViewed;
    }

    public MutableLiveData<List<Product>> getSimilarWProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> similarProducts = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getSimilarWProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: success similar");
                    similarProducts.setValue(response.body());
                }else {
                    Log.d(TAG, "onResponse: similar products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: similar products request failure" + t.getMessage());
            }
        });
        return similarProducts;
    }

    public MutableLiveData<List<Product>> getBoughtTogether(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> boughtTogetherProducts = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getBoughtTogether(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: success similar");
                    boughtTogetherProducts.setValue(response.body());
                }else {
                    Log.d(TAG, "onResponse: similar products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: similar products request failure" + t.getMessage());
            }
        });
        return boughtTogetherProducts;
    }

    public MutableLiveData<List<Product>> getSearchProducts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> searchedItems = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.searchProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: searched items " + response.body());
                    searchedItems.setValue(response.body());
                }else {
                    Log.d(TAG, "onResponse: searched products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: searched products request failure" + t.getMessage());
            }
        });
        return searchedItems;
    }

    public MutableLiveData<List<Product>> getCategoryProducts(final List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> categoryProducts = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getCategoryProducts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: category products " + response.body());
                    categoryProducts.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: category products request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: category products request failure" + t.getMessage());
            }
        });
        return categoryProducts;
    }

    public MutableLiveData<Product> getProductOfTheDay(){
        final MutableLiveData<Product> product = new MutableLiveData<>();
        Call<Product> call = mBuyOutService.getProductOfTheDay();
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()){
                    product.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse:  error getting product of the day " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting product of the day " + t.getMessage());
            }
        });
        return product;
    }



    // wish list
   public MutableLiveData<Product> createWishList(HashMap<String, String> body){
        final MutableLiveData<Product> wishList = new MutableLiveData<>();
        Call<Product> call = mBuyOutService.createWishList(body);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()){
                    wishList.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error creating wishList " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.d(TAG, "onFailure: creating wishList request failure " + t.getMessage());
            }
        });
        return wishList;
   }

   public MutableLiveData<List<Product>> createWishLists(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> wishLists = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.createWishLists(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    wishLists.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error creating wishLists " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure creating wishLists " + t.getMessage());
            }
        });
        return wishLists;
   }

    public MutableLiveData<List<Product>> getCustomerWishList(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> products = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getCustomerWishList(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
               if(response.isSuccessful()){
                   products.setValue(response.body());
               } else {
                   Log.d(TAG, "onResponse: wishList request error " + response.errorBody());
               }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: wishList request failure" + t.getMessage());
            }
        });
        return products;
    }

    public MutableLiveData<OutLaw> deleteCustomerWishList(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outLaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.deleteCustomerWishList(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    outLaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: delete customer wishList error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: delete customer wishList failure " + t.getMessage());
            }
        });
        return outLaw;
    }


    // cart
   public MutableLiveData<Cart> createCart(HashMap<String, String> body){
        final MutableLiveData<Cart> cart = new MutableLiveData<>();
        Call<Cart> call = mBuyOutService.createCart(body);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if(response.isSuccessful()){
                    cart.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: creating cart error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.d(TAG, "onFailure: creating cart request failure " + t.getMessage());
            }
        });
        return cart;
   }

   public MutableLiveData<List<Cart>> createCarts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Cart>> carts = new MutableLiveData<>();
        Call<List<Cart>> call = mBuyOutService.createCarts(body);
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if(response.isSuccessful()){
                    carts.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error creating carts " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure creating carts " + t.getMessage());
            }
        });
        return carts;
   }

    public MutableLiveData<List<Cart>> getCustomerCart(List<HashMap<String, String>> body){
        final MutableLiveData<List<Cart>> cart = new MutableLiveData<>();
        Call<List<Cart>> call = mBuyOutService.getCustomerCart(body);
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: customer cart " + response.body());
                    cart.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: cart request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Log.d(TAG, "onFailure: cart request failure " + t.getMessage());
            }
        });
        return cart;
    }

    public MutableLiveData<List<String>> deleteCustomerCart(List<HashMap<String, String>> body){
        final MutableLiveData<List<String>> cartDelete = new MutableLiveData<>();
        Call<List<String>> call = mBuyOutService.deleteCustomerCart(body);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
                    cartDelete.setValue(response.body());
                }else {
                    Log.d(TAG, "onResponse: cart delete request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d(TAG, "onFailure: cart request failure " + t.getMessage());
            }
        });
        return cartDelete;
    }

    public MutableLiveData<OutLaw> cartThumbs(HashMap<String, String> body){
        MutableLiveData<OutLaw> thumbsRate = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.cartThumbs(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                Log.d(TAG, "onResponse: thumbs " + response.body());
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return thumbsRate;
    }


    // order
    public MutableLiveData<String[]> createOrder(List<HashMap<String, String>> body){
        final MutableLiveData<String[]> orders = new MutableLiveData<>();
        Call<String[]> call = mBuyOutService.createOrders(body);
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if(response.isSuccessful()){
                    orders.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: order create request error");
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Log.d(TAG, "onFailure: order create request failure " + t.getMessage());
            }
        });
        return orders;
    }

    public MutableLiveData<List<Order>> getCustomerOrder(List<HashMap<String, String>> body){
        final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
        Call<List<Order>> call = mBuyOutService.getCustomerOrder(body);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: customer orders " + response.body());
                    orders.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: order request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d(TAG, "onFailure: order request failure " + t.getMessage());
            }
        });
        return orders;
    }

    public MutableLiveData<OutLaw> deleteCustomerOrder(HashMap<String, String> body){
        final MutableLiveData<OutLaw> del = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.deleteCustomerOrder(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    del.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error deleting order " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: request failure deleting customer order " + t.getMessage());
            }
        });
        return del;
    }

    // customer
    public MutableLiveData<OutLaw> signUpCustomer(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outLaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.signUpCustomer(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if (response.isSuccessful()) {
                    outLaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error signing up customer " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: signing up customer failure " + t.getMessage());
            }
        });
        return outLaw;
    }

    public MutableLiveData<List<Product>> getCustomerHistory(List<HashMap<String, String>> body){
       final MutableLiveData<List<Product>> history = new MutableLiveData<>();
      Call<List<Product>> call = mBuyOutService.getCustomerHistory(body);
      call.enqueue(new Callback<List<Product>>() {
          @Override
          public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
              if(response.isSuccessful()){
                  history.setValue(response.body());
              } else {
                  Log.d(TAG, "onResponse: error getting customer history " + response.body());
              }
          }

          @Override
          public void onFailure(Call<List<Product>> call, Throwable t) {
              Log.d(TAG, "onFailure: customer history request failure " + t.getMessage());
          }
      });
      return history;
    }

    public MutableLiveData<List<Product>> getCustomerHistoryInParts(List<HashMap<String, String>> body){
        final MutableLiveData<List<Product>> history = new MutableLiveData<>();
        Call<List<Product>> call = mBuyOutService.getCustomerHistoryInParts(body);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    history.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting customer history " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: customer history request failure " + t.getMessage());
            }
        });
        return history;
    }

    public MutableLiveData<Customer> signInCustomerByPhoneAndPwd(HashMap<String, String> body){
        final MutableLiveData<Customer> customer = new MutableLiveData<>();
        Call<Customer> call = mBuyOutService.signInCustomerByPhoneAndPwd(body);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if(response.isSuccessful()){
                    customer.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: signInByPhoneAndPwd request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.d(TAG, "onFailure: signInByPhoneAndPwd request failure " + t.getMessage());
            }
        });
        return customer;
    }

    public MutableLiveData<Customer> findCustomerByNP(HashMap<String, String> body){
        final MutableLiveData<Customer> customer = new MutableLiveData<>();
        Call<Customer> call = mBuyOutService.findCustomerByNP(body);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if(response.isSuccessful()){
                    customer.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: findCustomerByNP request error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.d(TAG, "onFailure: findCustomerByNP request failure " + t.getMessage());
            }
        });
        return customer;
    }

    public MutableLiveData<String> setCustomerImage(MultipartBody.Part file, RequestBody customerId){
       final MutableLiveData<String> msg = new MutableLiveData<>();
        Call<String> call = mBuyOutService.setCustomerImage(file, customerId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    msg.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: setCustomerImage request error " + response.errorBody());
                }
                ViewModelHomeFragment.updatingUser.setValue(true);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: setCustomerImage request failure " + t.getMessage());
                ViewModelHomeFragment.updatingUser.setValue(true);
            }
        });
        return msg;
    }

    public MutableLiveData<OutLaw> updateCustomer(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outLaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.updateCustomer(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    outLaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error updating customer " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: failure updating customer " + t.getMessage());
            }
        });
        return outLaw;
    }

    public MutableLiveData<OutLaw> resetCustomerPassword(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outLaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.resetCustomerPassword(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    outLaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error resetting password " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: failure resetting password " + t.getMessage());
            }
        });
        return outLaw;
    }


    // customer address
    public MutableLiveData<List<Address>> getCustomerAddress(List<HashMap<String, String>> body){
        final MutableLiveData<List<Address>> address = new MutableLiveData<>();
        Call<List<Address>> call = mBuyOutService.getCustomerAddress(body);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if(response.isSuccessful()){
                    address.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: getting customer address error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting customer's address " + t.getMessage());
            }
        });
        return address;
    }

    public MutableLiveData<Address> createUserAddress(HashMap<String, String> body){
        final MutableLiveData<Address> address = new MutableLiveData<>();
        Call<Address> call = mBuyOutService.createUserAddress(body);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if(response.isSuccessful()){
                    address.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error creating user address " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.d(TAG, "onFailure: creating user address request failure " + t.getMessage());
            }
        });
        return address;
    }

    public MutableLiveData<OutLaw> updateUserAddress(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outlaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.updateUserAddress(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    outlaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error updating user address " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: updating user address request failure " + t.getMessage());
            }
        });
        return outlaw;
    }

    public MutableLiveData<OutLaw> deleteCustomerAddress(HashMap<String, String> body){
        final MutableLiveData<OutLaw> outLaw = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.deleteCustomerAddress(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    outLaw.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error deleting customer address " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: failure deleting customer address " + t.getMessage());
            }
        });
        return outLaw;
    }

    // Product review
    public MutableLiveData<OutLaw> createProductReview(HashMap<String, String> body){
        final MutableLiveData<OutLaw> review = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.createProductReview(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    review.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error creating product review " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: creating review request failure " + t.getMessage());
            }
        });
        return review;
    }

    public MutableLiveData<List<Review>> getProductReview(List<HashMap<String, String>> body){
        final MutableLiveData<List<Review>> reviews = new MutableLiveData<>();
        Call<List<Review>> call = mBuyOutService.getProductReview(body);
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    reviews.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting product review " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.d(TAG, "onFailure: getting product review request failure " + t.getMessage());
            }
        });
        return reviews;
    }

    public MutableLiveData<OutLaw> reviewThumb(HashMap<String, String> body){
        final MutableLiveData<OutLaw> thumbs = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.reviewThumbs(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                if(response.isSuccessful()){
                    thumbs.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: couldn't thumb review " + t.getMessage());
            }
        });
        return thumbs;
    }

    public MutableLiveData<List<AboutUs>> getAboutUs(){
        final MutableLiveData<List<AboutUs>> aboutUs = new MutableLiveData<>();
        Call<List<AboutUs>> call = mBuyOutService.getAboutUs();
        call.enqueue(new Callback<List<AboutUs>>() {
            @Override
            public void onResponse(Call<List<AboutUs>> call, Response<List<AboutUs>> response) {
                if(response.isSuccessful()){
                    aboutUs.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting aboutUs " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<AboutUs>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting about us " + t.getMessage());
            }
        });
        return aboutUs;
    }

    public MutableLiveData<List<News>> getNews(){
        final MutableLiveData<List<News>> news = new MutableLiveData<>();
        Call<List<News>> call = mBuyOutService.getNews();
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if(response.isSuccessful()){
                    news.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: error getting news " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Log.d(TAG, "onFailure: failure getting news " + t.getMessage());
            }
        });
        return news;
    }

    public MutableLiveData<OutLaw> newsThumbs(HashMap<String, String> body){
        MutableLiveData<OutLaw> thumbsRate = new MutableLiveData<>();
        Call<OutLaw> call = mBuyOutService.newsThumbs(body);
        call.enqueue(new Callback<OutLaw>() {
            @Override
            public void onResponse(Call<OutLaw> call, Response<OutLaw> response) {
                Log.d(TAG, "onResponse: news thumbs " + response.body());
            }

            @Override
            public void onFailure(Call<OutLaw> call, Throwable t) {
                Log.d(TAG, "onFailure: news thumbs " + t.getMessage());
            }
        });
        return thumbsRate;
    }


}






























