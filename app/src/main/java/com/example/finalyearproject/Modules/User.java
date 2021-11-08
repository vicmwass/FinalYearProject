package com.example.finalyearproject.Modules;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public static final String USERNAME="username";

    private String id;
    private String username;
    private String phoneNo;
    private String email;
    private ArrayList<String> institutions=new ArrayList<String>();
    private String imgUri;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getInstitutions() {
        return institutions;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public void setInstitutions(ArrayList<String> institutions) {
        this.institutions = institutions;
    }

    public void addInstitution(String institution) {
        this.institutions.add(institution);
    }

    public User withId(String id){
        this.id=id;
        return this;
    }

}
