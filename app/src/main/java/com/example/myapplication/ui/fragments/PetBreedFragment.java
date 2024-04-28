package com.example.myapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.ui.CameraViewActivity;
import com.example.myapplication.ui.adapters.BreedListAdapter;

import io.flutter.embedding.android.FlutterActivity;

public class PetBreedFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        ListView breedsList = requireActivity().findViewById(R.id.breed_list);
        breedsList.setAdapter(new BreedListAdapter(getContext(), R.layout.breed));

        ImageView openCamera = requireActivity().findViewById(R.id.open_camera);
        openCamera.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), CameraViewActivity.class);
//            startActivity(intent);
                    startActivity(
                FlutterActivity
                        .withNewEngine()
                        .initialRoute("/camera_screen")
                        .build(requireActivity())
        );
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_breed, container, false);
    }
}