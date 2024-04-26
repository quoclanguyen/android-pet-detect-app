package com.example.myapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "petreco_pref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    public void onBackPressed() {
        if (!isLoggedIn()) {
            finishAffinity();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (isLoggedIn()) {
            startActivity(new Intent(this, PetsListActivity.class));
            finish();
        }
        else {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void openLoginPage(View view) {
//        Intent loginPage = new Intent(this, LoginActivity.class);
//        startActivity(loginPage);
        startActivity(
                FlutterActivity
                        .withNewEngine()
                        .initialRoute("/")
                        .build(this)
        );
    }

    public void openRegPage(View view) {
        Intent regPage = new Intent(this, RegisterActivity.class);
        startActivity(regPage);
    }
}