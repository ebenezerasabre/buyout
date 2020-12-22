package asabre.com.buyout.service.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private static final long serialVersionUID = 110219951L;

    // let server is the fields we don't have to send when making an order
    // request from a cart, we will let the server deal with it

    private String _id;         // let server
    private String productId;
    private String productName;
    private String customerId;

    private String material;    // let server
    private String quantity;
    private String orderValue; // let server
    private String size;

    private String color;
    private String userGpsLocation;
    private String sellerId;    // let server
    private String[] images;    // let server

    private String thirdPartyId = "";

    public Cart() {
        quantity = "";
        size = "";
        color = "";
        userGpsLocation = "";
    }

    public Cart(String _id, String productId, String productName,
                String customerId, String material, String quantity,
                String orderValue, String size, String color,
                String userGpsLocation, String sellerId, String[] images) {
        this._id = _id;
        this.productId = productId;
        this.productName = productName;
        this.customerId = customerId;
        this.material = material;
        this.quantity = quantity;
        this.orderValue = orderValue;
        this.size = size;
        this.color = color;
        this.userGpsLocation = userGpsLocation;
        this.sellerId = sellerId;
        this.images = images;
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

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getOrderValue() {
        return orderValue;
    }
    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
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

    public String getUserGpsLocation() {
        return userGpsLocation;
    }
    public void setUserGpsLocation(String userGpsLocation) { this.userGpsLocation = userGpsLocation; }

    public String getSellerId() {
        return sellerId;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String[] getImages() {
        return images;
    }
    public void setImages(String[] images) {
        this.images = images;
    }

    public String getThirdPartyId() { return thirdPartyId; }
    public void setThirdPartyId(String thirdPartyId) { this.thirdPartyId = thirdPartyId; }


}
