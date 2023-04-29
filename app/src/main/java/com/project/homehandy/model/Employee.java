package com.project.homehandy.model;

import com.google.firebase.firestore.DocumentId;

public class Employee {
    @DocumentId
    String id;

    public String name, mobile, job_title, address;

    public Employee() {
    }

    public Employee(String name, String mobile, String job_title, String address) {
        this.name = name;
        this.mobile = mobile;
        this.job_title = job_title;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}