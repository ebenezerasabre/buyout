package asabre.com.buyout.service.model;

public class OutLaw {
    private String msg;
    private String _id;

    public OutLaw(String msg) {
        this.msg = msg;
    }

    public OutLaw(String msg, String _id) {
        this.msg = msg;
        this._id = _id;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String get_id() { return _id; }
    public void set_id(String _id) {
        this._id = _id;
    }
}
