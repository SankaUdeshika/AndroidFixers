package lk.sankaudeshika.androidfixers.model;

public class PendingOrders {
    String OrderText ;

    public PendingOrders() {
    }
    public PendingOrders(String text) {
        OrderText = text;
    }

    public String getOrderText() {
        return OrderText;
    }

    public void setOrderText(String orderText) {
        OrderText = orderText;
    }
}
