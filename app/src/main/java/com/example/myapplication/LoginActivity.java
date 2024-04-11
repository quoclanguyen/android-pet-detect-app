package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
}