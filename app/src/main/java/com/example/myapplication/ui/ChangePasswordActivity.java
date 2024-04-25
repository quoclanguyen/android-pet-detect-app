package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "petreco_pref";
    private static final String KEY_EMAIL = "email";
    private String email;
    private EditText reenterPass;
    private EditText newPass;
    private EditText currentPass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, PetsListActivity.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        email = sharedPreferences.getString(KEY_EMAIL, "");
        Log.i("email", email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView eye_current = findViewById(R.id.see_current_password);
        ImageView eye_new = findViewById(R.id.see_new_password);
        ImageView eye_reenter_new = findViewById(R.id.see_reenter_new_password);

        List<ImageView> eyeList = new ArrayList<>();
        Collections.addAll(eyeList, eye_current, eye_new, eye_reenter_new);

        currentPass = findViewById(R.id.current_password);
        newPass = findViewById(R.id.new_password);
        reenterPass = findViewById(R.id.reenter_new_password);

        List<EditText> editTextList = new ArrayList<>();
        Collections.addAll(editTextList, currentPass, newPass, reenterPass);

        for (int i = 0; i < eyeList.size(); i++) {

            EditText pass = editTextList.get(i);
            ImageView eye = eyeList.get(i);
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
    }

    public void changePassword(View view) {
        String current_pass = currentPass.getText().toString();
        String new_pass = newPass.getText().toString();
        String reenter_new_pass = reenterPass.getText().toString();

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(new_pass);

        if (new_pass.length() < 8) {
            (Toast.makeText(this, "Use 8 characters or more for your password", Toast.LENGTH_LONG)).show();
            return;
        }
        if (!matcher.matches() || new_pass.isEmpty()) {
            (Toast.makeText(this, "Password must contain at least 1 capital letter, 1 number and 1 symbol", Toast.LENGTH_LONG)).show();
            return;
        }
        if (!new_pass.equals(reenter_new_pass)) {
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
                URL url = new URL(getResources().getString(R.string.base_url) + "/changePassword?isReset=0");
                URLConnection con = (URLConnection) url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");

                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);
                StringBuilder jsonInputString = new StringBuilder("{\"email\": \"" + email + "\", ");


                String current_pass_hash = Hashing.sha256().hashString(current_pass, StandardCharsets.UTF_8).toString();
                String new_pass_hash = Hashing.sha256().hashString(new_pass, StandardCharsets.UTF_8).toString();
                jsonInputString.append("\"currentPassword\": \"").append(current_pass_hash).append("\", ");
                jsonInputString.append("\"newPassword\": \"").append(new_pass_hash).append("\"}");
                String inputString = jsonInputString.toString();
                Log.i("test", inputString);
                try(OutputStream os = http.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                if (http.getResponseCode() == 401) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG)).show();
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
                    startActivity(new Intent(this, PetsListActivity.class));
                    new Handler(Looper.getMainLooper()).post(() -> {
                        (Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_LONG)).show();
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