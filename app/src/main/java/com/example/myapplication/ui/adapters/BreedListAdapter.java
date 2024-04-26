package com.example.myapplication.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.CameraViewActivity;
import com.example.myapplication.ui.PetsDetailActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BreedListAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mLines = new ArrayList<>();
    int layout;
    public void getBreedNames() {
        try {
            InputStream is = mContext.getAssets().open("labels.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public BreedListAdapter(Context context, int layout) {
        mContext = context;
        this.layout = layout;
        getBreedNames();
    }
    @Override
    public int getCount() {
        return mLines.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(layout, viewGroup, false);

        ImageView imageView = rowView.findViewById(R.id.img_breed);
        String breedUri = "@drawable/breed" + (index + 1);
        int imageRes = mContext.getResources().getIdentifier(breedUri, null, mContext.getPackageName());
        TextView breedName = rowView.findViewById(R.id.breed_name);

        imageView.setImageResource(imageRes);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PetsDetailActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("breedName", mLines.get(index));
            intent.putExtra("indexImage", imageRes);
            mContext.startActivity(intent);
        });
        breedName.setText(mLines.get(index));
        breedName.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PetsDetailActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("breedName", mLines.get(index));
            intent.putExtra("indexImage", imageRes);
            mContext.startActivity(intent);
        });
        return rowView;
    }
}
