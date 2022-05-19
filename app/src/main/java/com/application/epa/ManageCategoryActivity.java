package com.application.epa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.application.epa.Adapters.EditCategoriesAdapter;
import com.application.epa.Models.CategoryModel;



public class ManageCategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //AddCategory
        findViewById(R.id.addCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageCategoryActivity.this, AddCateogryScreen.class));
            }
        });

        RecyclerView recyclerView = findViewById(R.id.categories_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EditCategoriesAdapter editCategoriesAdapter = new EditCategoriesAdapter(this, CategoryModel.categoryModels);
        recyclerView.setAdapter(editCategoriesAdapter);
    }




}