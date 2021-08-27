package com.example.finalyearproject.Modules;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class Text {
    private String username;
    private String message;
    private String id;
    private Timestamp timeStamp;



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

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Text withId(String Id){
        setId(Id);
        return this;
    }
}
