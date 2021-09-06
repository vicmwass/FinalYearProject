package com.example.finalyearproject.Modules;

import com.google.firebase.Timestamp;

public class Text {
    private String userID;
    private String message;
    private String id;
    private Timestamp timeStamp;



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Text withId(String Id){
        setId(Id);
        return this;
    }
}
