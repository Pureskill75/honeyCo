package atomsandbots.android.sweepsnatch.user.Model;

public class UserModel {
    private String Name, Email, Phone, Image, Country, Postcode, Address;

    public UserModel() {
    }

    public UserModel(String name, String email, String phone, String image, String country, String postcode, String address) {
        Name = name;
        Email = email;
        Phone = phone;
        Image = image;
        Country = country;
        Postcode = postcode;
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }
    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
