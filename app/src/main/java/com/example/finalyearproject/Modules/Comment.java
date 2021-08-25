package com.example.finalyearproject.Modules;


import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;


public class Comment implements Serializable {
    private String username;
    private String message;
    private String id;
    private FieldValue timeStamp;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FieldValue getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(FieldValue timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Comment withId(String Id){
        setId(Id);
        return this;
    }
}
