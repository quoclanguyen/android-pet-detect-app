package com.example.myapplication.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView eye = (ImageView) findViewById(R.id.seePass);
        EditText pass = (EditText) findViewById(R.id.pass);
        int type = pass.getInputType();
        eye.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //do something when pressed down
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    pass.setTypeface(typeface);
                    pass.setSelection(pass.getText().toString().length());
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                //do something when let go
                pass.setInputType(type);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    pass.setTypeface(typeface);
                    pass.setSelection(pass.getText().toString().length());
                }
            }
            return true;
        });
    }

    public void openForgotPassPage(View view) {
    }

    public void login(View view) {
        String email = ((EditText) findViewById(R.id.mail)).getText().toString();
        String pass = ((EditText) findViewById(R.id.pass)).getText().toString();

        new Handler(Looper.getMainLooper()).post(() -> {
            LottieAnimationView loading = findViewById(R.id.animationViewLogin);
            loading.playAnimation();
            Button regButton = findViewById(R.id.buttonLog);
            regButton.setEnabled(false);
        });

        AsyncTask.execute(() -> {
            try {
                URL url = new URL(getResources().getString(R.string.baseURL) + "/login");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);
                StringBuilder jsonInputString = new StringBuilder("{\"email\": \"" + email + "\", ");

                String passHash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();

                jsonInputString.append("\"password\": \"").append(passHash).append("\"}");
                String inputString = jsonInputString.toString();
                Log.i("test", inputString);
                try(OutputStream os = http.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                if (http.getResponseCode() == 404) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Account does not exist", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationViewLogin);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                        Button regButton = findViewById(R.id.buttonLog);
                        regButton.setEnabled(true);
                    });
                    return;
                }
                if (http.getResponseCode() == 401) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationViewLogin);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                        Button regButton = findViewById(R.id.buttonLog);
                        regButton.setEnabled(true);
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
//                    Intent intent = new Intent(this, ConfirmActivity.class);
//                    intent.putExtra("email", email);
//                    intent.putExtra("pass", passHash);
//
//                    startActivity(intent);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Login success", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationViewLogin);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                        Button regButton = findViewById(R.id.buttonReg);
                        regButton.setEnabled(true);
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