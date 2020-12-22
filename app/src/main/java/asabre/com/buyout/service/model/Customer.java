package asabre.com.buyout.service.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 110219951L;

    private String _id;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String password;
    private String email;
    private int rating = 3;
    private String dateAccountWasCreated;

    private String trackUserAddress;
    private String userImage;

    public Customer() {
    }

    public Customer(String _id,
                    String firstName,
                    String lastName,
                    String phoneNumber,
                    String password,
                    String email,
                    int rating,
                    String dateAccountWasCreated,
                    String trackUserAddress,
                    String userImage) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.rating = rating;
        this.dateAccountWasCreated = dateAccountWasCreated;
        this.trackUserAddress = trackUserAddress;
        this.userImage = userImage;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDateAccountWasCreated() {
        return dateAccountWasCreated;
    }

    public void setDateAccountWasCreated(String dateAccountWasCreated) {
        this.dateAccountWasCreated = dateAccountWasCreated;
    }

    public String getTrackUserAddress() {
        return trackUserAddress;
    }

    public void setTrackUserAddress(String trackUserAddress) {
        this.trackUserAddress = trackUserAddress;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
