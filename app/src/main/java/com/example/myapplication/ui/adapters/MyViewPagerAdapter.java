package com.example.myapplication.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.ui.fragments.PetBreedFragment;
import com.example.myapplication.ui.fragments.ProfileFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PetBreedFragment();
            case 1:
                return new ProfileFragment();
            default:
                return new PetBreedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
