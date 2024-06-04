package com.example.evolet20;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.evolet20.Fragments.CiclosFragment;
import com.example.evolet20.Fragments.HoyTab;
import com.example.evolet20.Fragments.MesTab;
import com.example.evolet20.Fragments.SemanaTab;

public class ViewPagerAdapater extends FragmentStateAdapter {

    public ViewPagerAdapater(@NonNull CiclosFragment fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position){
            case 0: fragment = new HoyTab();
            case 1: fragment = new SemanaTab();
            case 2: fragment = new MesTab();
            default: fragment = new HoyTab();
        }
        return fragment;
    }

    @Override
    public int getItemCount(){
        return 3;
    }
}
