package asabre.com.buyout.service.model;


public class ProductImage {
    private String img;

    public ProductImage(String img) { this.img = img; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    @Override
    public String toString() {
        return "ProductImage{" +
                "img='" + img + '\'' +
                '}';
    }
}
