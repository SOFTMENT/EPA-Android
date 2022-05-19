package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.application.epa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import com.application.epa.Models.UserModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class SupportActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        //BACK
        findViewById(R.id.back).setOnClickListener(v -> finish());

        //Donate
        findViewById(R.id.donate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SupportActivity.this);
                View view = getLayoutInflater().inflate(R.layout.donation_layout,null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setView(view);
                alertDialog.show();
            }
        });

        EditText do_you = findViewById(R.id.do_you);
        EditText how_hear = findViewById(R.id.how_hear);
        EditText feedback = findViewById(R.id.feedback);

        findViewById(R.id.submit).setOnClickListener(v -> {
            String sDoYou = do_you.getText().toString().trim();
            String sHowHear = how_hear.getText().toString().trim();
            String sFeedback = feedback.getText().toString().trim();
            if (sDoYou.isEmpty()){
                do_you.setError("Empty");
                do_you.requestFocus();
            }
            else if (sHowHear.isEmpty()){
                how_hear.setError("Empty");
                how_hear.requestFocus();
            }
            else if (sFeedback.isEmpty()){
                feedback.setError("Empty");
                feedback.requestFocus();
            }
            else{
                ProgressHud.show(SupportActivity.this,"");
                Map<String, String> map = new HashMap<>();
                map.put("howDidYouHearAboutEcde",sHowHear);
                map.put("participateInAnyActivism",sDoYou);
                map.put("feedback",sFeedback);
                FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.uid).collection("Survay").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        ProgressHud.dialog.dismiss();
                        feedback.setText("");
                        how_hear.setText("");
                        do_you.setText("");
                        Services.showDialog(SupportActivity.this,"Submitted","Thank you for quick survey.");
                    }
                });
            }

        });
    }


}