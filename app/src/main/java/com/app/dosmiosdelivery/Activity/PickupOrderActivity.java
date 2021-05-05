package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class PickupOrderActivity extends AppCompatActivity implements NewOrderRecyclerAdapter.OrderDetailsInterface {

    private NewOrderRecyclerAdapter pickupOrderAdapter;
    private RecyclerView rv_pickupOrderList;
    private ImageView img_back_pickup;
    private String db_id, orderId;
    private ProgressBar pb_bar;
    private List<NewOrdersRecyclerModel> pickModelList;
    private TextView tv_no_pickorders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_order);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();
        img_back_pickup.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter,R.anim.pop_exit);
        });

        rv_pickupOrderList.setHasFixedSize(true);
        rv_pickupOrderList.setLayoutManager(new LinearLayoutManager(this));

        pickUpOrderApiCall();
    }

    private void pickUpOrderApiCall() {
        pb_bar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getPickUpOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);

                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            pickModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray itemsArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject itemsObject = itemsArray.getJSONObject(i);
                                    orderId = itemsObject.getString("order_id");
                                    String customerName = itemsObject.getString("customer_name");
                                    String mobile = itemsObject.getString("mobile");
                                    String address = itemsObject.getString("address");
                                    String date_time = itemsObject.getString("date_time");
                                    String payment_option = itemsObject.getString("payment_option");
                                    String payment_status = itemsObject.getString("payment_status");
                                    pickModelList.add(new NewOrdersRecyclerModel(orderId, customerName, mobile,
                                            address, date_time, payment_option, payment_status));
                                }
                                pickupOrderAdapter = new NewOrderRecyclerAdapter(PickupOrderActivity.this, pickModelList);
                                pickupOrderAdapter.setOrderDetailsInterface(PickupOrderActivity.this);
                                rv_pickupOrderList.setAdapter(pickupOrderAdapter);
                                tv_no_pickorders.setVisibility(View.GONE);
                            } else {
                                tv_no_pickorders.setText("No orders found");
                                tv_no_pickorders.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        tv_no_pickorders.setVisibility(View.VISIBLE);
                        rv_pickupOrderList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                tv_no_pickorders.setVisibility(View.VISIBLE);
                rv_pickupOrderList.setVisibility(View.GONE);

            }
        });
    }

    private void castingViews() {
        rv_pickupOrderList = findViewById(R.id.rv_pickupOrderList);
        img_back_pickup = findViewById(R.id.img_back_pickup);
        pb_bar = findViewById(R.id.pb_pbar);
        tv_no_pickorders = findViewById(R.id.tv_no_pickorders);
    }

    @Override
    public void onOrderdetailsListener(int position) {
        Intent intent = new Intent(PickupOrderActivity.this, PickUpOrderDetailsActivity.class);
        intent.putExtra("order_id", pickModelList.get(position).getOrderId());
        overridePendingTransition(R.anim.enter,R.anim.exit);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
