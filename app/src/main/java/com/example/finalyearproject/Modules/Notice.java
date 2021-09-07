package com.example.finalyearproject.Modules;

import java.io.Serializable;

public class Notice implements Serializable {

    private String senderId;
    private String subject;
    private String fileUrl;
    private String fileName;
    private String domainName;
    private String description;
    private String Id;
    private Long timeStamp;
    private boolean isCommentable=false;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCommentable(boolean commentable) {
        isCommentable = commentable;
    }

    public boolean isCommentable() {
        return isCommentable;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Notice withId(String Id){
        setId(Id);
        return this;
    }
}
