package com.project.homehandy.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Order {
    @DocumentId
    String id;

    String user_id, user_name, user_address, services, cart_total, payment_mode, status;
    Date created_at;
    Double rating;
    Boolean delivered = false;

    public Order() {}

    public Order(String user_id, String user_name, String user_address, String services, String cart_total, String payment_mode, Boolean delivered, String status, Date created_at) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_address = user_address;
        this.services = services;
        this.cart_total = cart_total;
        this.payment_mode = payment_mode;
        this.delivered = delivered;
        this.status = status;
        this.created_at = created_at;
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

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getServices() {
        return services;
    }

    public void setCart_total(String cart_total) {
        this.cart_total = cart_total;
    }

    public String getCart_total() {
        return cart_total;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
