package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.application.epa.Adapters.MyHeaderPagerAdapter;
import com.application.epa.Models.CategoryModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.Models.UserModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class ViewProductActivity extends AppCompatActivity {

    private LinearLayout dotlayout;
    private ProductModel productModel;
    private boolean fromDeepLink = false;
    private  ImageView[] dots;
    private ImageView more;

    private ImageView favourite;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);


        //MOREACTION
        more = findViewById(R.id.more);

        if (UserModel.data.emailAddress.equalsIgnoreCase("app.direwolf@gmail.com") || UserModel.data.emailAddress.equalsIgnoreCase("iamvijay67@gmail.com") || UserModel.data.emailAddress.equalsIgnoreCase("josericardocunhadeoliveira@gmail.com")) {
            more.setVisibility(View.VISIBLE);

        }
        else {
            more.setVisibility(View.GONE);
        }

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });




        productModel = (ProductModel)getIntent().getSerializableExtra("product");
        fromDeepLink = getIntent().getBooleanExtra("fromDeepLink",false);

        if (productModel == null) {

            finish();
        }

        if (productModel.getImages().size() < 1) {
            finish();
        }
        //Slider & DotLayout
        dotlayout = findViewById(R.id.dotlayout);
        ViewPager headerviewpager = findViewById(R.id.headerviewpager);
        ArrayList<String> images = new ArrayList<>(productModel.getImages().values());
        MyHeaderPagerAdapter myHeaderPagerAdapter = new MyHeaderPagerAdapter(this,images);
        headerviewpager.setAdapter(myHeaderPagerAdapter);



        favourite = findViewById(R.id.fav);
        favourite.setTag("unfav");
        isFav(productModel.id);

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String) favourite.getTag();
                if (tag.equalsIgnoreCase("fav")) {
                    unCheckFav(productModel.id);
                }
                else {
                    checkFav(productModel.id);
                }
            }
        });

        TextView title = findViewById(R.id.title);
        TextView category = findViewById(R.id.category);

        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);
        TextView seller = findViewById(R.id.sellerName);
        LinearLayout deliveryLL = findViewById(R.id.deliveryLL);
        TextView deliveryDay = findViewById(R.id.deliveryDays);
        TextView condition = findViewById(R.id.condition);
        TextView quantity = findViewById(R.id.quantity);
        TextView city = findViewById(R.id.city);
        ImageView storeImage = findViewById(R.id.storeImage);

        ProgressHud.show(ViewProductActivity.this,"Loading...");
        FirebaseFirestore.getInstance().collection("Users").document(productModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ProgressHud.dialog.dismiss();
                if (task.getResult() != null && task.getResult().exists()) {

                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    ViewProductActivity.this.userModel = userModel;
                    if (userModel.getUid() != null && !userModel.getStoreImage().isEmpty()) {
                        Glide.with(ViewProductActivity.this).load(userModel.getStoreImage()).apply(new RequestOptions().override(400, 400)).placeholder(R.drawable.man1).into(storeImage);
                    }
                    city.setText(userModel.storeCity);
                    seller.setText(Services.toUpperCase(userModel.storeName));

                }

            }
        });



        title.setText(Services.toUpperCase(productModel.title));
        category.setText(CategoryModel.getCategoryNameById(this,productModel.getCat_id()));
        description.setText(productModel.description);
        if (productModel.getPrice() == 0) {
            price.setVisibility(View.GONE);
        }
        else {
            price.setVisibility(View.VISIBLE);
            price.setText("R$ "+productModel.getPrice());
        }


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromDeepLink) {
                    Intent intent = new Intent(ViewProductActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    finish();
                }
            }
        });

        if (productModel.isProductNew){
            condition.setText(R.string.new_word);
        }
        else{
            condition.setText(R.string.used);
        }

        quantity.setText(String.valueOf(productModel.quantity));


        if (productModel.isDeliverProduct()) {
            deliveryLL.setVisibility(View.VISIBLE);
         //   deliveryFee.setText("R$ "+productModel.deliveryCharge);
            int days = productModel.maxDeliverDay;
            if (productModel.isSameDayDeliver()) {
                deliveryDay.setText(getString(R.string.day1));
            }
            else {
                deliveryDay.setText(days+" "+getString(R.string.days));
            }

        }
        else {
            deliveryLL.setVisibility(View.GONE);
        }


        CardView contactSellerBtn = findViewById(R.id.contactSeller);


        //ContactSeller
        contactSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (productModel.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Services.showCenterToast(ViewProductActivity.this,getString(R.string.you_can_not_contact));
                }
                else {

                        gotoChatScreen();

                }


            }
        });

        //ShareProduct
        findViewById(R.id.shareProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProductURL();
            }
        });

        dots = new ImageView[productModel.getImages().size()];

        for (int i = 0 ; i < dots.length;i++) {
            dots[i] = new ImageView(this);
        }

        preparedots(0);
        headerviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                        preparedots(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void checkFav(String productId){
        Map<String, Object> map = new HashMap();
        map.put("productId",productId);
        map.put("date", new Date());
        favourite.setTag("fav");
        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.uid).collection("Favourites").document(productId).set(map);
        Services.showCenterToast(this,getString(R.string.favourite_added));
        favourite.setImageResource(R.drawable.ic_baseline_favorite_24);
    }

    private void unCheckFav(String productId) {
        favourite.setTag("unfav");
        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.uid).collection("Favourites").document(productId).delete();
        Services.showCenterToast(this,getString(R.string.favorite_removed));
        favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    private void isFav(String productId) {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favourites").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        favourite.setTag("fav");
                        favourite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    }
                    else {
                        favourite.setTag("unfav");
                        favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }
            }
        });
    }
    private void createProductURL(){

        ProgressHud.show(ViewProductActivity.this, "");
        String productLinkText = "https://appepa.page.link/?"+
                "link=https://www.softment.in/?productId="+productModel.getId()+
                "&apn="+getPackageName()+
                "&st="+productModel.getTitle()+
                "&sd="+productModel.getDescription()+
                "&si="+productModel.getImages().get("0");



                FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(productLinkText)).buildShortDynamicLink().addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<ShortDynamicLink> task) {
                        ProgressHud.dialog.dismiss();
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        }
                        else {
                            Services.showDialog(ViewProductActivity.this,"ERROR", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                });


    }

    private void showPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(this,more);
        popupMenu.getMenuInflater().inflate(R.menu.product_action,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductActivity.this,R.style.AlertDialogTheme);
                builder.setTitle(R.string.delete_product_capital);
                builder.setMessage(R.string.are_you_sure_to_delete_item);
                builder.setCancelable(false);


               builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       ProgressHud.show(ViewProductActivity.this,getString(R.string.deleting));
                       FirebaseFirestore.getInstance().collection("Products").document(productModel.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull @NotNull Task<Void> task) {


                               if (task.isSuccessful()){
                                   Services.showCenterToast(ViewProductActivity.this,getString(R.string.product_has_deleted));
                               }
                               ProgressHud.dialog.dismiss();
                               dialog.dismiss();
                               finish();
                           }
                       });
                   }
               });

               builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                   }
               });



                AlertDialog alertDialog = builder.create();


                alertDialog.show();



            }
            else if (item.getItemId() == R.id.block_store){
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductActivity.this,R.style.AlertDialogTheme);
                builder.setCancelable(false);
                builder.setTitle(R.string.blockstore);
                builder.setMessage(R.string.are_you_sure_store_block);
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Map<String, Boolean > map = new HashMap<>();
                        map.put("isStoreBlocked",true);
                        ProgressHud.show(ViewProductActivity.this,getString(R.string.blocking));
                        FirebaseFirestore.getInstance().collection("Users").document(productModel.getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                   FirebaseFirestore.getInstance().collection("Products").whereEqualTo("uid",productModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           ProgressHud.dialog.dismiss();
                                            if (task.isSuccessful()) {

                                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                                        Map<String , Boolean> map1 = new HashMap<>();
                                                        map1.put("isProductBlocked",true);
                                                        FirebaseFirestore.getInstance().collection("Products").document(productModel.getId()).set(map1,SetOptions.merge());
                                                    }
                                                }

                                                Services.showDialog(ViewProductActivity.this,getString(R.string.BLOCKED),getString(R.string.this_store_has_been_blocked));
                                            }
                                            else {
                                                ProgressHud.dialog.dismiss();
                                            }
                                       }
                                   });
                                }
                                else {
                                    ProgressHud.dialog.dismiss();
                                    Services.showDialog(ViewProductActivity.this,getString(R.string.error),task.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                });
                builder.show();

            }
            return true;
        });
        popupMenu.show();
    }


    private void preparedots(int dotcustomposi) {

        if (dotlayout.getChildCount() > 0) {
            dotlayout.removeAllViews();
        }


        for (int i = 0 ; i < dots.length;i++) {

            if (i == dotcustomposi) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.act_dot));
            }
            else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inact_dot));
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,0,10,0);
            dotlayout.addView(dots[i],layoutParams);

        }

    }


    public void gotoChatScreen(){
        Intent intent = new Intent(ViewProductActivity.this, ChatScreenActivity.class);
        intent.putExtra("sellerId",userModel.getUid());
        intent.putExtra("sellerImage",userModel.getStoreImage());
        intent.putExtra("sellerName",userModel.storeName);
        intent.putExtra("sellerToken",userModel.token);
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        if (fromDeepLink) {
            Intent intent = new Intent(ViewProductActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }
}