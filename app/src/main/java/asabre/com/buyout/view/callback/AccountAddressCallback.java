package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Address;

public interface AccountAddressCallback {
    void editAddress(Address address);
    void viewAddress(Address address);
}
