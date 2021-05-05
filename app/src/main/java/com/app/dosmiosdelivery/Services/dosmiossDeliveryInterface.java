package com.app.dosmiosdelivery.Services;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface dosmiossDeliveryInterface {

    String BASE_URL = "https://dosmioss.com/webservices/";

    @FormUrlEncoded
    @POST("db-login")
    Call<ResponseBody> getLogin(@FieldMap Map<String, String> loginMap);

    @FormUrlEncoded
    @POST("db-new-orders")
    Call<ResponseBody> getOrderApi(@Field("db_id") String getOrders);

    @FormUrlEncoded
    @POST("db-view-order")
    Call<ResponseBody> getOrderDetails(@Field("order_id") String getDetails);

    @FormUrlEncoded
    @POST("db-confirm-pickup")
    Call<ResponseBody> pickUpOrderApi(@FieldMap Map<String, String> pickUpOrder);

    @FormUrlEncoded
    @POST("db-pickup-orders")
    Call<ResponseBody> getPickUpOrders(@Field("db_id") String getPickUpApi);

    @FormUrlEncoded
    @POST("db-completed-orders")
    Call<ResponseBody> getCompletedOrders(@Field("db_id") String getCompletedOrds);

    @FormUrlEncoded
    @POST("db-confirm-delivery")
    Call<ResponseBody> orderDeliveryApiCall(@FieldMap Map<String, String> deliveryOrder);

    @GET("db-payonline-details")
    Call<ResponseBody> getPayDetails();


}
