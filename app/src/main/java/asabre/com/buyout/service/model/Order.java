package asabre.com.buyout.service.model;

import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = 110219951L;

    private String _id;
    private String productId;
    private String customerId;
    private String productName;

    private String quantity;
    private String size;
    private String color;
    private String orderValue;

    private String status;
    private String delivery;
    private String productGroup;
    private String[] images;
    private String userGpsLocation;

    // for customerView
    private String customerName;
    private String customerPhoneNumber;
    private String customerLocation;

    /**
     * for thirdParty's
     * if thirdParty field of an order is not empty,
     * show thirdPartyOrderValue instead of orderValue at the client's side
     */

    private String thirdPartyId = "";
    private String thirdPartIncrease = "";
    private String thirdPartyOrderValue = "";


    public Order() {
    }

    public Order(String _id, String productId, String customerId,
                 String productName, String quantity, String size,
                 String color, String orderValue, String status,
                 String delivery, String productGroup, String[] images,
                 String userGpsLocation, String customerName, String customerPhoneNumber,
                 String customerLocation) {
        this._id = _id;
        this.productId = productId;
        this.customerId = customerId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.orderValue = orderValue;
        this.status = status;
        this.delivery = delivery;
        this.productGroup = productGroup;
        this.images = images;
        this.userGpsLocation = userGpsLocation;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerLocation = customerLocation;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getUserGpsLocation() {
        return userGpsLocation;
    }

    public void setUserGpsLocation(String userGpsLocation) {
        this.userGpsLocation = userGpsLocation;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
    public void setCustomerPhoneNumber(String customerPhoneNumber) { this.customerPhoneNumber = customerPhoneNumber; }

    public String getCustomerLocation() {
        return customerLocation;
    }
    public void setCustomerLocation(String customerLocation) { this.customerLocation = customerLocation; }

    // for thirdParties
    public String getThirdPartyId() { return thirdPartyId; }
    public void setThirdPartyId(String thirdPartyId) { this.thirdPartyId = thirdPartyId; }

    public String getThirdPartIncrease() { return thirdPartIncrease; }
    public void setThirdPartIncrease(String thirdPartIncrease) { this.thirdPartIncrease = thirdPartIncrease; }

    public String getThirdPartyOrderValue() { return thirdPartyOrderValue; }
    public void setThirdPartyOrderValue(String thirdPartyOrderValue) { this.thirdPartyOrderValue = thirdPartyOrderValue; }




}
