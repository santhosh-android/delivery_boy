package com.app.dosmiosdelivery.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Adapters.NewOrderRecyclerAdapter;
import com.app.dosmiosdelivery.Model.NewOrdersRecyclerModel;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedOrdersActivity extends AppCompatActivity {

    private NewOrderRecyclerAdapter completedAdapter;
    private RecyclerView rv_completedOrders;
    private ImageView img_back_cmp;
    private String db_id;
    private ProgressBar pb_bar;
    private List<NewOrdersRecyclerModel> newOrderModelList;
    private TextView tvNoOrders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_orders);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        img_back_cmp = findViewById(R.id.img_back_cmp);
        pb_bar = findViewById(R.id.pb_pbar);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        img_back_cmp.setOnClickListener(v -> onBackPressed());
        rv_completedOrders = findViewById(R.id.rv_completedOrders);
        rv_completedOrders.setHasFixedSize(true);
        rv_completedOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        completedApiCall();
    }

    private void completedApiCall() {
        pb_bar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getCompletedOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String respnseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(respnseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            newOrderModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ordersObject = jsonArray.getJSONObject(i);

                                    String orderId = ordersObject.getString("order_id");
                                    String customerName = ordersObject.getString("customer_name");
                                    String mobile = ordersObject.getString("mobile");
                                    String address = ordersObject.getString("address");
                                    String date_time = ordersObject.getString("date_time");
                                    String payment_option = ordersObject.getString("payment_option");
                                    String payment_status = ordersObject.getString("payment_status");
                                    newOrderModelList.add(new NewOrdersRecyclerModel(orderId, customerName,
                                            mobile, address, date_time, payment_option, payment_status));
                                }
                                completedAdapter = new NewOrderRecyclerAdapter(CompletedOrdersActivity.this, newOrderModelList);
                                rv_completedOrders.setAdapter(completedAdapter);
                            } else {
                                tvNoOrders.setVisibility(View.VISIBLE);
                                tvNoOrders.setText("No Orders found");
                                rv_completedOrders.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_bar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pb_bar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
