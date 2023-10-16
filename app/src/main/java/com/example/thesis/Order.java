package com.example.thesis;

public class Order {
    private String key;
    private String customerName;
    private Long orderTotal;
    private String time;

    public Order() {
        // Default constructor required for Firebase
    }



    public Order(String key, String customerName, Long orderTotal, String time) {
        this.key = key;
        this.customerName = customerName;
        this.orderTotal = orderTotal;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getOrderTotal() {
        return orderTotal;
    }

    public String getTime() {
        return time;
    }
}


