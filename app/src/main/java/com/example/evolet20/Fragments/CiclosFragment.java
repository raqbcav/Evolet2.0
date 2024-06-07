package com.example.evolet20.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.example.evolet20.ViewPagerAdapater;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;

public class CiclosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ciclos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Globals.tabLayout = view.findViewById(R.id.tabLayout);
        Globals.viewPager2 = view.findViewById(R.id.viewPager);
        Globals.viewPagerAdapater = new ViewPagerAdapater(this);
        Globals.viewPager2.setAdapter(Globals.viewPagerAdapater);
        Globals.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Globals.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Globals.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Globals.tabLayout.getTabAt(position).select();
            }
        });
    }
}