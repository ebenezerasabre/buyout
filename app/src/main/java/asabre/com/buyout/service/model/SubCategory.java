package asabre.com.buyout.service.model;

public class SubCategory {
    private String category;
    private String subCategory;

    public SubCategory() {}
    public SubCategory(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    @Override
    public String toString() {
        return "SubCategory{" +
                "category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                '}';
    }
}
