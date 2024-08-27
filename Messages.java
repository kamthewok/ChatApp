package com.example.secchatapp;

public class Messages {

    String message;
    String senderid;
    long timemessage;
    String currenttime;

    public Messages() {
    }

    public Messages(String message, String senderid, long timemessage, String currenttime) {
        this.message = message;
        this.senderid = senderid;
        this.timemessage = timemessage;
        this.currenttime = currenttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimemessage() {
        return timemessage;
    }

    public void setTimemessage(long timemessage) {
        this.timemessage = timemessage;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }
}
