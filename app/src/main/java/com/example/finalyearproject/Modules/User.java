package com.example.finalyearproject.Modules;

import java.util.ArrayList;

public class User {

    private String id;
    private ArrayList<String> institutions=new ArrayList<String>();

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
