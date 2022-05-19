package com.application.epa.Models;

public class UserModel {

    public String uid = "";
    public String fullName = "";
    public String emailAddress = "";
    public String profileImage = "";
    public String token = "";
    public boolean isSeller = false;
    public String storeAbout = "";
    public String storeImage = "";
    public String storeCity = "";
    public String storeAddress = "";
    public String storeName = "";
    public String phoneNumber = "";
    public boolean isStoreBlocked = false;

    public boolean isStoreBlocked() {
        return isStoreBlocked;
    }

    public void setStoreBlocked(boolean storeBlocked) {
        isStoreBlocked = storeBlocked;
    }

    public static UserModel data  = new UserModel();

    public UserModel() {
        data = this;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStoreAbout() {
        return storeAbout;
    }

    public void setStoreAbout(String storeAbout) {
        this.storeAbout = storeAbout;
    }

   public boolean isSeller() {
        return isSeller;
    }

    public void setSeller(boolean seller) {
        isSeller = seller;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }




    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
