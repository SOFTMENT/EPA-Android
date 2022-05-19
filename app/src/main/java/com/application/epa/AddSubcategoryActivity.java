package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;


public class AddSubcategoryActivity extends AppCompatActivity {

    private EditText cat_title_pt;
    private EditText cat_title_en;
    private AppCompatButton addSubCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subcategory);

        String cat_id = getIntent().getStringExtra("cat_id");
        String categoryName = getIntent().getStringExtra("cat_name");

        TextView cat_name = findViewById(R.id.cat_name);
        cat_name.setText(categoryName);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        //Cat_title
        cat_title_pt = findViewById(R.id.categoryNamePT);
        cat_title_en = findViewById(R.id.categoryNameEN);

        //AddCategory
        addSubCat = findViewById(R.id.addCategory);
        addSubCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_pt = cat_title_pt.getText().toString().trim();
                String title_en = cat_title_en.getText().toString().trim();

                    if (title_pt.isEmpty()) {
                        Services.showCenterToast(AddSubcategoryActivity.this,"Enter Title");
                    }
                    else {
                        if (title_en.isEmpty()) {
                            Services.showCenterToast(AddSubcategoryActivity.this,"Enter English Title");
                        }
                        else {
                            addSubCat.setEnabled(false);
                            String sub_cat_id =  FirebaseFirestore.getInstance().collection("Categories").document(cat_id).collection("Subcategories").document().getId();
                            uploadCategoryData(cat_id,sub_cat_id,title_pt,title_en);
                        }
                    }
                }

        });


    }


    public void uploadCategoryData(String cat_id,String sub_cat_id,String title_pt, String title_en) {
        Map<String, String> map = new HashMap<>();
        map.put("id",sub_cat_id);
        map.put("title_pt",title_pt);
        map.put("title_en",title_en);

        ProgressHud.show(AddSubcategoryActivity.this,"Adding...");
        FirebaseFirestore.getInstance().collection("Categories").document(cat_id).collection("Subcategories").document(sub_cat_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Services.showCenterToast(AddSubcategoryActivity.this,"Subcategory Added");

                    cat_title_pt.setText("");
                    cat_title_en.setText("");
                    addSubCat.setEnabled(true);

                }
                else {
                    addSubCat.setEnabled(true);
                    Services.showDialog(AddSubcategoryActivity.this,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }



}