package com.example.finalyearproject.Modules;

import java.io.Serializable;
import java.util.ArrayList;

public class Institution implements Serializable {

    private String name;
    private String code;
    private String creator;
    private ArrayList<String> adminList=new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
