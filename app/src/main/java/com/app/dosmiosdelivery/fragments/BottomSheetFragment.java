package com.app.dosmiosdelivery.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Activity.MainActivity;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment implements CodBottomSheetFragment.CodBottomSheetListener {
    private TextView cod_pm, online_pm;
    private String orderId, db_id, totalAmount;
    private SharedPreferences sharedPreferences;

    private PaymentBottomSheetListener bottomSheetListener;
    private ProgressDialog progressDialog;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment getInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        sharedPreferences = getActivity().getSharedPreferences("pheebsDb", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        Bundle arguments = getArguments();
        orderId = arguments.getString("order_id");
        totalAmount = arguments.getString("total");

        cod_pm = view.findViewById(R.id.cod_pm);
        online_pm = view.findViewById(R.id.online_pm);
        cod_pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodBottomSheetFragment codBottomSheetFragment = CodBottomSheetFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("order_id", orderId);
                bundle.putString("ttlAmount", totalAmount);
                codBottomSheetFragment.setArguments(bundle);
                codBottomSheetFragment.setCodBottomSheetListener(BottomSheetFragment.this);
                codBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "codBottomSheet");
                dismiss();
            }
        });

        online_pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryOrderApiCall(orderDeliveryApi());
            }
        });

        return view;
    }

    private Map<String, String> orderDeliveryApi() {
        Map<String, String> deliveryApiMap = new HashMap<>();
        deliveryApiMap.put("order_id", orderId);
        deliveryApiMap.put("db_id", db_id);
        deliveryApiMap.put("payment_via", "");
        deliveryApiMap.put("payment_remarks", "");
        return deliveryApiMap;
    }

    private void deliveryOrderApiCall(Map<String, String> deliveryMap) {
        progressDialog.show();
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = rythuFreshDeliveryInterface.orderDeliveryApiCall(deliveryMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject respnseObject = new JSONObject(responseString);
                        JSONArray responseArray = respnseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            progressDialog.hide();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dismiss();
                        } else if (jsonObject.getString("status").equalsIgnoreCase("Invalid")) {
                            String errorMessage = jsonObject.getString("message");
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    public interface PaymentBottomSheetListener {
        void onEventSelected(String eventType);
    }

    public void setBottomSheetListener(PaymentBottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    @Override
    public void onCodListener(String eventType) {

    }
}
