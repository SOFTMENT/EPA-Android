package com.application.epa.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.epa.BuildConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import com.application.epa.FavouritesActivity;
import com.application.epa.MainActivity;
import com.application.epa.ManageCategoryActivity;
import com.application.epa.Models.FavoritesModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.Models.UserModel;
import com.application.epa.PrivacyPolicyActivity;
import com.application.epa.R;
import com.application.epa.SignInActivity;
import com.application.epa.SupportActivity;
import com.application.epa.TermsAndConditions;
import com.application.epa.Utils.Const;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;
import com.application.epa.WelcomeActivity;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {

    private TextView buyOrSellTextView;
    private Context context;
    ImageView circleImageView;
    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);




        //ADMIN
        LinearLayout adminItemsView = view.findViewById(R.id.adminItemView);
        if (UserModel.data.emailAddress.equalsIgnoreCase("app.direwolf@gmail.com") || UserModel.data.emailAddress.equalsIgnoreCase("iamvijay67@gmail.com") || UserModel.data.emailAddress.equalsIgnoreCase("josericardocunhadeoliveira@gmail.com")) {
            adminItemsView.setVisibility(View.VISIBLE);
            RelativeLayout addCat = view.findViewById(R.id.addCat);
            addCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ManageCategoryActivity.class));
                }
            });

        }
        else {
            adminItemsView.setVisibility(View.GONE);
        }


        String code = Services.getLocateCode(context);


        TextView langName = view.findViewById(R.id.languageName);
        if (code.equalsIgnoreCase("pt")) {
            langName.setText("Portuguese");
        }
        else {
            langName.setText("English");
        }

        TextView versionName = view.findViewById(R.id.versionName);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionName.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Logout
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Services.logout(context);
            }
        });
           circleImageView = view.findViewById(R.id.profile_image);
           TextView name = view.findViewById(R.id.name);
           TextView email = view.findViewById(R.id.email);
           Glide.with(context).load(UserModel.data.profileImage).diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.man1).into(circleImageView);
           name.setText(UserModel.data.fullName);
            email.setText(UserModel.data.emailAddress);

       //ShareApp
        view.findViewById(R.id.shareApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Esquerda Compra Da Esquerda");
                    String shareMessage= getString(R.string.let_me_recommend);
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

       //AddProduct
        view.findViewById(R.id.addProductRR).setOnClickListener(v -> {

                ((MainActivity)context).changeBottomBarPossition(2);

        });


        //MyProducts
        view.findViewById(R.id.myProductRR).setOnClickListener(v -> {

                ((MainActivity)context).changeBottomBarPossition(3);

        });

        //Favorites
        view.findViewById(R.id.favRR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FavouritesActivity.class));
            }
        });

        //support
        view.findViewById(R.id.support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SupportActivity.class));
            }
        });

      //Language
        view.findViewById(R.id.language).setOnClickListener(v -> {
            final String[] listItems = {"English","Portuguese"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose Language");
            builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        setLocale("en");
                        restart();
                    }
                    else if (which == 1){
                        setLocale("pt");
                        restart();
                    }
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();



        });

        //ChangeProfilePic

        //TapToChangeImage
        view.findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionForReadExtertalStorage()) {
                    ShowFileChooser();
                }
                else {
                    requestStoragePermission();
                }



            }
        });


        //PrivacyPolicy
        view.findViewById(R.id.privacypolicy).setOnClickListener(v -> {
            context.startActivity(new Intent(context, PrivacyPolicyActivity.class));
        });

        //Terms&Conditions
        view.findViewById(R.id.termsandcondition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, TermsAndConditions.class));
            }
        });


        //AppDeveloper
        view.findViewById(R.id.developer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.softment.in"));
                startActivity(browserIntent);
            }
        });

        //RateUs
        view.findViewById(R.id.rateUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show();
                }
            }
        });

        //ContactUs
        view.findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "epa.economiaparticipative@gmail.com" });
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        //ClearCache
        view.findViewById(R.id.clearCacheRR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        deleteCache(context);
                        dialogInterface.dismiss();
                        Services.showCenterToast(context,getString(R.string.cache_has_been_cleared));
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setIcon(R.mipmap.ic_launcher_round);
                builder.setTitle(R.string.clear_cache);
                builder.setMessage(R.string.are_you_sure_you_want_to_clear_cache);

                builder.setCancelable(true);
                builder.show();


            }
        });



        //DeleteAcccount
        view.findViewById(R.id.deletAccount).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setTitle(R.string.delete);
                builder.setMessage(R.string.are_you_sure_delete_account);
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (Const.isReAuth) {

                                deleteAccount();
                            }

                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                builder.setTitle(R.string.verify);
                                builder.setMessage(R.string.signin_again_to_verify);
                                builder.setCancelable(true);
                                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Const.isReAuth = true;
                                        Services.logout(context);

                                    }
                                });



                                builder.show();

                            }
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });

        return view;
    }



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private void restart() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void setLocale(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration,context.getResources().getDisplayMetrics());

        //SharedPref
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("lang", MODE_PRIVATE).edit();
        sharedPreferences.putString("mylang",code);
        sharedPreferences.apply();
    }

    public void deleteAccount(){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        ProgressHud.show(context,getString(R.string.deleting_account));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String,String> map = new HashMap<>();
        map.put("uid",uid);
        map.put("email",UserModel.data.getEmailAddress());
        map.put("name",UserModel.data.getFullName());
        batch.set(FirebaseFirestore.getInstance().collection("Deleted").document(uid),map);

                    FirebaseFirestore.getInstance().collection("Products").whereEqualTo("uid", uid).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task1.getResult().getDocuments()) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                if (productModel != null) {
                                    batch.delete(FirebaseFirestore.getInstance().collection("Products").document(productModel.id));
                                }

                            }
                        }

                        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Favourites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {

                                if (task1.isSuccessful()) {
                                    for (DocumentSnapshot documentSnapshot : task1.getResult().getDocuments()) {
                                        FavoritesModel favoritesModel = documentSnapshot.toObject(FavoritesModel.class);
                                        if (favoritesModel != null) {
                                            batch.delete(FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Favourites").document(favoritesModel.productId));
                                        }

                                    }
                                }
                                batch.delete(FirebaseFirestore.getInstance().collection("Users").document(uid));

                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        ProgressHud.dialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        try {
                                                            FirebaseAuth.getInstance().signOut();
                                                        }
                                                        catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        Intent intent = new Intent(context, SignInActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            });

                                        }
                                    }
                                });

                            }
                        });
                    });




    }
    public void cropUrl(Uri uri){

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            circleImageView.setImageBitmap(bitmap);
            uploadImageOnFirebase(uri);

        } catch (IOException ignored) {

        }

    }

    private void uploadImageOnFirebase(Uri resultUri) {
        ProgressHud.show(context,getString(R.string.updating));
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
        }).addOnCompleteListener(task -> {

            ProgressHud.dialog.dismiss();
            Toast.makeText(context, getString(R.string.updated), Toast.LENGTH_SHORT).show();
            if (task.isSuccessful()) {
                String downloadUri = String.valueOf(task.getResult());
                Map<String,String> map = new HashMap();
                map.put("profileImage",downloadUri);
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge());

            }

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            Const.changeImageActivity = "account";
            CropImage.activity(filepath).setOutputCompressQuality(60).setAspectRatio(1,1).start((MainActivity)context);
        }

    }
    public void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE);//If the user has denied the permission previously your code will come to this block

        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    public void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        int PICK_IMAGE_REQUEST = 1;
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        ((MainActivity)context).initializeAccountFragment(this);
    }
}