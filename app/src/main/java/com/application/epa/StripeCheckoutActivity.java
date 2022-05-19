package com.application.epa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.application.epa.Models.Stripe.StripeModel;
import com.application.epa.Models.UserModel;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class StripeCheckoutActivity extends AppCompatActivity {
    private static final String BACKEND_URL = "https://softment.in/epa/";

    private final OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private String offerMode= "";
    private double amt = 0;
    private String productId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_checkout);

        productId = getIntent().getStringExtra("productId");

        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_live_51Kegc7LdhWFYtBr1zWdYDTNqBiQ4Fp06rE7x21UeH8fVm2r8r2DMTaKe9PbNOcQZPjhWb0cR6fBQiyRfKD6cvKCA00CifAg2Yk")
        );


        RadioButton day30RB = findViewById(R.id.day30RB);
        RadioButton day7RB = findViewById(R.id.day7RB);


        day30RB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                day30RB.setChecked(isChecked);
                day7RB.setChecked(!isChecked);
                offerMode = "30_days";
                amt = 25 * 100;
            }
        });

       day7RB.setOnCheckedChangeListener((buttonView, isChecked) -> {
           day7RB.setChecked(isChecked);
           day30RB.setChecked(!isChecked);
           offerMode = "7_days";
           amt = 10 * 100;
       });

       findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
       findViewById(R.id.example).setOnClickListener(v -> {
           AlertDialog.Builder builder = new AlertDialog.Builder(StripeCheckoutActivity.this);
           builder.setView(getLayoutInflater().inflate(R.layout.example_of_featured_ads,null));
           builder.show();
       });

        findViewById(R.id.continueToPay).setOnClickListener(v -> {
            if (offerMode.isEmpty()) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(StripeCheckoutActivity.this);

            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            View view = getLayoutInflater().inflate(R.layout.stripe_card_payment_view,null);
            TextView offer_title = view.findViewById(R.id.offer_title);
            AppCompatButton payBtn =  view.findViewById(R.id.pay);
            if ((offerMode.equalsIgnoreCase("30_days"))) {
                offer_title.setText(R.string.featured_Ad_for_30_days);
                payBtn.setText(R.string.pay_25_r);
            } else {
                offer_title.setText(R.string.featured_ad_for_7_days);
                payBtn.setText(R.string.pay_10_r);

            }
            startCheckout();
            CardInputWidget cardInputWidget = view.findViewById(R.id.cardInputWidget);

            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hook up the pay button to the card widget and stripe instance

                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(StripeCheckoutActivity.this, confirmParams);

                    }

                    alertDialog.dismiss();
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setView(view);
            alertDialog.show();
        });


    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", "BRL"); //dont change currency in testing phase otherwise it won't work
        itemMap.put("id", "featured_ad");
        itemMap.put("description","Featured_Ad_Service");
        itemMap.put("amount", amt);
        itemList.add(itemMap);

        payMap.put("items", itemList);

        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create.php")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<StripeCheckoutActivity> activityRef;

        PayCallback(@NonNull StripeCheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<StripeCheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull StripeCheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(paymentIntent);
                StripeModel stripeModel = gson.fromJson(json,StripeModel.class);
                addFeaturedAdDetails(stripeModel.getAmount());

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                Services.showDialog(StripeCheckoutActivity.this,"Payment Failed",paymentIntent.getLastPaymentError().getMessage());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final StripeCheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            Services.showDialog(StripeCheckoutActivity.this,"ERROR",e.getLocalizedMessage());
        }
    }

    public void addFeaturedAdDetails(int amount) {

        Map<String, Object> map = new HashMap<>();
        Date initialDate = new Date();
        try {
            initialDate = Services.getServerDate();


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (amount == 1000) {
            long sevenDaysMill = 604800000L;
            initialDate = new Date(initialDate.getTime() +  sevenDaysMill);

        }
        else {
            long thirtyDaysMill = 2592000000L;
            initialDate = new Date(initialDate.getTime() +  thirtyDaysMill);
        }

        map.put("adLastDate", initialDate);
        ProgressHud.show(StripeCheckoutActivity.this,"");
        FirebaseFirestore.getInstance().collection("Products").document(productId).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("amount",amt);
                    map1.put("date",new Date());
                    FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.uid).collection("Transactions").document().set(map1);
                    Services.showCenterToast(StripeCheckoutActivity.this,"Congratuations! Your product has featured on EPA plarform");
                }
                else {
                    Services.showDialog(StripeCheckoutActivity.this,"ERROR", task.getException().getLocalizedMessage());
                }
            }
        });
    }
}