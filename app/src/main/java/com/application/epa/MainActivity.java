package com.application.epa;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.application.epa.Utils.ProgressHud;
import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import com.application.epa.Fragments.AccountFragment;
import com.application.epa.Fragments.ChatFragment;
import com.application.epa.Fragments.GigFragment;
import com.application.epa.Fragments.HomeFragment;
import com.application.epa.Fragments.PostFragment;
import com.application.epa.Fragments.SellerStoreInformation;
import com.application.epa.Models.CategoryModel;
import com.application.epa.Models.LastMessageModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.Models.UpdateType;
import com.application.epa.Models.UserModel;
import com.application.epa.Utils.Const;
import com.application.epa.Utils.MyFirebaseMessagingService;
import com.application.epa.Utils.NonSwipeAbleViewPager;
import com.application.epa.Utils.Services;

public class MainActivity extends AppCompatActivity  {

    private TabLayout tabLayout;
    public HomeFragment homeFragment;
    public ChatFragment chatFragment;
    public AccountFragment accountFragment;
    private NonSwipeAbleViewPager viewPager;
    public PostFragment postFragment;
    public GigFragment gigFragment;
    public SellerStoreInformation sellerStoreInformation;
    private ViewPagerAdapter viewPagerAdapter;
    private static final int RC_APP_UPDATE = 11;
    private ListenerRegistration chatLitenerRegistration, myproductListener;
    private SharedPreferences sharedPreferences;

    private int[] tabIcons = {
            R.drawable.ic_outline_shopping_bag_24,
            R.drawable.ic_outline_message_24,
            R.drawable.ic_baseline_add_circle_outline_24,
            R.drawable.ic_baseline_storefront_24,
            R.drawable.ic_baseline_person_outline_24,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        sharedPreferences = getSharedPreferences("ECDE_DB",MODE_PRIVATE);

        //checkForUpdate();

        //UpdateToken
        updateToken();

        //ViewPager
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        viewPager.setCurrentItem(0);
//        int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.salmon);
//        tabLayout.getTabAt(0).getIcon().setTint(tabIconColor);
//
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.salmon);
//                tab.getIcon().setTint(tabIconColor);
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.gnt_gray);
//                tab.getIcon().setTint(tabIconColor);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }



    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);


    }

    public void showUpdatePopUp(String version,boolean isForceUpdate){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.update_view_layout,null);
        TextView no_thanks = view.findViewById(R.id.no_thanks);
        AppCompatButton updateBtn = view.findViewById(R.id.updateButton);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        if (isForceUpdate) {
            sharedPreferences.edit().putString("version",version).apply();
            no_thanks.setVisibility(View.GONE);
        }
        no_thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.show();
    }

    public void checkForUpdate(){
        FirebaseFirestore.getInstance().collection("UpdateType").document("status").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getException() == null && task.getResult().exists()){
                        UpdateType updateType = task.getResult().toObject(UpdateType.class);

                        if (updateType != null) {
                            String updateVersion = updateType.updateVersion;
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                String version = pInfo.versionName;
                                String sharedPreferVersion = sharedPreferences.getString("version","0");


                               if (!updateVersion.isEmpty()) {
                                   if(!updateVersion.equalsIgnoreCase(version)) {
                                       if (updateType.isForceUpdate) {
                                           showUpdatePopUp(version, updateType.isForceUpdate);
                                       }
                                       else {
                                           if (!sharedPreferVersion.equalsIgnoreCase(version)) {
                                               showUpdatePopUp(version, updateType.isForceUpdate);
                                           }
                                       }
                                   }
                               }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }


            }
        });

    }



    public void updateToken(){

        Map<String,Object> map = new HashMap<>();
        map.put("token", MyFirebaseMessagingService.getToken(this));
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge());

    }


    public void getCategotyData() {

        SharedPreferences sharedPreferences = getSharedPreferences("lang",MODE_PRIVATE);
        String code = sharedPreferences.getString("mylang","pt");

        String field;
        if (code.equalsIgnoreCase("pt"))
           field = "title_pt";
        else
            field = "title_en";


        FirebaseFirestore.getInstance().collection("Categories").orderBy(field, Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CategoryModel.categoryModels.clear();
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                        CategoryModel.categoryModels.add(categoryModel);
                    }

                }

                homeFragment.notifyAdapter();
                if (postFragment != null)
                    postFragment.notifyAdapter();

            }
            else {
                Services.showDialog(MainActivity.this,"ERROR", Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }



    //getLastChatModelData
    public void getLastMessageData(){
        chatLitenerRegistration = FirebaseFirestore.getInstance().collection("Chats").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("LastMessage").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
            if (error == null) {
                LastMessageModel.lastMessageModels.clear();
                if (value != null && !value.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        LastMessageModel lastMessageModel = documentSnapshot.toObject(LastMessageModel.class);
                        LastMessageModel.lastMessageModels.add(lastMessageModel);
                    }

                }
               chatFragment.notifyAdapter();

            }
        });
    }

    public void getMyProduct() {
       myproductListener = FirebaseFirestore.getInstance().collection("Products").orderBy("date").whereEqualTo("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {

            if (error == null) {

                ProductModel.myproductsModels.clear();
                if (value != null && !value.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                        ProductModel.myproductsModels.add(productModel);
                    }
                    Collections.reverse(ProductModel.myproductsModels);
                }

                gigFragment.notifyAdapter();

            }
            else {
                Services.showDialog(MainActivity.this,"ERROR",error.getLocalizedMessage());
            }

        });
    }



    private void setupViewPager(ViewPager viewPager) {

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new HomeFragment(),"Shop");
        viewPagerAdapter.addFrag(new ChatFragment(),"Chat");
        if (UserModel.data.isSeller()){

            viewPagerAdapter.addFrag(new PostFragment(),"Post");
        }

        else {

            viewPagerAdapter.addFrag(new SellerStoreInformation(),"Register");

        }

        viewPagerAdapter.addFrag(new GigFragment(),"My Store");
        viewPagerAdapter.addFrag(new AccountFragment(),"Profile");
        viewPager.setAdapter(viewPagerAdapter);

    }




    public void notifyPagerAdapter() {
        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(2);
    }



   static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
       private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 2) {
                if (UserModel.data.isSeller())  {
                    return new PostFragment();
                }
                else {
                    return new SellerStoreInformation();
                }
            }

            return mFragmentList.get(position);
        }

       @Override
       public int getItemPosition(@NonNull @NotNull Object object) {
            return POSITION_NONE;
       }

       @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment,String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

       @Override
       public CharSequence getPageTitle(int position) {
           return mFragmentTitleList.get(position);
       }


    }


    public void initializeHomeFragment(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
         getCategotyData();
    }

    public void initializeAccountFragment(AccountFragment accountFragment){
        this.accountFragment = accountFragment;

    }



    public void initializeChatFragment(ChatFragment chatFragment){
        this.chatFragment = chatFragment;
        getLastMessageData();
    }

    public void initializePostFragment(PostFragment postFragment){
        this.postFragment = postFragment;
        //getCategoryData
        getCategotyData();
    }

    public void initializeSellerFragment(SellerStoreInformation sellerStoreInformation) {
        this.sellerStoreInformation = sellerStoreInformation;
    }

    public void initializeGigFragment(GigFragment gigFragment) {
        this.gigFragment = gigFragment;

        //myProductData
        getMyProduct();



    }


    public void changeBottomBarPossition(int id) {
        viewPager.setCurrentItem(id);
        tabLayout.getTabAt(id).select();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {
                if (Const.changeImageActivity.equalsIgnoreCase("post"))
                   postFragment.cropUri(result.getUriContent());
                else if (Const.changeImageActivity.equalsIgnoreCase("gig"))
                    gigFragment.cropURI(result.getUriContent());
                else if (Const.changeImageActivity.equalsIgnoreCase("account"))
                    accountFragment.cropUrl(result.getUriContent());

                else
                    sellerStoreInformation.cropUrl(result.getUriContent());
            }
        }
        else if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {

            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();

        Services.loadLocale(this);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        }
        else {
            viewPager.setCurrentItem(0);
            tabLayout.getTabAt(0).select();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chatLitenerRegistration != null) {
            chatLitenerRegistration.remove();
        }
        if (myproductListener != null) {
            myproductListener.remove();
        }

    }
}


