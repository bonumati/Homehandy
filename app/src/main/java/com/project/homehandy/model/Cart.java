package com.project.homehandy.model;

import com.google.firebase.firestore.DocumentId;

public class Cart {
    @DocumentId
    String id;

    String user_id, user_name, service_name, service_type, user_address, service_price, date, timing;

    public Cart() {}

    public Cart(String user_id, String user_name, String service_name, String service_type, String user_address, String service_price,String date, String timing) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.service_name = service_name;
        this.service_type = service_type;
        this.user_address = user_address;
        this.service_price = service_price;
        this.date = date;
        this.timing = timing;
    }

    public String getId() {
        return id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getService_price() {
        return service_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}
