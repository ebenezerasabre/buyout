package asabre.com.buyout.view.callback;

import java.util.List;

import asabre.com.buyout.service.model.Product;

public interface BoughtTogetherCallback {
    void addAllToWishList(List<Product> productList);
    void addAllToCart(List<Product> productList);
}
