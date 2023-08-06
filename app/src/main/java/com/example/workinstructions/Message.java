package com.example.workinstructions;


import android.graphics.Bitmap;

public class Message {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT="bot";
    public static String SENT_AS_PROMPT = "prompt";

    String message;
    String sentBy;
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
    public Message(Bitmap image, String sentBy){
        this.image=image;
        this.sentBy=sentBy;
    }
}