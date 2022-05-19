package com.application.epa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import com.application.epa.MainActivity;
import com.application.epa.Models.CategoryModel;
import com.application.epa.Models.SubcategoryModel;
import com.application.epa.Models.UserModel;
import com.application.epa.R;
import com.application.epa.Utils.Const;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class PostFragment extends Fragment {

    private final Map<String, Uri> images = new HashMap<>();
    private int clickedImageViewPosition = 0;
    private RoundedImageView oneImage, twoImage, threeImage, fourImage;
    private LinearLayout oneLL, twoLL, threeLL, fourLL;
    private boolean oneImageSelected,twoImageSelected;
    private Context context;
    private EditText p_title, p_description, p_price;
    private EditText p_quantity;
    private EditText maxDeliveryDays;
    private RadioGroup willYouDeliverRadio;
    private RadioGroup productIsNewRadio;
    private RadioGroup canYouDeliverSameDayRadio;
    private LinearLayout canYouDeliverSameDayLL;
    private int selectedCategoryIndex = -1;
    private int selectedSubCategoryIndex = -1;

    private ArrayList<SubcategoryModel> subcategoryModels;
    private AutoCompleteTextView chooseCategory, chooseSubcategory;

    public PostFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);




        subcategoryModels = new ArrayList<>();
        chooseCategory = view.findViewById(R.id.chooseCategory);
        p_title = view.findViewById(R.id.p_title);
        p_description = view.findViewById(R.id.p_description);
        p_price = view.findViewById(R.id.p_price);
        p_quantity = view.findViewById(R.id.quantity);
        maxDeliveryDays = view.findViewById(R.id.delivery_day);
      //  deliveryCharge = view.findViewById(R.id.delivery_charge);
        chooseSubcategory = view.findViewById(R.id.chooseSubcategory);
        willYouDeliverRadio = view.findViewById(R.id.willYouDeliverRadioBtn);
        productIsNewRadio = view.findViewById(R.id.isProductFreshRadioGroup);
        canYouDeliverSameDayRadio = view.findViewById(R.id.canYouDeliverOnSameDayRadioBtn);
        canYouDeliverSameDayLL = view.findViewById(R.id.sameDayLL);

        willYouDeliverRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_deliver) {

                    canYouDeliverSameDayLL.setVisibility(View.VISIBLE);
                   // deliveryCharge.setVisibility(View.VISIBLE);

                }
                else {
                    canYouDeliverSameDayLL.setVisibility(View.GONE);
                   // deliveryCharge.setVisibility(View.GONE);
                }
            }
        });


        chooseSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSubcategory.showDropDown();
            }
        });



        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCategory.showDropDown();
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

        view.findViewById(R.id.addProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = p_title.getText().toString().trim();
                String description = p_description.getText().toString().trim();
                String price = p_price.getText().toString();
                String category = chooseCategory.getText().toString();
                String sSubCategory = chooseSubcategory.getText().toString();
                String sQuantity = p_quantity.getText().toString();
                int isProductNewId = productIsNewRadio.getCheckedRadioButtonId();
                int willYouDeliverItem = willYouDeliverRadio.getCheckedRadioButtonId();
                int sameDayId = canYouDeliverSameDayRadio.getCheckedRadioButtonId();
                String maxDayDelivery = maxDeliveryDays.getText().toString();
              //  String deliveryFee = deliveryCharge.getText().toString();

                if (title.isEmpty()) {
                    Services.showCenterToast(context,getString(R.string.enter_product_title));
                    return;
                }
                else if (description.isEmpty()) {
                    Services.showCenterToast(context,getString(R.string.enter_product_description));
                    return;
                }

                else if (sQuantity.isEmpty()) {
                    Services.showCenterToast(context,getString(R.string.enter_product_quantity));
                    return;
                }
                else if (category.isEmpty() || selectedCategoryIndex == -1) {
                    Services.showCenterToast(context,getString(R.string.choose_product_category));
                    return;
                }
                else if (sSubCategory.isEmpty() || selectedSubCategoryIndex == -1) {
                    Services.showCenterToast(context,getString(R.string.choose_product_subcategory));
                    return;
                }

                else if (isProductNewId == -1){
                    Services.showCenterToast(context,getString(R.string.choose_product_is_new_or_used));
                    return;
                }
                else if (!oneImageSelected || !twoImageSelected){
                    Services.showCenterToast(context,getString(R.string.upload_atleast_2_images));
                    return;
                }
                if (willYouDeliverItem == -1) {
                    Services.showCenterToast(context,getString(R.string.choose_will_you_deliver));
                }
                else {
                    boolean willYouDeliver = false;
                    boolean sameDay  = false;
                    boolean isProductNew = false;
                    if (willYouDeliverItem == R.id.yes_deliver) {
                        willYouDeliver = true;

                        if (sameDayId == -1) {
                            Services.showCenterToast(context,getString(R.string.choose_can_you_deliver_same_day));
                            return;
                        }
//                        if (deliveryFee.isEmpty()) {
//                            Services.showCenterToast(context,"Enter Delivery Charge");
//                            return;
//                        }
                        else {
                            if (sameDayId == R.id.yes_same_day) {
                                sameDay = true;
                                maxDayDelivery = "0";
                            }
                            else {
                                if (maxDayDelivery.isEmpty()) {
                                    Services.showCenterToast(context,getString(R.string.enter_delivery_days));
                                    return;
                                }
                            }
                        }

                    }


                    if (isProductNewId == R.id.yes_new) {
                        isProductNew = true;
                    }
                    if (!willYouDeliver) {
                        maxDayDelivery = "0";
                       // deliveryFee = "0";
                        
                    }


                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (price.isEmpty()) {
                            price = "0";
                        }
                        addProduct(CategoryModel.categoryModels.get(selectedCategoryIndex).getId(), subcategoryModels.get(selectedSubCategoryIndex).getId(),title,description,Long.parseLong(price),Integer.parseInt(sQuantity),isProductNew,willYouDeliver,sameDay,Integer.parseInt(maxDayDelivery));
                    } else {
                        Services.logout(context);
                    }
                }

            }

        });



        //IMAGES
        oneImage = view.findViewById(R.id.oneImage);
        twoImage = view.findViewById(R.id.twoImage);
        threeImage = view.findViewById(R.id.threeImage);
        fourImage = view.findViewById(R.id.fourImage);

        //Linear Layout
        oneLL = view.findViewById(R.id.oneLL);
        twoLL = view.findViewById(R.id.twoLL);
        threeLL = view.findViewById(R.id.threeLL);
        fourLL = view.findViewById(R.id.fourLL);


        oneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition  = 1;
                showFileChooser();
            }
        });

        twoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition  = 2;
                showFileChooser();
            }
        });

        threeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition  = 3;
                showFileChooser();
            }
        });

        fourImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition  = 4;
                showFileChooser();
            }
        });


        oneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition  = 1;
                showFileChooser();
            }
        });


        twoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition = 2;
                showFileChooser();
            }
        });

        threeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition = 3;
                showFileChooser();
            }
        });

        fourLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageViewPosition = 4;
                showFileChooser();
            }
        });
        return view;
    }



    private void uploadImageOnFirebase(String pid) {
        Map<String,String> sImages = new HashMap<>();

        for (String key: images.keySet()) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Products").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pid).child(key+ ".png");

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), images.get(key));

            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadTask uploadTask = null;
            if (bmp == null) {
               uploadTask  = storageReference.putFile(Objects.requireNonNull(images.get(key)));
            }
            else {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] data = baos.toByteArray();
                uploadTask = storageReference.putBytes(data);
            }

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        ProgressHud.dialog.dismiss();
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if (task.isSuccessful()) {
                        String downloadUri = String.valueOf(task.getResult());

                        sImages.put(key,downloadUri);
                        Map<String, Object> map = new HashMap<>();
                        map.put("images",sImages);
                        FirebaseFirestore.getInstance().collection("Products").document(pid).set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    }
                                    else {

                                    }
                            }
                        });
                    }
                    else {
                        Log.d("VIJAYERROR",task.getException().getLocalizedMessage());
                    }



                }
            });
        }

    }

    public void addProduct(String cat_id, String sub_cat_id,String title, String description, long price, int quantity, boolean isProductNew, boolean deliverProduct, boolean sameDayDeliver,int deliveryDay) {
        String id = FirebaseFirestore.getInstance().collection("Products").document().getId();
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("cat_id",cat_id);
        map.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("title",title);
        map.put("description",description);
        map.put("price",price);
        map.put("quantity",quantity);
        map.put("sub_cat_id",sub_cat_id);
        map.put("isProductNew",isProductNew);
        map.put("deliverProduct",deliverProduct);
        map.put("sameDayDeliver",sameDayDeliver);
        map.put("maxDeliverDay",deliveryDay);
        map.put("isProductBlocked",false);
       // map.put("deliveryCharge",deliveryFee);
        map.put("date", FieldValue.serverTimestamp());
        map.put("storeUid", UserModel.data.uid);
        map.put("hasThumbnail",true);
        map.put("storeCity",UserModel.data.storeCity);
        ProgressHud.show(context,"");
        FirebaseFirestore.getInstance().collection("Products").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Services.showCenterToast(context,getString(R.string.product_succssfully_added));
                    uploadImageOnFirebase(id);
                    images.clear();
                    oneImageSelected = false;
                    twoImageSelected = false;

                    oneLL.setVisibility(View.VISIBLE);
                    twoLL.setVisibility(View.VISIBLE);
                    threeLL.setVisibility(View.VISIBLE);
                    fourLL.setVisibility(View.VISIBLE);

                    oneImage.setVisibility(View.GONE);
                    twoImage.setVisibility(View.GONE);
                    threeImage.setVisibility(View.GONE);
                    fourImage.setVisibility(View.GONE);

                    p_title.setText("");
                    p_description.setText("");
                    p_price.setText("");
                    p_quantity.setText("");
                    chooseCategory.setText("");
                    chooseSubcategory.setText("");
                    selectedCategoryIndex = -1;
                    selectedSubCategoryIndex = -1;
                    clickedImageViewPosition = -1;
                    productIsNewRadio.clearCheck();
                    willYouDeliverRadio.clearCheck();
                    canYouDeliverSameDayRadio.clearCheck();
                    canYouDeliverSameDayLL.setVisibility(View.GONE);
                    maxDeliveryDays.setText("");
                    maxDeliveryDays.setVisibility(View.GONE);
//                    deliveryCharge.setText("");
//                    deliveryCharge.setVisibility(View.GONE);

                }
                else {
                    Services.showDialog(context,getString(R.string.error), Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            Const.changeImageActivity = "post";
            CropImage.activity(filepath).start((MainActivity)context);
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


            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
                oneImage.setImageBitmap(bitmap);
                oneImageSelected = true;
                oneImage.setVisibility(View.VISIBLE);
                oneLL.setVisibility(View.GONE);
                images.put("0",resultUri);
            } catch (IOException e) {
                Log.d("ERROR",e.getLocalizedMessage());
            }
        }else if (clickedImageViewPosition == 2) {


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
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
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
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
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
                fourImage.setImageBitmap(bitmap);
                fourImage.setVisibility(View.VISIBLE);
                fourLL.setVisibility(View.GONE);
                images.put("3",resultUri);
            } catch (IOException ignored) {

            }
        }
    }

    public void notifyAdapter(){
        ArrayList<String> categoryNames = new ArrayList<>();
        for (CategoryModel cat : CategoryModel.categoryModels) {
            if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
                categoryNames.add(cat.getTitle_pt());
            else
                categoryNames.add(cat.getTitle_en());

        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(context,R.layout.option_item,categoryNames);
        chooseCategory.setAdapter(arrayAdapter);


        chooseCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               selectedCategoryIndex = i;
               selectedSubCategoryIndex = -1;
               chooseSubcategory.setText("");
               getSubcategory(CategoryModel.categoryModels.get(i).getId());

            }
        });

    }

    public void getSubcategory(String cat_id){
        ProgressHud.show(context,"");
        String field = "title_pt";
        if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
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

                    notifySubcategoryAdapter();

                }
                else {
                    Services.showDialog(context,getString(R.string.error),error.getLocalizedMessage());
                }
            }
        });

    }

    public void notifySubcategoryAdapter(){
        ArrayList<String> subCategoryNames = new ArrayList<>();
        for (SubcategoryModel subcategoryModel : subcategoryModels) {
            if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
                subCategoryNames.add(subcategoryModel.getTitle_pt());
            else
                subCategoryNames.add(subcategoryModel.getTitle_en());

        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(context,R.layout.option_item,subCategoryNames);
        chooseSubcategory.setAdapter(arrayAdapter);


        chooseSubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubCategoryIndex = i;

            }
        });

    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        ((MainActivity)context).initializePostFragment(this);
    }
}