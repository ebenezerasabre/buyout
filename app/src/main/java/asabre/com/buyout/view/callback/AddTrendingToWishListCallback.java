package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.ui.AdapterTrendingProducts;

public interface AddTrendingToWishListCallback {
    void addTrendingToWishList(Product product, AdapterTrendingProducts.ViewHolder holder);
}
