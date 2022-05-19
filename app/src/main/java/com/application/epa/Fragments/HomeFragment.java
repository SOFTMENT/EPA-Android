package com.application.epa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.application.epa.Adapters.CategoriesAdaper;
import com.application.epa.Adapters.ProductAdapter;
import com.application.epa.MainActivity;
import com.application.epa.Models.CategoryModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.R;
import com.application.epa.SeeAllCategoryActivity;

import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;


public class HomeFragment extends Fragment {
    private ArrayList<ProductModel> productModels, searchProductModels, mainProductModels;
    private EditText searchET;
    private RecyclerView products_recyclerview;
    private CategoriesAdaper categoriesAdaper;
    private ShimmerRecyclerView shimmerFrameLayout;
    private ProductAdapter productAdapter;
    private SpinKitView progressBar;
    private boolean isLoading = false, isMaxData = false;
    private boolean hasSearched = false;
    private Date lastNode = null, lastKey = null;
    private Context context;
    private TextView no_product_available;
    private final String []promotedAdID = new String[2];


    public HomeFragment(){

    }

    public HomeFragment(MainActivity mainActivity){
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchET = view.findViewById(R.id.searchEditText);


        Client client = new Client("J2SL4HSQJ5", "919808179eade1b681f3323f1f603e41");
        Index index = client.getIndex("Products");

        shimmerFrameLayout = view.findViewById(R.id.shimmer_recycler_view);

        no_product_available = view.findViewById(R.id.no_product_available);

        RecyclerView categories_recyclerview = view.findViewById(R.id.cat_recyclerview);
        categories_recyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        categories_recyclerview.setLayoutManager(linearLayoutManager);

        categoriesAdaper = new CategoriesAdaper(context, CategoryModel.categoryModels);
        categories_recyclerview.setAdapter(categoriesAdaper);

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        promotedAdID[0] = "123";
        promotedAdID[1] = "456";

        //getLastKey
        getLastKey();

        products_recyclerview = view.findViewById(R.id.product_recyclerview);

        products_recyclerview.setHasFixedSize(true);
        products_recyclerview.setItemViewCacheSize(20);
        products_recyclerview.setDrawingCacheEnabled(true);
        products_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        products_recyclerview.setLayoutManager(staggeredGridLayoutManager);




        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))  {



                    if(!isLoading && !hasSearched)

                        getLatestProduct();
                         isLoading = true;
                }
            }
        });



        productModels = new ArrayList<>();
        searchProductModels = new ArrayList<>();
        mainProductModels = new ArrayList<>();

        productAdapter = new ProductAdapter(context, mainProductModels);
        products_recyclerview.setAdapter(productAdapter);

                view.findViewById(R.id.seeAllText).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SeeAllCategoryActivity.class);
                        startActivity(intent);
                    }
                });


                view.findViewById(R.id.cancel_search).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchET.setText("");
                        if (hasSearched) {
                            hasSearched = false;
                            isLoading = false;
                            notifyProductAdapter();
                        }
                    }
                });

                searchET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (charSequence.length() == 0){
                                if (hasSearched) {
                                    hasSearched = false;
                                    isLoading = false;
                                    notifyProductAdapter();
                                }

                            }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

        searchET.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {

                                if (searchET.getText().toString().isEmpty()) {
                                    notifyProductAdapter();

                                } else {


                                    ProgressHud.show(context, "Searching...");


                                    index.searchAsync(new Query(searchET.getText().toString()), new CompletionHandler() {
                                        @Override
                                        public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                                            ProgressHud.dialog.dismiss();
                                            searchProductModels.clear();
                                            hasSearched = true;
                                            if (e == null) {

                                                JsonElement mJson = null;
                                                try {
                                                    JSONArray jsonArray = jsonObject.getJSONArray("hits");

                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject hit = jsonArray.getJSONObject(i);

                                                        Gson gson = new Gson();

                                                        ProductModel object = gson.fromJson(hit.toString(), ProductModel.class);
                                                        searchProductModels.add(object);

                                                    }

                                                } catch (JSONException jsonException) {
                                                    Log.d("Error", jsonException.getLocalizedMessage());
                                                }


                                            } else {
                                                Log.d("Error", e.getLocalizedMessage());
                                            }


                                            if (productModels.size() > 0) {
                                                no_product_available.setVisibility(View.GONE);
                                            }
                                            else {
                                                no_product_available.setVisibility(View.VISIBLE);
                                            }

                                            mainProductModels.clear();
                                            mainProductModels.addAll(searchProductModels);

                                            productAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    return true; // consume.
                                }
                            }
                            else{
                                    notifyProductAdapter();
                                }
                            }
                            return false; // pass on to other listeners.
                        }

                }
        );

        return view;
    }


    public void getLastKey(){
        FirebaseFirestore.getInstance().collection("Products").orderBy("date").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {

                        for (DocumentSnapshot documentSnapshot :  task.getResult().getDocuments()) {
                           ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                           lastKey = productModel.getDate();
                            getPromotedProduct();
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
                    Services.showDialog(context,getString(R.string.error),getString(R.string.something_went_wrong));
                }
            }
        });
    }

    public void getPromotedProduct(){

        FirebaseFirestore.getInstance().collection("Products").orderBy("adLastDate").whereGreaterThan("adLastDate",new Date()).whereEqualTo("isProductBlocked",false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {


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
                    getLatestProduct();

                }
                else {
                    Services.showDialog(context,getString(R.string.error),getString(R.string.something_went_wrong));
                }
            }
        });
    }

    public void getLatestProduct() {
        if (!isMaxData) {
            progressBar.setVisibility(View.VISIBLE);
            com.google.firebase.firestore.Query query;
            int ITEM_LOAD_COUNT = 16;
            if (lastNode == null) {
                query = FirebaseFirestore.getInstance().collection("Products").orderBy("date",com.google.firebase.firestore.Query.Direction.DESCENDING).whereEqualTo("isProductBlocked",false).limit(ITEM_LOAD_COUNT);
            }
            else {
                  query = FirebaseFirestore.getInstance().collection("Products").orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING).whereEqualTo("isProductBlocked",false).startAt(lastNode).limit(ITEM_LOAD_COUNT);
            }
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        //ProductModel.latestproductModels.clear();
                        if (task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                if (productModel.getImages().get("0") != null) {
                                    if (promotedAdID[0].equals(productModel.getId()) || promotedAdID[1].equals(productModel.getId())) {
                                        continue;
                                    }
                                    productModels.add(productModel);
                                }
                                else {
                                    Log.d("OHMYGOD","NOOOOOO");
                                }

                            }

                            if (lastNode == null) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        products_recyclerview.setVisibility(View.VISIBLE);

                                    }
                                }, 500);
                            }



                            lastNode = productModels.get(productModels.size() - 1).getDate();


                            if (!lastNode.equals(lastKey)) {

                                productModels.remove(productModels.size() - 1);
                            }
                            else {

                               isMaxData = true;
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
                        notifyProductAdapter();

                    } else {

                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                        Services.showDialog(context, "ERROR", task.getException().getLocalizedMessage());
                    }
                }
            });
        }


    }




    public void notifyProductAdapter(){
        mainProductModels.clear();
        mainProductModels.addAll(productModels);
        productAdapter.notifyDataSetChanged();

    }
    public void notifyAdapter(){

        categoriesAdaper.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        ((MainActivity)context).initializeHomeFragment(this);


    }


}