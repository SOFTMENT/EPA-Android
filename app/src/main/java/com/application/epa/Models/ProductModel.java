package com.application.epa.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductModel implements Serializable {

    public String id = "";
    public String cat_id = "";
    public String uid = "";
    public String title = "";
    public String description = "";
    public Long price = 0L;
    public Integer quantity = 0;
    public String sub_cat_id = "";
    public boolean isProductNew = false;
    public boolean deliverProduct = false;
    public boolean sameDayDeliver = false;
    public Integer maxDeliverDay = 0;
    public Map<String,String> images = new HashMap<>();
    public Date date = new Date();
    public String storeCity = "";
    public Date adLastDate = new Date();
    public boolean isProductBlocked = false;

    public double latitude = 0.0;
    public double longitude = 0.0;


    public boolean isProductBlocked() {
        return isProductBlocked;
    }

    public void setProductBlocked(boolean productBlocked) {
        isProductBlocked = productBlocked;
    }

    public static ArrayList<ProductModel> myproductsModels = new ArrayList<>();

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public Date getAdLastDate() {
        return adLastDate;
    }

    public void setAdLastDate(Date adLastDate) {
        this.adLastDate = adLastDate;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public boolean isProductNew() {
        return isProductNew;
    }

    public void setProductNew(boolean productNew) {
        isProductNew = productNew;
    }

    public boolean isDeliverProduct() {
        return deliverProduct;
    }

    public void setDeliverProduct(boolean deliverProduct) {
        this.deliverProduct = deliverProduct;
    }

    public boolean isSameDayDeliver() {
        return sameDayDeliver;
    }

    public void setSameDayDeliver(boolean sameDayDeliver) {
        this.sameDayDeliver = sameDayDeliver;
    }

    public Integer getMaxDeliverDay() {
        return maxDeliverDay;
    }

    public void setMaxDeliverDay(Integer maxDeliverDay) {
        this.maxDeliverDay = maxDeliverDay;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
