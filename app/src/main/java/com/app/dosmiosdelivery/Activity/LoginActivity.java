package com.app.dosmiosdelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryInterface;
import com.app.dosmiosdelivery.Services.dosmiossDeliveryServiceInstance;
import com.app.dosmiosdelivery.UserSessionManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
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

public class LoginActivity extends AppCompatActivity {

    private EditText et_username, et_password;
    private Button btn_login;
    private String userName, password, db_id, token = "";
    private ProgressBar pb_pbar;
    SharedPreferences sharedPreferences;
    private TextView tv_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("pheebsDb", MODE_PRIVATE);
        token = sharedPreferences.getString("device_id", "");
        tv_login = findViewById(R.id.tv_login);

        castingViews();
        pb_pbar.setVisibility(View.GONE);

        btn_login.setOnClickListener(v -> {
            variablegetText();
            if (!validateUserName() || !validatePassword()) {
            } else {
                if (token.isEmpty()) {
                    getToken();
                } else {
                    btnLoginApiCall(createLoginMapApi());
                }
            }

        });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("device_id", token);
                        editor.apply();
                        editor.commit();
                        btnLoginApiCall(createLoginMapApi());
                    }
                });
    }

    private void castingViews() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        pb_pbar = findViewById(R.id.pb_pbar);
    }

    private void variablegetText() {
        userName = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();
    }

    private boolean validateUserName() {
        if (userName.isEmpty()) {
            et_username.setError("Field Must be Filled");
            return false;
        } else {
            et_username.setError(null);
        }
        return true;
    }

    private boolean validatePassword() {
        if (password.isEmpty()) {
            et_password.setError("Field Must be Filled");
            return false;
        } else {
            et_password.setError(null);
        }
        return true;
    }

    private Map<String, String> createLoginMapApi() {
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("mobile", userName);
        loginMap.put("password", password);
        loginMap.put("token", token);
        return loginMap;
    }

    private void btnLoginApiCall(Map<String, String> loginMapApi) {
        pb_pbar.setVisibility(View.VISIBLE);
        dosmiossDeliveryInterface rythuFreshDeliveryInterface = dosmiossDeliveryServiceInstance.getRetrofit().create(dosmiossDeliveryInterface.class);
        Call<ResponseBody> loginBodyCall = rythuFreshDeliveryInterface.getLogin(loginMapApi);
        loginBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                pb_pbar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String susMessage = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, susMessage, Toast.LENGTH_SHORT).show();
                            db_id = jsonObject.getString("db_id");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UserId", db_id);
                            editor.apply();
                            editor.commit();
                            UserSessionManagement.getUserSessionManagement(LoginActivity.this).saveUser(db_id);
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            startActivity(loginIntent);

                        } else {
                            if (jsonObject.getString("status").equalsIgnoreCase("Invalid")) {
                                String failMessage = jsonObject.getString("message");
                                Toast.makeText(LoginActivity.this, failMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pb_pbar.setVisibility(View.GONE);
            }
        });
    }
}
