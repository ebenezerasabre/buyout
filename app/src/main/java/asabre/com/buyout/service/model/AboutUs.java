package asabre.com.buyout.service.model;

public class AboutUs {
    String _id;
    String title;
    String msg;

    public AboutUs() {
    }

    public AboutUs(String _id, String title, String msg) {
        this._id = _id;
        this.title = title;
        this.msg = msg;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
