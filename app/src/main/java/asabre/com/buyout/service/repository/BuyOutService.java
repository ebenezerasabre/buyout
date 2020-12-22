package asabre.com.buyout.service.repository;

import android.graphics.Outline;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Phaser;

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
import asabre.com.buyout.service.model.WishList;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BuyOutService {

    @GET("v1/category")
    Call<List<Category>> getCategories();

    @POST("v1/product/category/sub")
    Call<List<SubCategory>> getSubCategories(@Body List<HashMap<String, String>> body);

    @POST("v1/product/parts")
    Call<List<Product>> getProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/subCategory/parts")
    Call<List<Product>> getProductsByCategoryAndSubCategory(@Body List<HashMap<String, String>> body);

    @POST("v1/product/hotsales/parts")
    Call<List<Product>> getHotSales(@Body List<HashMap<String, String>> body);

    @POST("v1/product/trending/parts")
    Call<List<Product>> getTrendingProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/explore/parts")
    Call<List<Product>> getExploreProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/similar/parts")
    Call<List<Product>> getSimilarProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/seller")
    Call<List<Product>> getSellerRecommendation(@Body List<HashMap<String, String>> body);

    @POST("v1/product/viewed/parts")
    Call<List<Product>> getUsersViewed(@Body List<HashMap<String, String>> body);

    @POST("v1/product/boughtTogether")
    Call<List<Product>> getBoughtTogether(@Body List<HashMap<String, String>> body);

    @POST("v1/product/similarw/parts")
    Call<List<Product>> getSimilarWProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/search")
    Call<List<Product>> searchProducts(@Body List<HashMap<String, String>> body);

    @POST("v1/product/parts")
    Call<List<Product>> getCategoryProducts(@Body List<HashMap<String, String>> body);

    @GET("v1/product/today")
    Call<Product> getProductOfTheDay();


    // wish list
    @POST("v1/wishList/customer/parts")
    Call<List<Product>> getCustomerWishList(@Body List<HashMap<String, String>> body);

    @POST("v1/wishList")
    Call<Product> createWishList(@Body HashMap<String, String> body);

    @POST("v1/wishLists")
    Call<List<Product>> createWishLists(@Body List<HashMap<String, String>> body);

    @POST("v1/customer/wishList/delete")
    Call<OutLaw> deleteCustomerWishList(@Body HashMap<String, String> body);


    // cart
    @POST("v1/cart/customer")
    Call<List<Cart>> getCustomerCart(@Body List<HashMap<String, String>> body);

    @POST("v1/customer/cart/delete")
    Call<List<String>> deleteCustomerCart(@Body List<HashMap<String, String>> body);

    @POST("v1/cart")
    Call<Cart> createCart(@Body HashMap<String, String> body);

    @POST("v1/carts")
    Call<List<Cart>> createCarts(@Body List<HashMap<String, String>> body);

    @POST("v1/cart/thumbs")
    Call<OutLaw> cartThumbs(@Body HashMap<String, String> body);


    // order
    @POST("v1/order/sub")
    Call<List<Order>> getCustomerOrder(@Body List<HashMap<String, String>> body);

    @POST("v1/orders")
    Call<String[]> createOrders(@Body List<HashMap<String, String>> body);

    @POST("v1/order/delete")
    Call<OutLaw> deleteCustomerOrder(@Body HashMap<String, String> body);


    // customer
    @POST("v1/customer")
    Call<OutLaw> signUpCustomer(@Body HashMap<String, String> body);

    @POST("v1/customer/pwd")
    Call<Customer> signInCustomerByPhoneAndPwd(@Body HashMap<String, String > body);

    @POST("v1/customer/name")
    Call<Customer> findCustomerByNP(@Body HashMap<String, String> body);

    @Multipart
    @POST("v1/customer/image")
    Call<String> setCustomerImage(@Part MultipartBody.Part file, @Part("customerId") RequestBody customerId);

    @POST("v1/customer/update")
    Call<OutLaw> updateCustomer(@Body HashMap<String, String> body);

    @POST("v1/customer/reset")
    Call<OutLaw> resetCustomerPassword(@Body HashMap<String, String> body);

    // History
    @POST("v1/history/featured/customer")
    Call<List<Product>> getCustomerHistory(@Body List<HashMap<String, String>> body);

    @POST("v1/history/customer/parts")
    Call<List<Product>> getCustomerHistoryInParts(@Body List<HashMap<String, String>> body);


    // location
    @POST("v1/address/customer")
    Call<List<Address>> getCustomerAddress(@Body List<HashMap<String, String>> body);

    @POST("v1/address")
    Call<Address> createUserAddress(@Body HashMap<String, String> body);

    @POST("v1/address/update")
    Call<OutLaw> updateUserAddress(@Body HashMap<String, String> body);

    @POST("v1/customer/address/delete")
    Call<OutLaw> deleteCustomerAddress(@Body HashMap<String, String> body);


    // Review
    @POST("v1/review")
    Call<OutLaw> createProductReview(@Body HashMap<String, String> body);

    @POST("v1/review/product")
    Call<List<Review>> getProductReview(@Body List<HashMap<String, String>> body);

    @POST("v1/review/thumbs")
    Call<OutLaw> reviewThumbs(@Body HashMap<String, String> body);

    // home filter
    @GET("v1/aboutUs")
    Call<List<AboutUs>> getAboutUs();

    @GET("v1/news")
    Call<List<News>> getNews();

    @POST("v1/news/thumbs")
    Call<OutLaw> newsThumbs(@Body HashMap<String, String> body);


}




















