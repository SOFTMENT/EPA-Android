package com.application.epa.Models;

import java.util.Date;

public class FavoritesModel {
    public String productId = "";
    public Date date = new Date();

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
