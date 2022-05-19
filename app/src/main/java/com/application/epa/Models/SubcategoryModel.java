package com.application.epa.Models;


import java.io.Serializable;

public class SubcategoryModel implements Serializable {

    public String id = "";
    public String title_pt = "";
    public String title_en = "";




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


//    public static String getSubcategoryNameById(String id) {
//        for (SubcategoryModel categoryModel : subCategoryModels) {
//            if (categoryModel.id.contains(id)) {
//                if (MyLanguage.lang.equalsIgnoreCase("pt")) {
//                    return categoryModel.getTitle_pt();
//                }
//                else {
//                    return categoryModel.getTitle_en();
//                }
//            }
//        }
//        return "Other";
//    }
}
