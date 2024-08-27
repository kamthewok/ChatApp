package com.example.secchatapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    int tabcounter;


    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        tabcounter = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new ChatFragment();

            case 1:
                return new AboutFragment();

            case 2:
                return new CallFragment();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabcounter;
    }
}
