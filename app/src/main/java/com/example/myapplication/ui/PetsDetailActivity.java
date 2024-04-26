package com.example.myapplication.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PetsDetailActivity extends AppCompatActivity {

    private int indexRes;
    private int index;
    private ImageView breedImage;
    private String breedName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pets_detail);
        Bundle extras = getIntent().getExtras();
        index = Objects.requireNonNull(extras).getInt("index");
        indexRes = extras.getInt("indexImage");
        breedImage = findViewById(R.id.img_breed_detail);
        breedImage.setImageResource(indexRes);
        breedName = extras.getString("breedName");

        InputStream is = null;
        int size = 0;
        try {
            is = getAssets().open("breedDetails.json");
            size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject element = jsonArray.getJSONObject(index);
            int avgWeight = element.getInt("avg_weight");
            JSONArray colorsArray = element.getJSONArray("colors");
            int lifeSpan = element.getInt("life_span");
            String characteristic = element.getString("characteristic");

            StringBuilder colorsStringBuilder = new StringBuilder();
            for (int i = 0; i < colorsArray.length(); i++) {
                if (i > 0) {
                    colorsStringBuilder.append(", ");
                }
                colorsStringBuilder.append(colorsArray.getString(i));
            }
            String colors = colorsStringBuilder.toString();
            TextView textChracter = findViewById(R.id.character);
            TextView textAvgWeight = findViewById(R.id.avg_weight);
            TextView textLifeSpan = findViewById(R.id.life_span);
            TextView textColorVariant = findViewById(R.id.colors_variant);
            TextView textBreedName = findViewById(R.id.breed_name_detail);

            textBreedName.setText(breedName);
            textChracter.setText(String.format("- Characteristics: %s", characteristic));
            textAvgWeight.setText(String.format("- Average weight: %s kg", avgWeight));
            textLifeSpan.setText(String.format("- Life span: %s years", lifeSpan));
            textColorVariant.setText(String.format("- Color variants: %s", colors));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Read the JSON file content

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}