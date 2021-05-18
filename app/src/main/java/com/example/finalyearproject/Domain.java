package com.example.finalyearproject;

public class Domain {
    private String name;
    private String id;


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

}
