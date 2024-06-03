package com.example.evolet20.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.evolet20.R;

public class MesTab extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mes_tab, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        // Escuchar los eventos de selección de fecha
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Manejar la fecha seleccionada

            }
        });

        customizeCalendar(calendarView);

        return view;
    }

    private void customizeCalendar(CalendarView calendarView) {
        // Obtener la vista raíz del CalendarView
        ViewGroup root = (ViewGroup) calendarView.getChildAt(0);

        // Iterar sobre todas las celdas del calendario
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            if (child instanceof TextView) {
                TextView dayTextView = (TextView) child;
                int dayOfMonth = Integer.parseInt(dayTextView.getText().toString());

                // Personalizar el color de fondo de ciertos días (por ejemplo, los días 1, 5 y 10)
                if (dayOfMonth == 1 || dayOfMonth == 5 || dayOfMonth == 10) {
                    dayTextView.setBackgroundColor(Color.RED);
                }
            }
        }
    }
}