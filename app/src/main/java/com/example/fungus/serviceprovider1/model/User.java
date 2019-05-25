package com.example.fungus.serviceprovider1.model;

public class User {
    String id;
    String user_email;
    String user_Password;
    String name;
    int userType;
    String contact;

    public User() {
    }

    public User(String id, String user_email, String name, int userType, String contact) {
        this.id = id;
        this.user_email = user_email;
        this.name = name;
        this.userType = userType;
        this.contact = contact;
    }

    public User(String id, String user_email, String name, int userType){
        this.id = id;
        this.user_email = user_email;
        this.name = name;
        this.userType = userType;
    }

    public User(String user_email, String user_Password) {
        this.user_email = user_email;
        this.user_Password = user_Password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_ID() {
        return user_email;
    }

    public void setUser_ID(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_Password() {
        return user_Password;
    }

    public void setUser_Password(String user_Password) {
        this.user_Password = user_Password;
    }
}
