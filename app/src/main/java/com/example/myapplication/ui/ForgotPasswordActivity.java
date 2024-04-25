package com.example.myapplication.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.R;
import com.google.common.hash.Hashing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void sendResetEmail(View view) {
        String email = ((EditText) findViewById(R.id.mail_reset)).getText().toString();

        new Handler(Looper.getMainLooper()).post(() -> {
            LottieAnimationView loading = findViewById(R.id.animationView);
            loading.playAnimation();
        });

        AsyncTask.execute(() -> {
            try {
                URL url = new URL(getResources().getString(R.string.base_url) + "/forgotPassword");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);

                String inputString = "{\"email\": \"" + email + "\"}";
                Log.i("test", inputString);
                try(OutputStream os = http.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                if (http.getResponseCode() == 404) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Account does not exist", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationView);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                    });
                    return;
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    Log.i("test", response.toString());
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Reset email sent. Please check your email", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationView);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                    });
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}