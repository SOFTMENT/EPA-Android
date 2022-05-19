package com.application.epa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.application.epa.Adapters.EditSubCategoriesAdapter;
import com.application.epa.Models.SubcategoryModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class ManageSubcategoryActivity extends AppCompatActivity {

    private ArrayList<SubcategoryModel> subcategoryModels;
    private String cat_id;
    private EditSubCategoriesAdapter editSubCategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subcategory);

        cat_id = getIntent().getStringExtra("cat_id");
        String categoryName = getIntent().getStringExtra("cat_name");

        TextView cat_name = findViewById(R.id.cat_name);
        cat_name.setText(categoryName);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //AddCategory
        findViewById(R.id.addSubategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageSubcategoryActivity.this,AddSubcategoryActivity.class);
                intent.putExtra("cat_id",cat_id);
                intent.putExtra("cat_name",categoryName);
                startActivity(intent);
            }
        });

        subcategoryModels = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.sub_categories_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editSubCategoriesAdapter = new EditSubCategoriesAdapter(this, subcategoryModels,cat_id);
        recyclerView.setAdapter(editSubCategoriesAdapter);


        getSubCategotyData();
    }

    public void getSubCategotyData() {
        ProgressHud.show(this,"");
        String field = "title_pt";
        if (Services.getLocateCode(this).equalsIgnoreCase("pt"))
            field = "title_pt";
        else
            field = "title_en";


        FirebaseFirestore.getInstance().collection("Categories").document(cat_id).collection("Subcategories").orderBy(field, Query.Direction.ASCENDING).addSnapshotListener(MetadataChanges.INCLUDE,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ProgressHud.dialog.dismiss();
                if (error == null) {
                    subcategoryModels.clear();
                    if (value != null && !value.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            SubcategoryModel subcategoryModel = documentSnapshot.toObject(SubcategoryModel.class);
                            subcategoryModels.add(subcategoryModel);
                        }

                    }
                    editSubCategoriesAdapter.notifyDataSetChanged();

                }
                else {
                    Services.showDialog(ManageSubcategoryActivity.this,"ERROR",error.getLocalizedMessage());
                }
            }
        });
    }
}

