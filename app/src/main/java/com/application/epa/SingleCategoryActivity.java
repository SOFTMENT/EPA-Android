package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.application.epa.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.application.epa.Adapters.ProductAdapter;
import com.application.epa.Models.ProductModel;
import com.application.epa.Utils.Services;

public class SingleCategoryActivity extends AppCompatActivity {
    private RecyclerView products_recyclerview;
    private ProductAdapter productAdapter;
    private ArrayList<ProductModel> productModels, mainModels;
    private TextView no_product_available;
    private ShimmerRecyclerView shimmerFrameLayout;
    private SpinKitView progressBar;
    private boolean isLoading = false, isMaxData = false;
    private final boolean hasSearched = false;
    private Date lastNode = null, lastKey = null;
    private final String []promotedAdID = new String[2];
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category);

        String cat_id = getIntent().getStringExtra("cat_id");
        String cat_name = getIntent().getStringExtra("cat_name");
        String sub_cat_id = getIntent().getStringExtra("sub_cat_id");
        String sub_cat_name = getIntent().getStringExtra("sub_cat_name");

        shimmerFrameLayout = findViewById(R.id.shimmer_recycler_view);
        no_product_available = findViewById(R.id.no_product_available);
        products_recyclerview = findViewById(R.id.product_recyclerview);
        products_recyclerview.setHasFixedSize(true);
        products_recyclerview.setItemViewCacheSize(20);
        products_recyclerview.setDrawingCacheEnabled(true);
        products_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        products_recyclerview.setLayoutManager(staggeredGridLayoutManager);


        products_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    recyclerView.invalidateItemDecorations();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {

                    visibleItemCount = staggeredGridLayoutManager.getChildCount();
                    totalItemCount = staggeredGridLayoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                    if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                        pastVisibleItems = firstVisibleItems[0];
                    }

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                          isLoading = true;
                          getLatestProduct(cat_id, sub_cat_id);
                        }
                     }
                    }

            }
        });

        productModels = new ArrayList<>();
        mainModels = new ArrayList<>();

        productAdapter = new ProductAdapter(this, mainModels);
        products_recyclerview.setAdapter(productAdapter);

        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);




        TextView categoryName = findViewById(R.id.categoryName);
        categoryName.setText(cat_name);

        TextView subCategoryName = findViewById(R.id.sub_cat_name);
        subCategoryName.setText(sub_cat_name);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //SearchETTEXT
        EditText searchET = findViewById(R.id.searchEditText);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productAdapter.filter(s.toString(),productModels);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //getproducitems
        getLastKey(cat_id,sub_cat_id);
    }


    public void getLastKey(String cat_id,String sub_cat_id){
        FirebaseFirestore.getInstance().collection("Products").orderBy("date").whereEqualTo("cat_id",cat_id).whereEqualTo("sub_cat_id",sub_cat_id).whereEqualTo("isProductBlocked",false).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {

                        for (DocumentSnapshot documentSnapshot :  task.getResult().getDocuments()) {
                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                            lastKey = productModel.getDate();
                            getPromotedProduct(cat_id, sub_cat_id);
                        }

                    }
                    else {
                        no_product_available.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        products_recyclerview.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    no_product_available.setVisibility(View.VISIBLE);
                    Services.showDialog(SingleCategoryActivity.this,getString(R.string.error),getString(R.string.something_went_wrong));
                }
            }
        });
    }

    public void getPromotedProduct(String cat_id,String sub_cat_id){
        FirebaseFirestore.getInstance().collection("Products").orderBy("adLastDate").whereGreaterThan("adLastDate",new Date()).whereEqualTo("cat_id",cat_id).whereEqualTo("sub_cat_id",sub_cat_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    promotedAdID[0] = "123";
                    promotedAdID[1] = "456";
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        ArrayList<ProductModel> productModels1 = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                            if (productModel.getImages().get("0") != null) {
                                productModels1.add(productModel);

                            }
                        }
                        Collections.shuffle(productModels1);
                        int i = 0;
                        for (ProductModel productModel : productModels1) {
                            if (i>=2) {
                                break;
                            }
                            promotedAdID[i] = productModel.getId();
                            productModels.add(productModel);
                            i++;
                        }
                    }
                    getLatestProduct(cat_id,sub_cat_id);

                }
                else {
                    Log.d("SOFTMENTERROR","AD_PROMOTED_GIG");
                    Services.showDialog(SingleCategoryActivity.this,"ERROR","Something Went Wrong");
                }
            }
        });
    }

    public void getLatestProduct(String cat_id,String sub_cat_id) {
        if (!isMaxData) {
            progressBar.setVisibility(View.VISIBLE);
            com.google.firebase.firestore.Query query;
            int ITEM_LOAD_COUNT = 21;
            if (lastNode == null) {

                query = FirebaseFirestore.getInstance().collection("Products").orderBy("date",com.google.firebase.firestore.Query.Direction.DESCENDING).whereEqualTo("cat_id",cat_id).whereEqualTo("sub_cat_id",sub_cat_id).limit(ITEM_LOAD_COUNT);
            }
            else {
                query = FirebaseFirestore.getInstance().collection("Products").orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING).startAt(lastNode).whereEqualTo("cat_id",cat_id).whereEqualTo("sub_cat_id",sub_cat_id).limit(ITEM_LOAD_COUNT);
            }
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {



                    if (task.isSuccessful()) {

                        if (task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                if (productModel.getImages().get("0") != null) {
                                    if (promotedAdID[0].equals(productModel.getId()) || promotedAdID[1].equals(productModel.getId())) {
                                        continue;
                                    }
                                    productModels.add(productModel);
                                }

                            }



                            if (lastNode == null) {

                                shimmerFrameLayout.setVisibility(View.GONE);
                                products_recyclerview.setVisibility(View.VISIBLE);

                            }

                            if (productModels.size() > 0) {

                                lastNode = productModels.get(productModels.size() - 1).getDate();

                                if (!lastNode.equals(lastKey)) {

                                    if (productModels.size() > 0){
                                        productModels.remove(productModels.size() - 1);
                                    }

                                }
                                else {
                                    isMaxData = true;
                                }

                            }





                            isLoading = false;
                            progressBar.setVisibility(View.GONE);



                        }
                        else {
                            shimmerFrameLayout.setVisibility(View.GONE);
                            products_recyclerview.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);
                            isLoading = false;
                            isMaxData = true;

                        }

                        if (productModels.size() > 0) {
                            no_product_available.setVisibility(View.GONE);
                        }
                        else {
                            no_product_available.setVisibility(View.VISIBLE);
                        }
                        mainModels.clear();
                        mainModels.addAll(productModels);
                        productAdapter.notifyDataSetChanged();



                    } else {

                        isLoading = false;
                        progressBar.setVisibility(View.GONE);

                        Services.showDialog(SingleCategoryActivity.this, "ERROR", task.getException().getLocalizedMessage());
                    }

                }
            });
        }


    }



}