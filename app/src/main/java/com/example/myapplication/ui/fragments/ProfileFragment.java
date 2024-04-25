package com.example.myapplication.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.CameraViewActivity;
import com.example.myapplication.ui.ChangePasswordActivity;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.adapters.BreedListAdapter;

public class ProfileFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "petreco_pref";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    @Override
    public void onStart() {
        super.onStart();
        TextView profileName = requireActivity().findViewById(R.id.profile_avatar);
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        profileName.setText(String.valueOf(email.charAt(0)).toUpperCase());

        TextView emailProfile = requireActivity().findViewById(R.id.email_profile);
        emailProfile.setText(email);

        Button logoutButton = requireActivity().findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
        });

        TextView changePassword = requireActivity().findViewById(R.id.change_password);
        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        TextView aboutUs = requireActivity().findViewById(R.id.about_us);
        aboutUs.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String members = getString(R.string.member_Lan) + "\n"
                    + getString(R.string.member_Dang) + "\n"
                    + getString(R.string.member_Thong);
            String title = getString(R.string.about_us) + " - Group 4";
            builder.setMessage(members)
                    .setTitle(title);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}