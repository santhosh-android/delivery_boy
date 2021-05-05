package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Adapters.OrderDetailsListAdapter;
import com.app.dosmiosdelivery.Model.OrderDetailsModelList;
import com.app.dosmiosdelivery.Model.VendorDetailsModel;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    private OrderDetailsListAdapter detailsListAdapter;
    private List<OrderDetailsModelList> detailsList;
    private RecyclerView rv_orItems;
    private ImageView img_back, imageCall, imageWhatsapp;
    private TextView tv_pick_order, tv_order_id, tv_name, tv_number,
            tv_paymentType, tv_paymentStatus, tv_address, tv_ttlAmount, tv_date, tvAlterNative;
    private String orderId, name, building_name, street, location, landmark, db_id;
    private String order_id, customer_name, mobile, date_time,
            payment_option, payment_status, sub_total, shipping, vat, total, alterNativeNumber;
    private ProgressBar pb_bar;
    private VendorDetailsModel vendorDeailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        orderId = getIntent().getStringExtra("orderId");
        castingViews();
        onClick();
        newOrderDetails();
    }

    private void newOrderDetails() {
        pb_bar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getOrderDetails(orderId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);
                        JSONArray itemsArray = jsonObject.getJSONArray("items");
                        detailsList = new ArrayList<>();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            String id = itemObject.getString("id");
                            String product = itemObject.getString("product");
                            String price = itemObject.getString("price");
                            String qty = itemObject.getString("qty");
                            String sub_total = itemObject.getString("sub_total");
                            String image = itemObject.getString("image");
                            JSONArray vendorDetailsArray = itemObject.getJSONArray("vendor_details");
                            if (vendorDetailsArray.length() == 0) {
                                vendorDeailsModel = null;
                            } else {
                                JSONObject vendorJsonObject = vendorDetailsArray.getJSONObject(0);
                                vendorDeailsModel = new VendorDetailsModel(
                                        vendorJsonObject.getString("shop_name"),
                                        vendorJsonObject.getString("contact_person_name"),
                                        vendorJsonObject.getString("mobile"),
                                        vendorJsonObject.getString("alternate_mobile"),
                                        vendorJsonObject.getString("address")
                                );
                            }
                            detailsList.add(new OrderDetailsModelList(id, product, "\u20b9 " + price,
                                    "Qty :" + qty, sub_total, image, vendorDeailsModel));
                        }
                        recyclerViewAttach();

                        JSONArray addresArray = jsonObject.getJSONArray("address");
                        JSONObject addressObject = addresArray.getJSONObject(0);
                        name = addressObject.getString("name");
                        building_name = addressObject.getString("building_name");
                        street = addressObject.getString("street");
                        location = addressObject.getString("location");
                        landmark = addressObject.getString("landmark");

                        order_id = jsonObject.getString("order_id");
                        customer_name = jsonObject.getString("customer_name");
                        mobile = jsonObject.getString("mobile");
                        //alterNativeNumber = jsonObject.getString("alternate_mobile");
                        date_time = jsonObject.getString("date_time");
                        payment_option = jsonObject.getString("payment_option");
                        payment_status = jsonObject.getString("payment_status");
                        sub_total = jsonObject.getString("sub_total");
                        shipping = jsonObject.getString("shipping");
                        vat = jsonObject.getString("gst");
                        total = jsonObject.getString("total");

                        tv_order_id.setText(order_id);
                        tv_name.setText(customer_name);
                        tv_number.setText(mobile);
                        //tvAlterNative.setText(alterNativeNumber);
                        tv_date.setText(date_time);
                        tv_paymentType.setText(payment_option);
                        tv_paymentStatus.setText(payment_status);
                        tv_ttlAmount.setText("\u20b9" + total);
                        tv_address.setText(building_name + "," + street + "," + "\n" + location + "," + landmark);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_bar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pb_bar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void recyclerViewAttach() {
        rv_orItems.setHasFixedSize(true);
        rv_orItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        detailsListAdapter = new OrderDetailsListAdapter(this, detailsList);
        rv_orItems.setAdapter(detailsListAdapter);
    }

    private void onClick() {

        img_back.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
        tv_pick_order.setOnClickListener(v -> orderPickedApiCall(pickUpApiMap()));
        imageCall.setOnClickListener(view -> callPhoneNumber());
        imageWhatsapp.setOnClickListener(view -> {
            String number = tv_number.getText().toString();
            String url = "https://api.whatsapp.com/send?phone=" + 91 + number;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    private void callPhoneNumber() {
        String number = tv_number.getText().toString();
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            }
        }
    }

    private Map<String, String> pickUpApiMap() {
        Map<String, String> createPickUpMap = new HashMap<>();
        createPickUpMap.put("order_id", order_id);
        createPickUpMap.put("db_id", db_id);
        return createPickUpMap;
    }

    private void orderPickedApiCall(Map<String, String> pickUpMap) {
        pb_bar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.pickUpOrderApi(pickUpMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String message = jsonObject.getString("message");
                            Toast.makeText(OrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            finishAffinity();
                            startActivity(new Intent(OrderDetailsActivity.this, PickupOrderActivity.class));
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void castingViews() {
        rv_orItems = findViewById(R.id.rv_orItems);
        img_back = findViewById(R.id.img_back);
        tv_pick_order = findViewById(R.id.tv_pick_order);
        pb_bar = findViewById(R.id.pb_pbar);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_paymentType = findViewById(R.id.tv_paymentType);
        tv_paymentStatus = findViewById(R.id.tv_paymentStatus);
        tv_address = findViewById(R.id.tv_address);
        tv_ttlAmount = findViewById(R.id.tv_ttlAmount);
        tv_date = findViewById(R.id.tv_date);
        tvAlterNative = findViewById(R.id.tvAlterNative);
        imageWhatsapp = findViewById(R.id.imageWhatsapp);
        imageCall = findViewById(R.id.imageCall);
    }
}
