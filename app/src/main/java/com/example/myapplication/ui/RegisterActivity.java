package com.example.myapplication.ui;

import android.annotation.SuppressLint;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView eye = (ImageView) findViewById(R.id.seePassReg);
        ImageView eyeRePass = (ImageView) findViewById(R.id.seeRePass);
        EditText pass = (EditText) findViewById(R.id.passReg);
        EditText rePass = (EditText) findViewById(R.id.reEnterPass);
        int typePass = pass.getInputType();
        int typeRePass = rePass.getInputType();
        eye.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //do something when pressed down
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    pass.setSelection(pass.getText().toString().length());
                    pass.setTypeface(typeface);
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                //do something when let go
                pass.setInputType(typePass);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    pass.setSelection(pass.getText().toString().length());
                    pass.setTypeface(typeface);
                }
            }
            return true;
        });
        eyeRePass.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //do something when pressed down
                rePass.setInputType(InputType.TYPE_CLASS_TEXT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    rePass.setSelection(rePass.getText().toString().length());
                    rePass.setTypeface(typeface);
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                //do something when let go
                rePass.setInputType(typeRePass);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    rePass.setSelection(rePass.getText().toString().length());
                    rePass.setTypeface(typeface);
                }
            }
            return true;
        });
    }

    public void registerAccount(View view) {
        String email = ((EditText) findViewById(R.id.mailReg)).getText().toString();
        String pass = ((EditText) findViewById(R.id.passReg)).getText().toString();
        String rePass = ((EditText) findViewById(R.id.reEnterPass)).getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            (Toast.makeText(this, "Email not valid", Toast.LENGTH_LONG)).show();
            Log.i("test", email);
            return;
        }
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(pass);

        if (pass.length() < 8) {
            (Toast.makeText(this, "Use 8 characters or more for your password", Toast.LENGTH_LONG)).show();
            return;
        }
        if (!matcher.matches() || pass.isEmpty()) {
            (Toast.makeText(this, "Password must contain at least 1 capital letter, 1 number and 1 symbol", Toast.LENGTH_LONG)).show();
            return;
        }
        if (!pass.equals(rePass)) {
            (Toast.makeText(this, "Those passwords didn't match", Toast.LENGTH_LONG)).show();
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            LottieAnimationView loading = findViewById(R.id.animationView);
            loading.playAnimation();
            Button regButton = findViewById(R.id.buttonReg);
            regButton.setEnabled(false);
        });
        AsyncTask.execute(() -> {
            try {
                URL url = new URL(getResources().getString(R.string.baseURL) + "/register");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
//                http.connect();
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
                if (http.getResponseCode() == 409) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Email used", Toast.LENGTH_LONG)).show();
                        LottieAnimationView loading = findViewById(R.id.animationView);
                        loading.cancelAnimation();
                        loading.setFrame(0);
                        Button regButton = findViewById(R.id.buttonReg);
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
                    Intent intent = new Intent(this, ConfirmActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("pass", passHash);

                    startActivity(intent);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        LottieAnimationView loading = findViewById(R.id.animationView);
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