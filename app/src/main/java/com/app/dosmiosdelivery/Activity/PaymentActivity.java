package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private ImageView imgBackPayment, imgQrCode;
    private ProgressBar pbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        castingViews();
        paymentDetailsApiCall();
        imgBackPayment.setOnClickListener(view -> onBackPressed());
    }

    private void paymentDetailsApiCall() {
        pbr.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getPayDetails();
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONObject object = responseObject.getJSONObject("response");
                        if (object.getString("status").equalsIgnoreCase("Valid")) {
                            String qr = object.getString("scanner_image");
                            Glide.with(PaymentActivity.this)
                                    .load(qr)
                                    .into(imgQrCode);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void castingViews() {
        imgBackPayment = findViewById(R.id.imgBackPayment);
        imgQrCode = findViewById(R.id.imgQrCode);
        pbr = findViewById(R.id.pbr);
    }
}