package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.application.epa.Adapters.ProductAdapter;
import com.application.epa.Models.FavoritesModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.Utils.ProgressHud;

public class FavouritesActivity extends AppCompatActivity {

    private final ArrayList<ProductModel> productModels = new ArrayList<>();
    private TextView no_fav_available;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        no_fav_available = findViewById(R.id.no_fav_available);

        RecyclerView favoritesRecyclerview = findViewById(R.id.favorites_recyclerview);
        favoritesRecyclerview.setHasFixedSize(true);
        favoritesRecyclerview.setItemViewCacheSize(20);
        favoritesRecyclerview.setDrawingCacheEnabled(true);
        favoritesRecyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        favoritesRecyclerview.setLayoutManager(staggeredGridLayoutManager);
        productAdapter = new ProductAdapter(this, productModels);
        favoritesRecyclerview.setAdapter(productAdapter);

        getAllProductId();

    }

    private void getAllProductId() {
        ProgressHud.show(this,getString(R.string.loading));
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favourites").orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (!task.getResult().isEmpty()) {
                        productModels.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            FavoritesModel favoritesModel = documentSnapshot.toObject(FavoritesModel.class);
                            if (favoritesModel != null) {
                                getProduct(favoritesModel.productId);
                            }
                        }
                    }
                    else {
                        no_fav_available.setVisibility(View.VISIBLE);
                        ProgressHud.dialog.dismiss();
                    }

                }
                else {

                    no_fav_available.setVisibility(View.VISIBLE);

                    ProgressHud.dialog.dismiss();
                }
            }
        });
    }

    private void getProduct(String productId) {
            FirebaseFirestore.getInstance().collection("Products").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    ProgressHud.dialog.dismiss();
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            ProductModel productModel = task.getResult().toObject(ProductModel.class);
                            productModels.add(productModel);
                            productAdapter.notifyDataSetChanged();
                        }
                        if (productModels.size() > 0) {
                            no_fav_available.setVisibility(View.GONE);
                        }
                        else {
                            no_fav_available.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        no_fav_available.setVisibility(View.VISIBLE);
                    }
                }
            });
    }
}