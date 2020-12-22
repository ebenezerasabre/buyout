package asabre.com.buyout.service.model;

public class Review {
    private String _id;
    private String productId;
    private String customerId;
    private String customerName;

    private String sellerId = "";
    private String msg;
    private String time;
    private String stars;

    private String[] thumbs;       // let server

    public Review() {
    }

    public Review(String _id,
                  String productId,
                  String customerId,
                  String customerName,
                  String sellerId,
                  String msg,
                  String time,
                  String stars,
                  String[] thumbs) {
        this._id = _id;
        this.productId = productId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.sellerId = sellerId;
        this.msg = msg;
        this.time = time;
        this.stars = stars;
        this.thumbs = thumbs;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String[] getThumbs() {
        return thumbs;
    }

    public void setThumbs(String[] thumbs) {
        this.thumbs = thumbs;
    }
}
