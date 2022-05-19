package com.application.epa.Models;

import java.util.ArrayList;
import java.util.Date;

public class LastMessageModel {

    public String senderUid = "";
    public Boolean isRead = false;
    public Date date = new Date();
    public String senderImage = "";
    public String senderName = "";
    public String senderToken = "";
    public String message = "";





    public static ArrayList<LastMessageModel> lastMessageModels = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    public static ArrayList<LastMessageModel> getUnreadMessages() {
        ArrayList<LastMessageModel> newlastMessageModels = new ArrayList<>();
        for (LastMessageModel lastMessageModel : lastMessageModels) {
            if (!lastMessageModel.isRead())  {
                newlastMessageModels.add(lastMessageModel);
            }
        }
        return newlastMessageModels;
    }
}
