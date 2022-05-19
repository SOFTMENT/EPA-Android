package com.application.epa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.application.epa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

import com.application.epa.Models.ProductModel;
import com.application.epa.Utils.Services;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_welcome);

       //LoadLocale
        loadLocale();
    }

    public void loadLocale(){
        SharedPreferences sharedPreferences = getSharedPreferences("lang",MODE_PRIVATE);
        String code = sharedPreferences.getString("mylang","pt");
        setLocale(code);
    }

    private void setLocale(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        if ( firebaseUser != null) {
            for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com")) {
                    handleDeepLink();
                    return;
                }

            }

            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {

             handleDeepLink();

            }


            else {
                gotoSignInPage();
            }

        }
        else {
            gotoSignInPage();
        }
    }

    public void gotoSignInPage(){
        Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void handleDeepLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                           if (deepLink != null) {
                               String productId = deepLink.getQueryParameter("productId");
                               getProductById(productId);
                           }
                           else {
                               FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                               Services.getCurrentUserData(WelcomeActivity.this, firebaseUser.getUid(),false);
                           }

                        }
                        else {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            Services.getCurrentUserData(WelcomeActivity.this, firebaseUser.getUid(),false);
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Services.getCurrentUserData(WelcomeActivity.this, firebaseUser.getUid(),false);
                    }
                });
    }

    public void getProductById(String pId){
        FirebaseFirestore.getInstance().collection("Products").document(pId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value != null && value.exists()) {
                        ProductModel productModel = value.toObject(ProductModel.class);
                        Intent intent = new Intent(WelcomeActivity.this, ViewProductActivity.class);
                        intent.putExtra("product",productModel);
                        intent.putExtra("fromDeepLink",true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Services.getCurrentUserData(WelcomeActivity.this, firebaseUser.getUid(),false);
                    }
                }
                else {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Services.getCurrentUserData(WelcomeActivity.this, firebaseUser.getUid(),false);
                }
            }
        });
    }
}