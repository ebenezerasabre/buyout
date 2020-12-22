package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.Product;

public interface ProductDetailCallback {
//    void addToWishList(Product product);
    void addToCart(Cart cart);
    void checkProductReview(String productId);
    void onUsersAlsoViewedSeeAll();
    void onFromSellerSeeAll();
    void onSimilarProductsSeeAll();
}








