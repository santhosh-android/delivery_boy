package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Adapters.NewOrderRecyclerAdapter;
import com.app.dosmiosdelivery.Model.NewOrdersRecyclerModel;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;


import org.jetbrains.annotations.NotNull;
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

public class NewOrdersActivity extends AppCompatActivity implements NewOrderRecyclerAdapter.OrderDetailsInterface {

    private RecyclerView rv_newOrders;
    private ImageView img_back_new;
    private String userId, orderId;
    private SharedPreferences sharedPreferences;
    private ProgressBar pb_bar;
    private TextView tv_no_orders;
    private ImageView no_order;
    private List<NewOrdersRecyclerModel> newOrderModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_orders);

        sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", "");

        tv_no_orders = findViewById(R.id.tv_no_orders);
        img_back_new = findViewById(R.id.img_back_new);
        img_back_new.setOnClickListener(v -> {
                onBackPressed();
            overridePendingTransition(R.anim.pop_enter,R.anim.pop_exit);
        });
        pb_bar = findViewById(R.id.pb_pbar);
        no_order = findViewById(R.id.no_order);
        rv_newOrders = findViewById(R.id.rv_newOrders);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newOrdersAllApiCall();
    }

    private void newOrdersAllApiCall() {
        pb_bar.setVisibility(View.VISIBLE);
        rv_newOrders.setLayoutManager(new LinearLayoutManager(this));
        rv_newOrders.setHasFixedSize(true);

        dosmiossDeliveryInterface deliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getOrderApi(userId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                newOrderModelList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ordersObject = jsonArray.getJSONObject(i);
                                    orderId = ordersObject.getString("order_id");
                                    String customerName = ordersObject.getString("customer_name");
                                    String mobile = ordersObject.getString("mobile");
                                    String address = ordersObject.getString("address");
                                    String date_time = ordersObject.getString("date_time");
                                    String payment_option = ordersObject.getString("payment_option");
                                    String payment_status = ordersObject.getString("payment_status");

                                    newOrderModelList.add(new NewOrdersRecyclerModel(orderId, customerName, mobile, address, date_time, payment_option, payment_status));
                                }

                                NewOrderRecyclerAdapter adapter = new NewOrderRecyclerAdapter(NewOrdersActivity.this, newOrderModelList);
                                rv_newOrders.setAdapter(adapter);
                                adapter.setOrderDetailsInterface(NewOrdersActivity.this);
                            } else {
                                tv_no_orders.setVisibility(View.VISIBLE);
                                tv_no_orders.setText("No orders found");
                                rv_newOrders.setVisibility(View.GONE);
                                no_order.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        tv_no_orders.setVisibility(View.VISIBLE);
                        rv_newOrders.setVisibility(View.GONE);
                        no_order.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public void onOrderdetailsListener(int position) {
        Intent intent = new Intent(NewOrdersActivity.this, OrderDetailsActivity.class);
        intent.putExtra("orderId", newOrderModelList.get(position).getOrderId());
        overridePendingTransition(R.anim.enter,R.anim.exit);
        startActivity(intent);
    }
}
