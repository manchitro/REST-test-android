package com.example.apitest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;

    public static final String BASE_URL= "http://192.168.0.101/p2a/";
    public static final String LOGIN_URL = BASE_URL+"login.inc.php/";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String RESPONSE_HEADERS = "responseHeaders";
    public static final String HEADER_DATE = "headerDate";
    public static final String HEADER_SERVER = "headerServer";
    public static final String HEADER_X_POWERED_BY = "headerPoweredBy";
    public static final String HEADER_SET_COOKIE = "headerSetCookie";
    public static final String HEADER_EXPIRES = "headerExpires";
    public static final String HEADER_CACHE_CONTROL = "headerCacheControl";
    public static final String HEADER_PRAGMA = "headerPragma";
    public static final String HEADER_CONTENT_LENGTH = "headerContentLength";
    public static final String HEADER_KEEP_ALIVE = "headerKeepAlive";
    public static final String HEADER_CONNECTION = "headerConnection";
    public static final String HEADER_CONTENT_TYPE = "headerContentType";

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
//        Toast.makeText(this, ipAddress, Toast.LENGTH_SHORT).show();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString().trim();
                password = editPassword.getText().toString().trim();
                if (!password.equals("") && !username.equals("")){
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Testing", "Login: Response received");
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
                                        Toast.makeText(MainActivity.this, "Login: Some error occured please try again", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "Login: Error occured: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Testing", "Login: Error received");
                            progressDialog.hide();
                            Toast.makeText(MainActivity.this, "Login: Got error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            Log.d("Testing", "Login: Setting body for request");
                            params.put("username", username);
                            params.put("password", password);
                            return params;
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            Log.d("Testing","Login: Network response: " + response.headers.toString());
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Map<String, String> responseHeaders = response.headers;
                            editor.putString(RESPONSE_HEADERS, response.headers.toString()); // all headers in one string
                            editor.putString(HEADER_DATE, responseHeaders.get("Date"));
                            editor.putString(HEADER_SERVER, responseHeaders.get("Server"));
                            editor.putString(HEADER_X_POWERED_BY, responseHeaders.get("X-Powered-By"));
                            editor.putString(HEADER_SET_COOKIE, responseHeaders.get("Set-Cookie"));
                            Log.d("Testing", "Login: cookie being set in login: " + responseHeaders.get("Set-Cookie"));
                            editor.putString(HEADER_EXPIRES, responseHeaders.get("Expires"));
                            editor.putString(HEADER_CACHE_CONTROL, responseHeaders.get("Cache-Control"));
                            editor.putString(HEADER_PRAGMA, responseHeaders.get("Pragma"));
                            //editor.putString(HEADER_CONTENT_LENGTH, responseHeaders.get("Content-Length"));
                            editor.putString(HEADER_KEEP_ALIVE, responseHeaders.get("Keep-Alive"));
                            editor.putString(HEADER_CONNECTION, responseHeaders.get("Connection"));
                            //editor.putString(HEADER_CONTENT_TYPE, responseHeaders.get("Content-Type"));
                            editor.apply();
                            return super.parseNetworkResponse(response);
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Log.d("Testing", "Login: Setting headers for request");
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            Map<String, String> headers = new HashMap<>();
//                            headers.put("Date", sharedPreferences.getString(HEADER_DATE, ""));
//                            headers.put("Server", sharedPreferences.getString(HEADER_SERVER, ""));
//                            headers.put("X-Powered-By", sharedPreferences.getString(HEADER_X_POWERED_BY, ""));
                            headers.put("Cookie", sharedPreferences.getString(HEADER_SET_COOKIE, ""));
//                            headers.put("Expires", sharedPreferences.getString(HEADER_EXPIRES, ""));
//                            headers.put("Cache-Control", sharedPreferences.getString(HEADER_CACHE_CONTROL, ""));
//                            headers.put("Pragma", sharedPreferences.getString(HEADER_PRAGMA, ""));
//                            headers.put("Content-Length", sharedPreferences.getString(HEADER_CONTENT_LENGTH, ""));
//                            headers.put("Keep-Alive", sharedPreferences.getString(HEADER_KEEP_ALIVE, ""));
//                            headers.put("Connection", sharedPreferences.getString(HEADER_CONNECTION, ""));
//                            headers.put("Content-Type", sharedPreferences.getString(HEADER_CONTENT_TYPE, ""));
                            return headers;
                        }
                    };
                    Log.d("Testing", "Login: Sending request");
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    requestQueue.add(stringRequest);
                }
                else{
                    Toast.makeText(MainActivity.this, "Login: Please enter Username and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}