package lk.sankaudeshika.androidfixers.model;

public class PendingOrders {
    String customerMobile;
    String date;

    public PendingOrders(String customerMobile, String date, String time, String bookingID) {
        this.customerMobile = customerMobile;
        this.date = date;
        this.time = time;
        this.bookingID = bookingID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    String time;
    String bookingID;

    public PendingOrders() {
    }


}
