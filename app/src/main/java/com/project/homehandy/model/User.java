package com.project.homehandy.model;

import com.google.firebase.firestore.DocumentId;

public class User {
    @DocumentId
    String id;

    public String name, mobile, email, instanceId, type;

    public User() {
    }

    public User(String mobile, String instanceId, String type) {
        this.mobile = mobile;
        this.instanceId = instanceId;
        this.type = type;
    }

    public User(String name, String mobile, String email, String instanceId, String type) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.instanceId = instanceId;
        this.type = type;
    }

    public User(String instanceId) {
        this.instanceId = instanceId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}