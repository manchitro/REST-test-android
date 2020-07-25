package com.example.apitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private TextView txtUsername;
    private TextView txtCookie;
    private Button btnLogout;

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
        setContentView(R.layout.activity_home);

        txtUsername = findViewById(R.id.txtUsername);
        txtCookie = findViewById(R.id.txtCookie);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        txtUsername.setText(sharedPreferences.getString(USER_NAME, ""));
        txtCookie.setText(sharedPreferences.getString(SET_COOKIE, ""));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(USER_NAME);
                editor.remove(USER_ID);
                editor.remove(RESPONSE_HEADERS);
                editor.apply();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}