package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.application.epa.Models.ProductModel;
import com.application.epa.Models.UserModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class EditProductActivity extends AppCompatActivity {

    private final Map<String, Uri> images = new HashMap<>();
    private Map<String,String> previous_image = new HashMap<>();
    private int clickedImageViewPosition = 0;
    private RoundedImageView oneImage, twoImage, threeImage, fourImage;
    private LinearLayout oneLL, twoLL, threeLL, fourLL;
    private boolean oneImageSelected,twoImageSelected;
    private EditText p_title, p_description, p_price,q_quantity;
    private EditText maxDeliveryDays;
//    private EditText deliveryCharge;
    private ProductModel productModel;

    private RadioGroup willYouDeliverRadio;
    private RadioGroup canYouDeliverSameDayRadio;
    private LinearLayout canYouDeliverSameDayLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        int index = getIntent().getIntExtra("index",-1);
        if (index == -1) {
            finish();
            return;
        }
        productModel = ProductModel.myproductsModels.get(index);
        if (productModel == null) {
            finish();
        }

        previous_image = productModel.getImages();

        p_title = findViewById(R.id.p_title);
        p_description = findViewById(R.id.p_description);
        p_price = findViewById(R.id.p_price);
        q_quantity = findViewById(R.id.p_quantity);
        maxDeliveryDays = findViewById(R.id.delivery_day);
       // deliveryCharge =  findViewById(R.id.delivery_charge);
        willYouDeliverRadio = findViewById(R.id.willYouDeliverRadioBtn);
        canYouDeliverSameDayRadio = findViewById(R.id.canYouDeliverOnSameDayRadioBtn);
        canYouDeliverSameDayLL = findViewById(R.id.sameDayLL);
        findViewById(R.id.addProduct).setOnClickListener(view -> {
            String title = p_title.getText().toString().trim();
            String description = p_description.getText().toString().trim();
            String price = p_price.getText().toString();
            String sQuantity = q_quantity.getText().toString();
            int willYouDeliverItem = willYouDeliverRadio.getCheckedRadioButtonId();
            int sameDayId = canYouDeliverSameDayRadio.getCheckedRadioButtonId();
            String maxDayDelivery = maxDeliveryDays.getText().toString();
          //  String deliveryFee = deliveryCharge.getText().toString();

            if (title.isEmpty()) {
                Services.showCenterToast(EditProductActivity.this,getString(R.string.enter_product_title));
            }
            else if (description.isEmpty()) {
                    Services.showCenterToast(EditProductActivity.this,getString(R.string.enter_product_description));
            }


           else  if (!oneImageSelected || !twoImageSelected){
                Services.showCenterToast(EditProductActivity.this,getString(R.string.upload_atleast_2_images));
           }
            else if (willYouDeliverItem == -1) {
                Services.showCenterToast(EditProductActivity.this,getString(R.string.choose_will_you_deliver));
            }
            else {
                boolean willYouDeliver = false;
                boolean sameDay  = false;
                boolean isProductNew = false;
                if (willYouDeliverItem == R.id.yes_deliver) {
                    willYouDeliver = true;

                    if (sameDayId == -1) {
                        Services.showCenterToast(EditProductActivity.this,"Choose Can You Deliver Same Day?");
                        return;
                    }

                    else {
                        if (sameDayId == R.id.yes_same_day) {
                            sameDay = true;
                            maxDayDelivery = "0";
                        }
                        else {
                            if (maxDayDelivery.isEmpty()) {
                                Services.showCenterToast(EditProductActivity.this,getString(R.string.enter_delivery_days));
                                return;
                            }
                        }
                    }

                }


                if (!willYouDeliver) {
                    maxDayDelivery = "0";
                    //deliveryFee = "0";

                }



                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if (price.isEmpty()) {
                        price = "0";
                    }
                    if (sQuantity.isEmpty()) {
                        sQuantity = "0";
                    }

                    updateProduct(title,description,Integer.parseInt(price),Integer.parseInt(sQuantity),willYouDeliver,sameDay,Integer.parseInt(maxDayDelivery));

                } else {
                    Services.logout(EditProductActivity.this);
                }
            }


        });


        //GoBack
        findViewById(R.id.back).setOnClickListener(view -> finish());


        //IMAGES
        oneImage = findViewById(R.id.oneImage);
        twoImage = findViewById(R.id.twoImage);
        threeImage = findViewById(R.id.threeImage);
        fourImage = findViewById(R.id.fourImage);

        //Linear Layout
        oneLL = findViewById(R.id.oneLL);
        twoLL = findViewById(R.id.twoLL);
        threeLL = findViewById(R.id.threeLL);
        fourLL = findViewById(R.id.fourLL);

        //INITIALIZE ITEMS
        p_title.setText(productModel.getTitle());
        p_description.setText(productModel.getDescription());
        p_price.setText(productModel.getPrice()+"");
        q_quantity.setText(productModel.getQuantity()+"");

        if (productModel.maxDeliverDay > 0) {
            maxDeliveryDays.setText(productModel.maxDeliverDay+"");
        }
        else {
            maxDeliveryDays.setText("");
        }


        willYouDeliverRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_deliver) {

                    canYouDeliverSameDayLL.setVisibility(View.VISIBLE);
                    //deliveryCharge.setVisibility(View.VISIBLE);

                }
                else {
                    canYouDeliverSameDayLL.setVisibility(View.GONE);
                   // deliveryCharge.setVisibility(View.GONE);
                }
            }
        });
        canYouDeliverSameDayRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_same_day) {


                    maxDeliveryDays.setVisibility(View.GONE);


                }
                else {

                    maxDeliveryDays.setVisibility(View.VISIBLE);

                }
            }
        });


        if (productModel.isDeliverProduct()) {
            willYouDeliverRadio.check(R.id.yes_deliver);
            canYouDeliverSameDayLL.setVisibility(View.VISIBLE);

            if (productModel.isSameDayDeliver()) {
                canYouDeliverSameDayRadio.check(R.id.yes_same_day);
                maxDeliveryDays.setVisibility(View.GONE);
            }
            else {
                canYouDeliverSameDayRadio.check(R.id.not_same_day);
                maxDeliveryDays.setVisibility(View.GONE);
            }

        }
        else {

            willYouDeliverRadio.check(R.id.no_deliver);
            canYouDeliverSameDayLL.setVisibility(View.GONE);
          //  deliveryCharge.setVisibility(View.GONE);
        }


        if (productModel.getImages().containsKey("0")) {
            oneImageSelected = true;
            oneImage.setVisibility(View.VISIBLE);
            oneLL.setVisibility(View.GONE);
            Glide.with(this).load(productModel.getImages().get("0")).diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.placeholder1).into(oneImage);
        }

        if (productModel.getImages().containsKey("1")) {
            twoImageSelected = true;
            twoImage.setVisibility(View.VISIBLE);
            twoLL.setVisibility(View.GONE);
            Glide.with(this).load(productModel.getImages().get("1")).diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.placeholder1).into(twoImage);
        }

        if (productModel.getImages().containsKey("2")) {
            threeImage.setVisibility(View.VISIBLE);
            threeLL.setVisibility(View.GONE);
            Glide.with(this).load(productModel.getImages().get("2")).diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.placeholder1).into(threeImage);
        }

        if (productModel.getImages().containsKey("3")) {
            fourImage.setVisibility(View.VISIBLE);
            fourLL.setVisibility(View.GONE);
            Glide.with(this).load(productModel.getImages().get("3")).diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.placeholder1).into(fourImage);
        }

        oneImage.setOnClickListener(view -> {
            clickedImageViewPosition  = 1;
            showFileChooser();
        });

        twoImage.setOnClickListener(view -> {
            clickedImageViewPosition  = 2;
            showFileChooser();
        });

        threeImage.setOnClickListener(view -> {
            clickedImageViewPosition  = 3;
            showFileChooser();
        });

        fourImage.setOnClickListener(view -> {
            clickedImageViewPosition  = 4;
            showFileChooser();
        });


        oneLL.setOnClickListener(view -> {
            clickedImageViewPosition  = 1;
            showFileChooser();
        });


        twoLL.setOnClickListener(view -> {
            clickedImageViewPosition = 2;
            showFileChooser();
        });

        threeLL.setOnClickListener(view -> {
            clickedImageViewPosition = 3;
            showFileChooser();
        });

        fourLL.setOnClickListener(view -> {
            clickedImageViewPosition = 4;
            showFileChooser();
        });

    }

    private void uploadImageOnFirebase(String uid,String pid) {

        for (String key : images.keySet()) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Products").child(uid).child(pid).child(key+ ".png");
            UploadTask uploadTask = storageReference.putFile(images.get(key));
            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    ProgressHud.dialog.dismiss();
                    throw  task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Log.d("MYVALUE",key+"");
                    String downloadUri = String.valueOf(task.getResult());
                    previous_image.put(key,downloadUri);
                    Map<String, Object> map = new HashMap<>();
                    map.put("images",previous_image);
                    FirebaseFirestore.getInstance().collection("Products").document(pid).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("imagesUplaoded","YES");
                            }
                            else {
                                Log.d("imagesUploaded", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                            }
                        }
                    });
                }



            });
        }

    }

    public void updateProduct(String title, String description, Integer price, Integer quantity, boolean willDeliver, boolean sameDay, Integer maxDeliveryDay) {
        Map<String, Object> map = new HashMap<>();


        map.put("title",title);
        map.put("description",description);
        map.put("price",price);
        map.put("quantity",quantity);
        map.put("deliverProduct",willDeliver);
        map.put("sameDayDeliver",sameDay);
        map.put("maxDeliverDay",maxDeliveryDay);
        //map.put("deliveryCharge",deliveryFee);

        ProgressHud.show(EditProductActivity.this,"");

        FirebaseFirestore.getInstance().collection("Products").document(productModel.id).set(map,SetOptions.merge()).addOnCompleteListener(task -> {
            ProgressHud.dialog.dismiss();
            if (task.isSuccessful()) {
                uploadImageOnFirebase(UserModel.data.uid, productModel.id);
                Services.showDialog(EditProductActivity.this,getString(R.string.updated),getString(R.string.product_sucssfully_updated));

                images.clear();

            }
            else {
                Services.showDialog(EditProductActivity.this,getString(R.string.error),task.getException().getLocalizedMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {

            Uri filepath = data.getData();
            CropImage.activity(filepath).setOutputCompressQuality(40).start(EditProductActivity.this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                cropUri(result.getUriContent());

            }
        }

    }
    public void showFileChooser() {

        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 1);
        }
        catch (Exception ignored) {

        }

    }

    public void cropUri(Uri resultUri ){
        if (clickedImageViewPosition == 1) {


            Bitmap bitmap;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(EditProductActivity.this.getContentResolver(), resultUri);
                oneImage.setImageBitmap(bitmap);
                oneImageSelected = true;
                oneImage.setVisibility(View.VISIBLE);
                oneLL.setVisibility(View.GONE);
                images.put("0",resultUri);
            } catch (IOException e) {

            }
        }else if (clickedImageViewPosition == 2) {


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditProductActivity.this.getContentResolver(), resultUri);
                twoImage.setImageBitmap(bitmap);
                twoImageSelected = true;
                twoImage.setVisibility(View.VISIBLE);
                twoLL.setVisibility(View.GONE);
                images.put("1",resultUri);
            } catch (IOException ignored) {

            }
        }
        else if (clickedImageViewPosition == 3) {


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditProductActivity.this.getContentResolver(), resultUri);
                threeImage.setImageBitmap(bitmap);
                threeImage.setVisibility(View.VISIBLE);
                threeLL.setVisibility(View.GONE);
                images.put("2",resultUri);
            } catch (IOException ignored) {

            }
        }
        else if (clickedImageViewPosition == 4) {

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditProductActivity.this.getContentResolver(), resultUri);
                fourImage.setImageBitmap(bitmap);

                fourImage.setVisibility(View.VISIBLE);
                fourLL.setVisibility(View.GONE);
                images.put("3",resultUri);
            } catch (IOException ignored) {

            }
        }
    }






}