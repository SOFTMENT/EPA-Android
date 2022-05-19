package com.application.epa.Models;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import com.application.epa.Utils.Services;

public class CategoryModel implements Serializable {

    public String id = "";
    public String title_pt = "";
    public String title_en = "";
    public String image = "";

    public static ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle_pt() {
        return title_pt;
    }

    public void setTitle_pt(String title_pt) {
        this.title_pt = title_pt;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static String getCategoryNameById(Context context,String id) {
        for (CategoryModel categoryModel : categoryModels) {
            if (categoryModel.id.contains(id)) {
                if (Services.getLocateCode(context).equalsIgnoreCase("pt")) {
                    return categoryModel.getTitle_pt();
                }
               else {
                    return categoryModel.getTitle_en();
                }
            }
        }
        return "Other";
    }
}
