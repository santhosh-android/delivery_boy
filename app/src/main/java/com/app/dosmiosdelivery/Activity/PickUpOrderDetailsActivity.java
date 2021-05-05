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
import com.app.dosmiosdelivery.Model.VendorDetailsModel;
import com.app.dosmiosdelivery.fragments.BottomSheetFragment;
import com.app.dosmiosdelivery.Model.OrderDetailsModelList;
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

public class PickUpOrderDetailsActivity extends AppCompatActivity implements BottomSheetFragment.PaymentBottomSheetListener {
    private TextView tv_delivery_order, tv_payment_status;
    private OrderDetailsListAdapter orderDetailsListAdapter;
    private List<OrderDetailsModelList> detailsModelList;
    private RecyclerView rv_orDetails_pick;
    private ImageView bck_image, imageCall, imageWhatsapp;
    private String notPaid = "Not Paid";
    private TextView tv_order_id, tv_name, tv_number, tv_paymentType, tv_paymentStatus, tv_address,
            tv_ttlAmount, tv_date, tvAlterNative;
    private String orderId;
    private String order_Id, name, building_name, street, location, landmark, db_id, alterNativeNumber;
    String order_id, customer_name, mobile, date_time,
            payment_option, payment_status, sub_total, shipping, vat, total;
    private ProgressBar pbr;
    private VendorDetailsModel vendorDeailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_order_details);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        orderId = getIntent().getStringExtra("order_id");
        castingViews();
        onClick();
        orderPickupItemsRv();
        pickUpOrderDetailsApiCall();
    }

    private void pickUpOrderDetailsApiCall() {
        pbr.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getOrderDetails(orderId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.INVISIBLE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject itemsObject = responseArray.getJSONObject(0);
                        JSONArray itemsArray = itemsObject.getJSONArray("items");
                        detailsModelList = new ArrayList<>();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject jsonObject = itemsArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String product = jsonObject.getString("product");
                            String price = jsonObject.getString("price");
                            String qty = jsonObject.getString("qty");
                            String sub_total = jsonObject.getString("sub_total");
                            String image = jsonObject.getString("image");
                            JSONArray vendorDetailsArray = jsonObject.getJSONArray("vendor_details");
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
                            detailsModelList.add(new OrderDetailsModelList(id, product, "\u20b9 " + price, "Qty :" + qty, sub_total, image, vendorDeailsModel));
                        }
                        orderDetailsListAdapter = new OrderDetailsListAdapter(PickUpOrderDetailsActivity.this, detailsModelList);
                        rv_orDetails_pick.setAdapter(orderDetailsListAdapter);

                        JSONArray addresArray = itemsObject.getJSONArray("address");
                        JSONObject addressObject = addresArray.getJSONObject(0);
                        name = addressObject.getString("name");
                        building_name = addressObject.getString("building_name");
                        street = addressObject.getString("street");
                        location = addressObject.getString("location");
                        landmark = addressObject.getString("landmark");

                        order_id = itemsObject.getString("order_id");
                        customer_name = itemsObject.getString("customer_name");
                        mobile = itemsObject.getString("mobile");
                        date_time = itemsObject.getString("date_time");
                        payment_option = itemsObject.getString("payment_option");
                        customer_name = itemsObject.getString("customer_name");
                        payment_status = itemsObject.getString("payment_status");
                        sub_total = itemsObject.getString("sub_total");
                        shipping = itemsObject.getString("shipping");
                        vat = itemsObject.getString("gst");
                        total = itemsObject.getString("total");

                        tv_order_id.setText(order_id);
                        tv_name.setText(customer_name);
                        tv_number.setText(mobile);
                        tv_date.setText(date_time);
                        tv_paymentType.setText(payment_option);
                        tv_paymentStatus.setText(payment_status);
                        tv_ttlAmount.setText("\u20b9" + total);
                        tv_address.setText(building_name + "," + street + "," + "\n" + location + "," + landmark);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.INVISIBLE);
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
        tv_delivery_order = findViewById(R.id.tv_delivery_order);
        rv_orDetails_pick = findViewById(R.id.rv_orDetails_pick);
        bck_image = findViewById(R.id.img_back_details);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_paymentType = findViewById(R.id.tv_paymentType);
        tv_paymentStatus = findViewById(R.id.tv_paymentStatus);
        tv_address = findViewById(R.id.tv_address);
        tv_ttlAmount = findViewById(R.id.tv_ttlAmount);
        tv_date = findViewById(R.id.tv_date);
        pbr = findViewById(R.id.pbr);
        tvAlterNative = findViewById(R.id.tvAlterNative);
        imageWhatsapp = findViewById(R.id.imageWhatsapp);
        imageCall = findViewById(R.id.imageCall);
    }

    private void onClick() {
        tv_delivery_order.setOnClickListener(v -> orderDeliverApiCall(orderDeliveryMap()));

        bck_image.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });

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
                    ActivityCompat.requestPermissions(PickUpOrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
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

    private Map<String, String> orderDeliveryMap() {
        pbr.setVisibility(View.VISIBLE);
        Map<String, String> deliveryApiMap = new HashMap<>();
        deliveryApiMap.put("order_id", orderId);
        deliveryApiMap.put("db_id", db_id);
        deliveryApiMap.put("payment_via", "");
        deliveryApiMap.put("payment_remarks", "");
        return deliveryApiMap;
    }

    private void orderDeliverApiCall(Map<String, String> deliveryMap) {
        pbr.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface deliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.orderDeliveryApiCall(deliveryMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject statusObject = responseArray.getJSONObject(0);
                        if (statusObject.getString("status").equalsIgnoreCase("Valid")) {
                            pbr.setVisibility(View.INVISIBLE);
                            String message = statusObject.getString("message");
                            Toast.makeText(PickUpOrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PickUpOrderDetailsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            startActivity(intent);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void orderPickupItemsRv() {
        rv_orDetails_pick.setHasFixedSize(true);
        rv_orDetails_pick.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onEventSelected(String eventType) {
        if (eventType.equals(Constants.COD)) {

        } else if (eventType.equals(Constants.ONLINE)) {

        }
    }
}
