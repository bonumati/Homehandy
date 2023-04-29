package com.project.homehandy.model;

import com.google.firebase.firestore.DocumentId;

public class Service {
    @DocumentId
    String id;

    String name, description, regular_price, advance_price;

    public Service() { }

    public Service(String name, String description, String regular_price, String advance_price) {
        this.name = name;
        this.description = description;
        this.regular_price = regular_price;
        this.advance_price = advance_price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdvance_price() {
        return advance_price;
    }

    public void setAdvance_price(String advance_price) {
        this.advance_price = advance_price;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }
}
