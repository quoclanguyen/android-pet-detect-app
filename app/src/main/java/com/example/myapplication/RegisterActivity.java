package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

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
                    rePass.setTypeface(typeface);
                    rePass.setSelection(pass.getText().toString().length());
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                //do something when let go
                rePass.setInputType(typeRePass);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Typeface typeface = getResources().getFont(R.font.kameron_reg);
                    rePass.setTypeface(typeface);
                    rePass.setSelection(pass.getText().toString().length());
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



    }
}