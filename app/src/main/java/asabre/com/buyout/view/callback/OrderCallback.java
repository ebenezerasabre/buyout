package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Order;

public interface OrderCallback {
    void orderDetails(Order order);
    void leaveReview(String productId, String customerId);
}
