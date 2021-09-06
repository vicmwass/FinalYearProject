package com.example.finalyearproject.Modules;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Institution implements Serializable, Parcelable {

    private String name;
    private String code;
    private String creator;
    private ArrayList<String> adminList=new ArrayList<String>();
    private ArrayList<String> users=new ArrayList<String>();
    public Institution(){}

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    protected Institution(Parcel in) {
        name = in.readString();
        code = in.readString();
        creator = in.readString();
        adminList = in.createStringArrayList();
        users = in.createStringArrayList();
    }

    public static final Creator<Institution> CREATOR = new Creator<Institution>() {
        @Override
        public Institution createFromParcel(Parcel in) {
            return new Institution(in);
        }

        @Override
        public Institution[] newArray(int size) {
            return new Institution[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(creator);
        dest.writeStringList(adminList);
        dest.writeStringList(users);

    }
}
