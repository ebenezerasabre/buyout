package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.ui.AdapterProduct;

public interface AddFpToWishListCallback {
    void addFpToWishList(Product product, AdapterProduct.ViewHolder holder);
}
