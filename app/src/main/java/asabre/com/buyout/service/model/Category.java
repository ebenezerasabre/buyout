package asabre.com.buyout.service.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("_id")
    private String _id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    public Category(String _id, String name, String image) {
        this._id = _id;
        this.name = name;
        this.image = image;
    }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
