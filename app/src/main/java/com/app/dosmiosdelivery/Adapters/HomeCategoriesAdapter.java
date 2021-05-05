package com.app.dosmiosdelivery.Adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Model.HomeCategoryMoadel;


import java.util.List;

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeCategoryMoadel> categoryMoadelList;
    private SharedPreferences sharedPreferences;
    private String db_id;

    public HomeCategoriesAdapter(Context mContext, List<HomeCategoryMoadel> categoryMoadelList) {
        this.mContext = mContext;
        this.categoryMoadelList = categoryMoadelList;
    }

    public interface CategeoryListenerInterface {
        void onCategeoryListenerInterface(int position);

       /* void onNeworderListener(boolean check);

        void onPickUpOrderListener(boolean check);

        void onCompletedListener(boolean check);*/

    }

    public CategeoryListenerInterface listenerInterface;

    public void setListenerInterface(CategeoryListenerInterface listenerInterface) {
        this.listenerInterface = listenerInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_home_categories_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeCategoryMoadel modelList = categoryMoadelList.get(position);
        holder.tv_category.setText(modelList.getTittleName());
        holder.tv_count.setText(modelList.getCount());

        //  if (position=)
       /* newOrdersApiCall(holder.tv_count, position);
        completedOrderApiCall(holder.tv_count, position);
        pickupOrderApiCall(holder.tv_count, position);*/


    }

    @Override
    public int getItemCount() {
        return categoryMoadelList.size();
    }

   /* private void newOrdersApiCall(TextView count, int position) {
        sharedPreferences = mContext.getSharedPreferences("pheebsDb", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        RythuFreshDeliveryInterface rythuFreshDeliveryInterface = RythuFreshServiceInstance.getRetrofit().create(RythuFreshDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getOrderApi(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")){
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");

                                if (listenerInterface != null) {
                                    listenerInterface.onNeworderListener(true);
                                }
                                if (position == 0) {
                                    count.setText(String.valueOf(jsonArray.length()));
                                }
                            }else {
                                if (listenerInterface != null) {
                                    listenerInterface.onNeworderListener(true);
                                }
                                if (position == 0) {
                                    count.setText("0");
                                }
                            }

                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (listenerInterface != null) {
                            listenerInterface.onNeworderListener(true);
                        }
                        if (position == 0) {
                            count.setText("0");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void completedOrderApiCall(TextView countCompleted, int position) {
        RythuFreshDeliveryInterface rythuFreshDeliveryInterface = RythuFreshServiceInstance.getRetrofit().create(RythuFreshDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getCompletedOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")){
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                if (listenerInterface != null) {
                                    listenerInterface.onCompletedListener(true);
                                }
                                if (position == 2) {
                                    countCompleted.setText(String.valueOf(jsonArray.length()));
                                }
                            }else {
                                if (listenerInterface != null) {
                                    listenerInterface.onCompletedListener(true);
                                }
                                if (position == 2) {
                                    countCompleted.setText("0");
                                }
                            }

                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (listenerInterface != null) {
                            listenerInterface.onCompletedListener(true);
                        }
                        if (position == 2) {
                            countCompleted.setText("0");
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    private void pickupOrderApiCall(TextView countPick, int position) {
        RythuFreshDeliveryInterface rythuFreshDeliveryInterface = RythuFreshServiceInstance.getRetrofit().create(RythuFreshDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.getPickUpOrders(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")){
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                if (listenerInterface != null) {
                                    listenerInterface.onPickUpOrderListener(true);
                                }
                                if (position == 1) {
                                    countPick.setText(String.valueOf(jsonArray.length()));
                                }
                            }else {
                                if (position == 1) {
                                    if (listenerInterface != null) {
                                        listenerInterface.onPickUpOrderListener(true);
                                    }
                                    countPick.setText("0");
                                }
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (position == 1) {
                            if (listenerInterface != null) {
                                listenerInterface.onPickUpOrderListener(true);
                            }
                            countPick.setText("0");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }*/


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_category, tv_count;
        private CardView cv_home;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_count = itemView.findViewById(R.id.tv_count);
            cv_home = itemView.findViewById(R.id.cv_home);

            cv_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerInterface != null) {
                        listenerInterface.onCategeoryListenerInterface(getAdapterPosition());
                    }
                }
            });

        }
    }
}
