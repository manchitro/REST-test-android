package com.example.apitest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;

    public static final String BASE_URL= "http://192.168.0.102/p2a/";
    public static final String LOGIN_URL = BASE_URL+"login.inc.php/";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String RESPONSE_HEADERS = "responseHeaders";
    public static final String SET_COOKIE = "setCookie";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = editUsername.getText().toString().trim();
                final String password = editPassword.getText().toString().trim();
                if (!password.equals("") && !username.equals("")){
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                //Toast.makeText(MainActivity.this,   jsonObject.getString("message") + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.getString("error").equals("false")){
                                    Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(USER_NAME, jsonObject.getString("userName"));
                                    editor.putString(USER_ID, jsonObject.getString("userId"));
                                    editor.apply();

                                    if (!Objects.equals(sharedPreferences.getString(USER_ID, ""), "") && !Objects.equals(sharedPreferences.getString(USER_NAME, ""), "")){
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Some error occured please try again", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "Error occured: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("password", password);
                            return params;
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            Log.i("response",response.headers.toString());
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Map<String, String> responseHeaders = response.headers;
                            editor.putString(RESPONSE_HEADERS, response.headers.toString());
                            editor.putString(SET_COOKIE, responseHeaders.get("Set-Cookie"));
                            editor.apply();
                            return super.parseNetworkResponse(response);
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    requestQueue.add(stringRequest);
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter Username and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}