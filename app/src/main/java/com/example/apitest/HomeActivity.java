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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private TextView txtUsername;
    private TextView txtCookie;
    private EditText editOldPassword;
    private EditText editNewPassword;
    private Button btnLogout;
    private Button btnUpdatePass;
    private ProgressDialog progressDialog;

    public static final String BASE_URL= "http://192.168.0.101/p2a/";
    public static final String LOGOUT_URL = BASE_URL+"logout.inc.php/";
    public static final String UPDATE_PASS_URL = BASE_URL+"updatePass.inc.php/";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtUsername = findViewById(R.id.txtUsername);
        txtCookie = findViewById(R.id.txtCookie);
        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        progressDialog = new ProgressDialog(this);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        txtUsername.setText(sharedPreferences.getString(USER_NAME, ""));
        txtCookie.setText(sharedPreferences.getString(RESPONSE_HEADERS, ""));

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging you out");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGOUT_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Testing", "Logout: Response received");
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").equals("false")) {
                                Toast.makeText(HomeActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, "Logout: Error occurred: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("Testing", e.getMessage() + "Response was: " + response);
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Testing", "Logout: Error received");
                        progressDialog.hide();
                        Toast.makeText(HomeActivity.this, "Logout: Got error: " + error.getMessage() + ". Could not delete session!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Log.d("Testing", "Logout: Setting headers for request");
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
                Log.d("Testing", "Logout: Sending request");
                RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                requestQueue.add(stringRequest);
            }
        });

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this, "Change password pressed", Toast.LENGTH_SHORT).show();
                final String oldPassword = editOldPassword.getText().toString();
                final String newPassword = editNewPassword.getText().toString();

                progressDialog.setMessage("Changing your password...");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_PASS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Testing", "UpdatePass: Response received");
                        progressDialog.dismiss();
                        try {
                            Toast.makeText(HomeActivity.this, response, Toast.LENGTH_LONG).show();

                            JSONObject jsonObject = new JSONObject("");
//                            Toast.makeText(HomeActivity.this,   jsonObject.getString("message") + " " +  jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
//                            if (jsonObject.getString("error").equals("false")){
//                                Toast.makeText(HomeActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(HomeActivity.this, "Error occured: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Testing", "UpdatePass: Error received " + error.getMessage());
                        progressDialog.hide();
                        Toast.makeText(HomeActivity.this, "UpdatePass: Error: message" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d("Testing", "UpdatePass: Creating Params");
                        Map<String,String> params = new HashMap<>();
                        params.put("oldPassword", oldPassword);
                        params.put("newPassword", newPassword);
                        Log.d("Testing", "UpdatePass: Returning params");
                        return params;
                    }

//                    @Override
//                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                        Log.i("response",response.headers.toString());
//                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        Map<String, String> responseHeaders = response.headers;
////                        editor.putString(RESPONSE_HEADERS, response.headers.toString()); // all headers in one string
////                        editor.putString(HEADER_DATE, responseHeaders.get("Date"));
////                        editor.putString(HEADER_SERVER, responseHeaders.get("Server"));
////                        editor.putString(HEADER_X_POWERED_BY, responseHeaders.get("X-Powered-By"));
////                        editor.putString(HEADER_SET_COOKIE, responseHeaders.get("Set-Cookie"));
////                        Log.d("Testing", "cookie being set in home: " + responseHeaders.get("Set-Cookie"));
////                        editor.putString(HEADER_EXPIRES, responseHeaders.get("Expires"));
////                        editor.putString(HEADER_CACHE_CONTROL, responseHeaders.get("Cache-Control"));
////                        editor.putString(HEADER_PRAGMA, responseHeaders.get("Pragma"));
////                        //editor.putString(HEADER_CONTENT_LENGTH, responseHeaders.get("Content-Length"));
////                        editor.putString(HEADER_KEEP_ALIVE, responseHeaders.get("Keep-Alive"));
////                        editor.putString(HEADER_CONNECTION, responseHeaders.get("Connection"));
//                        //editor.putString(HEADER_CONTENT_TYPE, responseHeaders.get("Content-Type"));
//                        editor.apply();
//                        return super.parseNetworkResponse(response);
//                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        Map<String, String> headers = new HashMap<>();
//                        headers.put("Date", sharedPreferences.getString(HEADER_DATE, ""));
//                        headers.put("Server", sharedPreferences.getString(HEADER_SERVER, ""));
//                        headers.put("X-Powered-By", sharedPreferences.getString(HEADER_X_POWERED_BY, ""));
                        Log.d("Testing", "UpdatePass: putting cookie: " + sharedPreferences.getString(HEADER_SET_COOKIE, ""));
                        headers.put("Cookie", sharedPreferences.getString(HEADER_SET_COOKIE, ""));
//                        headers.put("Expires", sharedPreferences.getString(HEADER_EXPIRES, ""));
//                        headers.put("Cache-Control", sharedPreferences.getString(HEADER_CACHE_CONTROL, ""));
//                        headers.put("Pragma", sharedPreferences.getString(HEADER_PRAGMA, ""));
//                        headers.put("Content-Length", sharedPreferences.getString(HEADER_CONTENT_LENGTH, ""));
//                        headers.put("Keep-Alive", sharedPreferences.getString(HEADER_KEEP_ALIVE, ""));
//                        headers.put("Connection", sharedPreferences.getString(HEADER_CONNECTION, ""));
//                        headers.put("Content-Type", sharedPreferences.getString(HEADER_CONTENT_TYPE, ""));
                        Log.d("Testing", "UpdatePass: Headers sent with request: " + headers);
                        return headers;
                    }
                };

                Log.d("Testing", "UpdatePass: Sending request");
                RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                requestQueue.add(stringRequest);
            }
        });
    }
}