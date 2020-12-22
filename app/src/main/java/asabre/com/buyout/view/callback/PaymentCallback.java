package asabre.com.buyout.view.callback;

public interface PaymentCallback {
    void payOnDelivery();
    void orderPlaceSuccessfully(String successMsg, String extraMsg);
}
