package com.example.finalyearproject.Modules;

import java.util.ArrayList;

public class User {
    public static final String USERNAME="username";

    private String id;
    private String username;
    private String phoneNo;
    private String email;
    private ArrayList<String> institutions=new ArrayList<String>();

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
