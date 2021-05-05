package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Adapters.HomeCategoriesAdapter;
import com.app.dosmiosdelivery.Model.HomeCategoryMoadel;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;
import com.app.dosmiosdelivery.UserSessionManagement;

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

public class MainActivity extends AppCompatActivity implements HomeCategoriesAdapter.CategeoryListenerInterface {

    private HomeCategoriesAdapter categoriesAdapter;
    private List<HomeCategoryMoadel> categoryList;
    private RecyclerView rv_home_ctgs;
    private ImageView img_logout;
    private SharedPreferences sharedPreferences;
    private String db_id;
    private String newOrdersCount="", pickUpOrderCount="", completedOrderCount="";
    private ProgressBar pb_pbar;
    private SwipeRefreshLayout swipe_layout;
    private TextView tvnewOrderCount, tvCompletedCount, tvCountPickup;
    private CardView cvNewOrders, cv_pickup, cv_home, cvPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();

        img_logout.setOnClickListener(v -> {
            openAlert();
        });

        rv_home_ctgs = findViewById(R.id.rv_home_ctgs);
        ordersCountApiCall();

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ordersCountApiCall();
                swipe_layout.setRefreshing(false);
            }
        });

        cvNewOrders.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewOrdersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter,R.anim.exit);
        });
        cv_pickup.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, PickupOrderActivity.class));
            overridePendingTransition(R.anim.enter,R.anim.exit);

        });
        cv_home.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
            overridePendingTransition(R.anim.enter,R.anim.exit);
            startActivity(intent);
        });
        cvPayment.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
            overridePendingTransition(R.anim.enter,R.anim.exit);
        });
    }

    private void openAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent logOut = new Intent(MainActivity.this, LoginActivity.class);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        UserSessionManagement.getUserSessionManagement(MainActivity.this).logOut();
                        overridePendingTransition(R.anim.pop_enter,R.anim.pop_exit);
                        startActivity(logOut);
                        MainActivity.this.finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void castingViews() {
        pb_pbar = findViewById(R.id.pb_pbar);
        img_logout = findViewById(R.id.img_logout);
        swipe_layout = findViewById(R.id.swipe_layout);
        tvnewOrderCount = findViewById(R.id.tvnewOrderCount);
        tvCountPickup = findViewById(R.id.tvCountPickup);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        cvNewOrders = findViewById(R.id.cvNewOrders);
        cv_pickup = findViewById(R.id.cv_pickup);
        cv_home = findViewById(R.id.cv_home);
        cvPayment = findViewById(R.id.cvPayment);
    }

    private void ordersCountApiCall() {
        pb_pbar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getOrderApi(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    pb_pbar.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                newOrdersCount = String.valueOf(jsonArray.length());
                                tvnewOrderCount.setText(String.valueOf(jsonArray.length()));
                            } else {
                                newOrdersCount = "0";
                                tvnewOrderCount.setText("0");
                            }
                            pickUpOrderApiCall();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_pbar.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pb_pbar.setVisibility(View.GONE);
            }
        });
    }

    private void pickUpOrderApiCall() {
        pb_pbar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getPickUpOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    pb_pbar.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                pickUpOrderCount = String.valueOf(jsonArray.length());
                                tvCountPickup.setText(String.valueOf(jsonArray.length()));
                            } else {
                                pickUpOrderCount = "0";
                                tvCountPickup.setText("0");
                            }
                            completedOrderApiCall();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_pbar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pb_pbar.setVisibility(View.GONE);
            }
        });
    }

          /*  @Override
            public void onNeworderListener(boolean check) {
                if (check) {
                    pb_pbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPickUpOrderListener(boolean check) {
                if (check) {
                    pb_pbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCompletedListener(boolean check) {
                if (check) {
                    swipe_layout.setRefreshing(false);
                    pb_pbar.setVisibility(View.GONE);
                }
            }*/

    private void completedOrderApiCall() {
        pb_pbar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getCompletedOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    pb_pbar.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                completedOrderCount = String.valueOf(jsonArray.length());
                                tvCompletedCount.setText(String.valueOf(jsonArray.length()));
                                createRecyclerView();
                                swipe_layout.setRefreshing(false);
                            } else {
                                completedOrderCount = "0";
                                tvCompletedCount.setText("0");
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_pbar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                pb_pbar.setVisibility(View.GONE);
            }
        });

    }

    private void createRecyclerView() {
        rv_home_ctgs.setHasFixedSize(true);
        rv_home_ctgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        categoryList = new ArrayList<>();
        categoryList.add(new HomeCategoryMoadel("New Orders", newOrdersCount));
        categoryList.add(new HomeCategoryMoadel("Pickup Orders", pickUpOrderCount));
        categoryList.add(new HomeCategoryMoadel("Completed Orders", completedOrderCount));
        categoriesAdapter = new HomeCategoriesAdapter(MainActivity.this, categoryList);
        categoriesAdapter.setListenerInterface(MainActivity.this);
        rv_home_ctgs.setAdapter(categoriesAdapter);
    }

    @Override
    public void onCategeoryListenerInterface(int position) {
        if (position == 0) {
            Intent intent = new Intent(MainActivity.this, NewOrdersActivity.class);
            startActivity(intent);
        } else if (position == 1) {

        } else if (position == 2) {
            Intent intent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
            startActivity(intent);
        }
    }
}