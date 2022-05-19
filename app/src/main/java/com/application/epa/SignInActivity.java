package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GoogleAuthProvider;


import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class SignInActivity extends AppCompatActivity {
    EditText emailAddress,password;
    private FirebaseAuth mAuth;
    private static final int STORAGE_PERMISSION_CODE = 4655;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    protected Animation blink_anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        //REQUEST_PERMISSION
        requestStoragePermission();


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 909);

            }
        });


        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        //CREATE ACCOUNT
        TextView createNew = findViewById(R.id.createNew);
        blink_anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        createNew.startAnimation(blink_anim);
        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignInActivity.this, CreateNewAccount.class));

            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        firebaseAuth(credential);

                    }

                    @Override
                    public void onCancel()
                    {

                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {

                        Services.showDialog(SignInActivity.this,getString(R.string.error),getString(R.string.something_went_wrong));
                    }


                });
        findViewById(R.id.facebook_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList( "email", "public_profile"));
            }
        });

        String code = Services.getLocateCode(this);


        TextView langName = findViewById(R.id.languageCode);
        if (code.equalsIgnoreCase("pt")) {
            langName.setText("Pt");
        }
        else {
            langName.setText("En");
        }


        //Language
        findViewById(R.id.langaugeCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] listItems = {"English","Portuguese"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                builder.setTitle(R.string.choose_language);
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


            }
        });

        //SignIn
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sEmail = emailAddress.getText().toString().trim();
                String sPassword = password.getText().toString().trim();
                if (sEmail.isEmpty()) {
                    Services.showCenterToast(SignInActivity.this,getString(R.string.enter_email_address));
                }
                else {
                    if (sPassword.isEmpty()) {
                        Services.showCenterToast(SignInActivity.this,getString(R.string.enter_password));
                    }
                    else {
                        ProgressHud.show(SignInActivity.this,getString(R.string.signin));
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                ProgressHud.dialog.dismiss();
                                if (task.isSuccessful()) {
                                        if (FirebaseAuth.getInstance().getCurrentUser() != null){
                                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                                    Services.sentEmailVerificationLink(SignInActivity.this);
                                            }
                                            else {
                                                Services.getCurrentUserData(SignInActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(),true);
                                            }

                                        }

                                    }
                                    else {
                                        if (task.getException() instanceof FirebaseAuthException) {
                                            Services.handleFirebaseERROR(SignInActivity.this, ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode());
                                        }
                                        else {
                                            Services.showDialog(SignInActivity.this,getString(R.string.error),getString(R.string.something_went_wrong));
                                        }


                                    }
                            }
                        });
                    }
                }
            }
        });

        //Reset
        findViewById(R.id.resetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sEmail = emailAddress.getText().toString().trim();
                if (sEmail.isEmpty()) {
                    Services.showCenterToast(SignInActivity.this,getString(R.string.enter_email_address));
                    return;
                }
                ProgressHud.show(SignInActivity.this,getString(R.string.resetting));
                FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ProgressHud.dialog.dismiss();
                        if (task.isSuccessful()) {
                            Services.showDialog(SignInActivity.this,getString(R.string.reset_password),getString(R.string.we_have_sent_password_reset_link));
                        }
                        else {
                            Services.showDialog(SignInActivity.this, getString(R.string.error),getString(R.string.something_went_wrong));
                        }
                    }
                });
            }
        });
    }

    private void firebaseAuth(AuthCredential credential) {

        ProgressHud.show(this,"");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressHud.dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (mAuth.getCurrentUser() != null){

                                Services.getCurrentUserData(SignInActivity.this,mAuth.getCurrentUser().getUid(),true);

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                           Services.showDialog(SignInActivity.this,"ERROR", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 909) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth(credential);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
              Services.showDialog(SignInActivity.this,"ERROR",e.getLocalizedMessage());
            }
        }
        else {
           mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void restart() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void setLocale(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,this.getResources().getDisplayMetrics());

        //SharedPref
        SharedPreferences.Editor sharedPreferences = getSharedPreferences("lang", MODE_PRIVATE).edit();
        sharedPreferences.putString("mylang",code);
        sharedPreferences.apply();
    }



    public void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//If the user has denied the permission previously your code will come to this block
//Here you can explain why you need this permission
//Explain here why you need this permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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

//    public void addUserDataOnServer(String uid,String fullName, String emailAddress, String imageUrl){
//        Map<String,Object> user = new HashMap<>();
//        user.put("uid",uid);
//        user.put("fullName",fullName);
//        user.put("emailAddress",emailAddress);
//        user.put("profileImage",imageUrl);
//        user.put("registrationDate", FieldValue.serverTimestamp());
//
//        ProgressHud.show(this,"");
//        FirebaseFirestore.getInstance().collection("User").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                ProgressHud.dialog.dismiss();
//                if (task.isSuccessful()) {
//                   Services.getCurrentUserData(SignInActivity.this,FirebaseAuth.getInstance().getCurrentUser().getUid(),emailAddress);
//                }
//            }
//        });
//    }

}