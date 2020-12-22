package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Address;

public interface LocationDialogCallback {
    void addLocation(String location,
                     String houseNo,
                     String landmark,
                     String name,
                     String tag);
    void useLocation(String addressId);
    void updateLocation(Address address);
}
