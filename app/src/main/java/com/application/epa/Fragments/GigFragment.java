package com.application.epa.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.application.epa.Adapters.MyProductAdapter;
import com.application.epa.MainActivity;
import com.application.epa.Models.ProductModel;
import com.application.epa.Models.UserModel;
import com.application.epa.R;
import com.application.epa.Utils.Const;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class GigFragment extends Fragment {


    private TextView message;
    private RecyclerView recyclerView;
    private MyProductAdapter myProductAdapter;
    private ImageView profile_image;
    private Context context;

    public GigFragment(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_gig, container, false);
        view.findViewById(R.id.shareApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Esquerda Compra Da Esquerda");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        profile_image = view.findViewById(R.id.profile_image);
        TextView name = view.findViewById(R.id.name);
        TextView mobileNumber = view.findViewById(R.id.mobileNumber);
        RelativeLayout topRR = view.findViewById(R.id.topRR);

        if (UserModel.data.isSeller()) {
            Glide.with(this).load(UserModel.data.storeImage).placeholder(R.drawable.man1).into(profile_image);
            name.setText(UserModel.data.storeName);
            mobileNumber.setText(UserModel.data.phoneNumber);
            topRR.setVisibility(View.VISIBLE); //CHNAGE PLEASE
        }
        else {
            topRR.setVisibility(View.GONE);
        }


        view.findViewById(R.id.changeStoreImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Const.changeImageActivity = "gig";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start((Activity) context);
            }
        });

        message = view.findViewById(R.id.message);
        recyclerView = view.findViewById(R.id.recyclerview);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        myProductAdapter = new MyProductAdapter(context, ProductModel.myproductsModels);
        recyclerView.setAdapter(myProductAdapter);

        return view;
    }

    public void notifyAdapter(){
        myProductAdapter.notifyDataSetChanged();
        if (ProductModel.myproductsModels.size() < 1) {
            message.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }
        else {
            message.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        ((MainActivity)context).initializeGigFragment(this);
    }

    public void cropURI(Uri resultUri) {
        Bitmap bitmap = null;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
            profile_image.setImageBitmap(bitmap);
            uploadImageOnFirebase(resultUri);

        } catch (IOException e) {
            Log.d("ERROR",e.getLocalizedMessage());
        }
    }

    private void uploadImageOnFirebase(Uri resultUri) {
        ProgressHud.show(context,"");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("StoreProfile").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ ".png");
        UploadTask uploadTask = storageReference.putFile(resultUri);
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
                    UserModel.data.storeImage = downloadUri;
                    updateStoreInformationOnServer(downloadUri);


                }
                else{
                    UserModel.data.storeImage = "https://firebasestorage.googleapis.com/v0/b/ecde-24c9c.appspot.com/o/ProfilePicture%2Fuser.png?alt=media&token=e95347b6-c527-4f3e-bc3c-169ea498dd93";
                    updateStoreInformationOnServer("https://firebasestorage.googleapis.com/v0/b/ecde-24c9c.appspot.com/o/ProfilePicture%2Fuser.png?alt=media&token=e95347b6-c527-4f3e-bc3c-169ea498dd93");
                }



            }
        });


    }



    private void updateStoreInformationOnServer(String downloadUri) {

        Map<String,String> map = new HashMap();
        map.put("storeImage",downloadUri);
        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.uid).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                   ProgressHud.dialog.dismiss();

                    if (task.isSuccessful()) {
                        Services.showCenterToast(context,"Updated...");
                    }
                    else {
                        Services.showDialog(context,"ERROR",task.getException().getLocalizedMessage());
                    }
            }
        });
    }


}