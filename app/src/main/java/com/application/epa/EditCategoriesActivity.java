package com.application.epa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.application.epa.Models.CategoryModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;


public class EditCategoriesActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 4655;
    private final int PICK_IMAGE_REQUEST = 1;
    private  Uri resultUri = null;
    private ImageView cat_image;
    private EditText cat_title_pt;
    private EditText cat_title_en;
    private boolean isImageSelected = false;
    private CategoryModel categoryModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        requestStoragePermission();


        categoryModel = (CategoryModel) getIntent().getSerializableExtra("category");
        if (categoryModel == null) {
            finish();
        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Cat_image
        cat_image = findViewById(R.id.categoryImage);
        cat_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        //Cat_title
        cat_title_pt = findViewById(R.id.categoryNamePT);
        cat_title_en = findViewById(R.id.categoryNameEN);

        //AddCategory
        findViewById(R.id.addCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_pt = cat_title_pt.getText().toString().trim();
                String title_en = cat_title_en.getText().toString().trim();
                if (!isImageSelected) {

                    if (title_pt.isEmpty()) {
                        Services.showCenterToast(EditCategoriesActivity.this,"Enter Title");
                    }
                    else {
                        if (title_en.isEmpty()) {
                            Services.showCenterToast(EditCategoriesActivity.this,"Enter English Title");
                        }
                        else {
                            ProgressHud.show(EditCategoriesActivity.this,"Updating...");
                            String cat_id =  categoryModel.getId();
                            uploadCategoryData(cat_id,title_pt,title_en,categoryModel.image);

                        }

                    }

                }
                else {
                    if (title_pt.isEmpty()) {
                        Services.showCenterToast(EditCategoriesActivity.this,"Enter Title");
                    }
                    else {
                        if (title_en.isEmpty()) {
                            Services.showCenterToast(EditCategoriesActivity.this,"Enter English Title");
                        }
                        else {
                            ProgressHud.show(EditCategoriesActivity.this,"Updating...");
                            String cat_id =  categoryModel.getId();
                            uploadImageOnFirebase(cat_id,title_pt,title_en);
                        }

                    }
                }
            }
        });


        //INITIALIZE
        Glide.with(this).load(categoryModel.getImage()).placeholder(R.drawable.placeholder).into(cat_image);
        cat_title_pt.setText(categoryModel.getTitle_pt());
        cat_title_en.setText(categoryModel.getTitle_en());


    }

    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }

        }

    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    private void uploadImageOnFirebase(String cat_id, String title_pt, String title_en) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Categories").child(cat_id).child(cat_id+ ".png");
        UploadTask uploadTask = storageReference.putFile(resultUri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    ProgressHud.dialog.dismiss();
                    throw  task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    String downloadUri = String.valueOf(task.getResult());
                    uploadCategoryData(cat_id,title_pt,title_en,downloadUri);

                }
                else{
                    ProgressHud.dialog.dismiss();
                    Toast toast = Toast.makeText(EditCategoriesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


            }
        });
    }


    public void uploadCategoryData(String cat_id,String title_pt, String title_en, String imageUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("id",cat_id);
        map.put("image",imageUrl);
        map.put("title_pt",title_pt);
        map.put("title_en",title_en);

        FirebaseFirestore.getInstance().collection("Categories").document(cat_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Services.showCenterToast(EditCategoriesActivity.this,"Category Updated");
                    isImageSelected = false;


                }
                else {
                    Services.showDialog(EditCategoriesActivity.this,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            resultUri = data.getData();
            isImageSelected = true;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                cat_image.setImageBitmap(bitmap);
            } catch (IOException e) {

            }
            //            CropImage.activity(filepath).setOutputCompressQuality(100)
//                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUriContent();

                isImageSelected = true;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    cat_image.setImageBitmap(bitmap);
                } catch (IOException e) {

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

}