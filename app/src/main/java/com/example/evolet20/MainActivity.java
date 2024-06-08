package com.example.evolet20;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.evolet20.Fragments.CarrerasFragment;
import com.example.evolet20.Fragments.HoyTab;
import com.example.evolet20.Fragments.MesTab;
import com.example.evolet20.Fragments.SemanaTab;
import com.example.evolet20.Fragments.TuFragment;
import com.example.evolet20.Static.Globals;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            replaceFragment(new HoyTab());
        }

        // Obtener la fecha actual yla semana del año
        Calendar calendar = Calendar.getInstance();
        Globals.fechaActual = Globals.dateToLocalDate(calendar.getTime());
        Globals.semanaActual = calendar.get(Calendar.WEEK_OF_YEAR);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.hoy:
                    replaceFragment(new HoyTab());
                    break;
                case R.id.semana:
                    replaceFragment(new SemanaTab());
                    break;
                case R.id.mes:
                    replaceFragment(new MesTab());
                    break;
                case R.id.carreras:
                    replaceFragment(new CarrerasFragment());
                    break;
                case R.id.tu:
                    replaceFragment(new TuFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}