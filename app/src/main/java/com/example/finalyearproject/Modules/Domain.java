package com.example.finalyearproject.Modules;

import java.util.ArrayList;

public class Domain {
    private String name;
    private String id;
    private ArrayList<String> adminList=new ArrayList<String>();
    private Boolean isPrivate=false;


    private ArrayList<String> memberList=new ArrayList<String>();

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

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public ArrayList<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        this.memberList = memberList;
    }


}
