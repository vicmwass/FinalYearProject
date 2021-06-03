package com.example.finalyearproject;

import java.util.ArrayList;

public class Domain {
    private String name;
    private String id;
    private ArrayList<String> adminList=new ArrayList<String>();


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
    public Domain withId(String Id){
        setId(Id);
        return this;
    }
    public ArrayList<String> getAdminList() {
        return adminList;
    }

    public void setAdminList(ArrayList<String> adminList) {
        this.adminList = adminList;
    }
    public void addAdmin(String admin){
        this.adminList.add(admin);
    }

}
