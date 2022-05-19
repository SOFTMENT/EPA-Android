package com.application.epa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.application.epa.Adapters.SeeAllCategoriesAdapter;
import com.application.epa.Models.CategoryModel;


public class SeeAllCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_category);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SeeAllCategoriesAdapter seeAllCategoriesAdapter = new SeeAllCategoriesAdapter(this, CategoryModel.categoryModels);
        recyclerView.setAdapter(seeAllCategoriesAdapter);


        //back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}