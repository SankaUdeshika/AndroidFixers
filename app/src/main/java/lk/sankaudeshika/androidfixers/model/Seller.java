package lk.sankaudeshika.androidfixers.model;

public class Seller {
    String Id;
    String email ;
    String locaiton;
    String mobile_1;
    String mobile_2;
    String password;
    String seller_company;
    String seller_name;
    String status;
    String sub_category;

    public Seller(String id, String email, String mobile_1, String seller_company, String seller_name, String status, String sub_category) {
        Id = id;
        this.email = email;
        this.mobile_1 = mobile_1;
        this.seller_company = seller_company;
        this.seller_name = seller_name;
        this.status = status;
        this.sub_category = sub_category;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocaiton() {
        return locaiton;
    }

    public void setLocaiton(String locaiton) {
        this.locaiton = locaiton;
    }

    public String getMobile_1() {
        return mobile_1;
    }

    public void setMobile_1(String mobile_1) {
        this.mobile_1 = mobile_1;
    }

    public String getMobile_2() {
        return mobile_2;
    }

    public void setMobile_2(String mobile_2) {
        this.mobile_2 = mobile_2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSeller_company() {
        return seller_company;
    }

    public void setSeller_company(String seller_company) {
        this.seller_company = seller_company;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
}
