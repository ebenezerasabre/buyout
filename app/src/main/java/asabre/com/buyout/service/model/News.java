package asabre.com.buyout.service.model;

public class News {
    private String _id;
    private String source;
    private String date;
    private String msg;
    private String img;
    private String[] thumbs;

    public News() {
    }

    public News(String _id, String source, String date, String msg, String img, String[] thumbs) {
        this._id = _id;
        this.source = source;
        this.date = date;
        this.msg = msg;
        this.img = img;
        this.thumbs = thumbs;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String[] getThumbs() {
        return thumbs;
    }

    public void setThumbs(String[] thumbs) {
        this.thumbs = thumbs;
    }
}
