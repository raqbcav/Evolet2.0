package com.example.evolet20;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.evolet20.Fragments.CarrerasFragment;
import com.example.evolet20.Fragments.HoyFragment;
import com.example.evolet20.Fragments.MesFragment;
import com.example.evolet20.Fragments.SemanaFragment;
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
            replaceFragment(new HoyFragment());
        }

        setFechas();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            openFragment(item);
            return true;
        });
    }

    private static void setFechas() {
        // Obtener la fecha actual yla semana del año
        Calendar calendar = Calendar.getInstance();
        Globals.fechaActual = Globals.dateToLocalDate(calendar.getTime());
        Globals.semanaActual = calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private void openFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hoy:
                replaceFragment(new HoyFragment());
                break;
            case R.id.semana:
                replaceFragment(new SemanaFragment());
                break;
            case R.id.mes:
                replaceFragment(new MesFragment());
                break;
            case R.id.carreras:
                replaceFragment(new CarrerasFragment());
                break;
            case R.id.tu:
                replaceFragment(new TuFragment());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}