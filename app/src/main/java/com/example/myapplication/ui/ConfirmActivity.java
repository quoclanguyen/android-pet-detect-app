package com.example.myapplication.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Objects;

public class ConfirmActivity extends AppCompatActivity {
    private String email;
    private String pass;
    private final long OTP_TTL_MAX = 60 * 3 * 1000;
    private long timeLeftInMillis = OTP_TTL_MAX;
    private TextView otpTtlTextView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);
        email = Objects.requireNonNull(getIntent().getExtras()).getString("email", "");
        pass = Objects.requireNonNull(getIntent().getExtras()).getString("pass", "");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCountDown();
    }
    private void startCountDown() {
        otpTtlTextView = findViewById(R.id.otp_ttl);
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
            }
        }.start();
    }
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("OTP will be expired after %02d:%02d minutes", minutes, seconds);
        otpTtlTextView.setText(timeLeftFormatted);
    }

    public void confirmOTP(View view) {
        EditText otpView = findViewById(R.id.otp_view);
        String otp = otpView.getText().toString();

        AsyncTask.execute(() -> {
            try {
                URL url = new URL(getResources().getString(R.string.base_url) + "/registerConfirm");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);
                StringBuilder jsonInputString = new StringBuilder("{\"email\": \"" + email + "\", ");

                jsonInputString.append("\"otp\": \"").append(otp).append("\"}");
                String inputString = jsonInputString.toString();
                Log.i("test", inputString);
                try(OutputStream os = http.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                if (http.getResponseCode() == 409) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Error. Please resend OTP", Toast.LENGTH_LONG)).show();
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
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("createdSuccess", true);
                    startActivity(intent);
                    finish();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resendOTP(View view) {
//        new Handler(Looper.getMainLooper()).post(() -> {
//            LottieAnimationView loading = findViewById(R.id.animationView);
//            loading.playAnimation();
//            Button regButton = findViewById(R.id.buttonReg);
//            regButton.setEnabled(false);
//        });
        AsyncTask.execute(() -> {
            try {
                URL url = new URL(getResources().getString(R.string.base_url) + "/register");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);
                StringBuilder jsonInputString = new StringBuilder("{\"email\": \"" + email + "\", ");

                jsonInputString.append("\"password\": \"").append(pass).append("\"}");
                String inputString = jsonInputString.toString();
                Log.i("test", inputString);
                try(OutputStream os = http.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
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
                        (Toast.makeText(this, "OTP sent", Toast.LENGTH_LONG)).show();
                    });
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    timeLeftInMillis = OTP_TTL_MAX;
                    startCountDown();
                });
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}