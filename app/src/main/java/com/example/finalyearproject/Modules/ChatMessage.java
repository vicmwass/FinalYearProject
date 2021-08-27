package com.example.finalyearproject.Modules;


import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;


public class ChatMessage extends Text implements Serializable {
    private FieldValue timeStamp;
    public void addTimeStampToken(FieldValue timeStamp) {
        this.timeStamp=timeStamp;
    }

    public FieldValue getTimeStamp() {
        return timeStamp;
    }
}
