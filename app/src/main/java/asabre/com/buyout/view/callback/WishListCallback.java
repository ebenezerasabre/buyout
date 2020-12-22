package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Product;

public interface WishListCallback {
    void productDetail(Product product);
    void removeWishList(Product product);
}
