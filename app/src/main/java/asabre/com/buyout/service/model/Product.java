package asabre.com.buyout.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

public class Product implements Serializable {
    private static final long serialVersionUID = 110219951L;

    @SerializedName("_id")
    private String _id;
    @SerializedName("name")
    private String name;
    @SerializedName("about")
    private String about;
    @SerializedName("material")
    private String material;

    @SerializedName("availColors")
    private String availColors;
    @SerializedName("availSize")
    private String availSize;
    @SerializedName("price")
    private String price;
    @SerializedName("stock")
    private String stock;

    @SerializedName("sales")
    private double[] sales;
    @SerializedName("category")
    private String category;
    @SerializedName("subCategory")
    private String subCategory;
    @SerializedName("sellerId")
    private String sellerId;

    @SerializedName("discount")
    private String discount;
    @SerializedName("searchQuery")
    private String searchQuery;
    @SerializedName("status")
    private String status;
    @SerializedName("images")
    private String[] images;

    @SerializedName("productGroup")
    private String productGroup;

    public Product() {
    }

    public Product(String _id, String name, String about, String material,
                   String availColors, String availSize, String price, String stock,
                   double[] sales, String category, String subCategory, String sellerId, String discount,
                   String searchQuery, String status, String[] images, String productGroup) {
        this._id = _id;
        this.name = name;
        this.about = about;
        this.material = material;
        this.availColors = availColors;
        this.availSize = availSize;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
        this.category = category;
        this.subCategory = subCategory;
        this.sellerId = sellerId;
        this.discount = discount;
        this.searchQuery = searchQuery;
        this.status = status;
        this.images = images;
        this.productGroup = productGroup;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getAvailColors() {
        return availColors;
    }

    public void setAvailColors(String availColors) {
        this.availColors = availColors;
    }

    public String getAvailSize() {
        return availSize;
    }

    public void setAvailSize(String availSize) {
        this.availSize = availSize;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public double[] getSales() {
        return sales;
    }

    public void setSales(double[] sales) {
        this.sales = sales;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    @Override
    public String toString() {
        return "Product{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", about='" + about + '\'' +
                ", material='" + material + '\'' +
                ", availColors='" + availColors + '\'' +
                ", availSize='" + availSize + '\'' +
                ", price='" + price + '\'' +
                ", stock='" + stock + '\'' +
                ", sales=" + Arrays.toString(sales) +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", discount='" + discount + '\'' +
                ", searchQuery='" + searchQuery + '\'' +
                ", status='" + status + '\'' +
                ", images=" + Arrays.toString(images) +
                ", productGroup='" + productGroup + '\'' +
                '}';
    }
}
