package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.view.ui.AdapterCart;

public interface CartCallback {
    void productDetail(Cart cart);
    void removeCart(Cart cart);
    void increaseQuantity(Cart cart, AdapterCart.ViewHolder holder);
    void decreaseQuantity(Cart cart, AdapterCart.ViewHolder holder);

}
