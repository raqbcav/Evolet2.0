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
        switch (position){
            case 0: return new HoyTab();
            case 1: return new SemanaTab();
            case 2: return new MesTab();
            default: return new HoyTab();
        }
    }

    @Override
    public int getItemCount(){
        return 3;
    }
}
