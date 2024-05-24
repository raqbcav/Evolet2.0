package com.example.evolet20.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CarrerasFragment extends Fragment {

    private EditText etFiltroFecha, etNewFecha;
    private ImageButton ibFiltroFecha, ibNewFecha;
    private final Calendar calendarFiltro = Calendar.getInstance();
    private final Calendar calendarNew = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);

        spVisible(view);
        rellenarSpinners(view);

        etFiltroFecha = view.findViewById(R.id.etFiltroFecha);
        ibFiltroFecha = view.findViewById(R.id.ibFiltroFecha);
        ibFiltroFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListenerFiltro,
                        calendarFiltro.get(Calendar.YEAR), calendarFiltro.get(Calendar.MONTH),
                        calendarFiltro.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etNewFecha = view.findViewById(R.id.etNewFecha);
        ibNewFecha = view.findViewById(R.id.ibNewFecha);
        ibNewFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListenerNew,
                        calendarNew.get(Calendar.YEAR), calendarNew.get(Calendar.MONTH),
                        calendarNew.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button btnNewCarrera = view.findViewById(R.id.btnNewCarrera);
        btnNewCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    registrarCarrera(view);
                } else {
                    Snackbar.make(view, "No se puede realizar el registro sin conexión a internet.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private final DatePickerDialog.OnDateSetListener dateSetListenerFiltro = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendarFiltro.set(Calendar.YEAR, year);
            calendarFiltro.set(Calendar.MONTH, month);
            calendarFiltro.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelFiltro();
        }
    };

    private void updateLabelFiltro() {
        String myFormat = "dd/MM/yyyy"; // Formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etFiltroFecha.setText(sdf.format(calendarFiltro.getTime()));
    }

    private final DatePickerDialog.OnDateSetListener dateSetListenerNew = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendarNew.set(Calendar.YEAR, year);
            calendarNew.set(Calendar.MONTH, month);
            calendarNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelNew();
        }
    };

    private void updateLabelNew() {
        String myFormat = "dd/MM/yyyy"; // Formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etNewFecha.setText(sdf.format(calendarNew.getTime()));
    }

    private static void spVisible(View view) {
        Spinner spDeportista = view.findViewById(R.id.spDeportista);
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
        }
    }

    private static void rellenarSpinners(View view) {
        Spinner spFiltroTipo = view.findViewById(R.id.spFiltroTipo);
        Spinner spFiltroDistancia = view.findViewById(R.id.spFiltroDistancia);
        Spinner spNewTipo = view.findViewById(R.id.spNewTipo);
        Spinner spNewDistancia = view.findViewById(R.id.spNewDistancia);

        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(view.getContext(), R.array.tipoCarrera, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroTipo.setAdapter(adapterTipo);
        spNewTipo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterDistancia = ArrayAdapter.createFromResource(view.getContext(), R.array.distanciaCarrera, android.R.layout.simple_spinner_item);
        adapterDistancia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroDistancia.setAdapter(adapterDistancia);
        spNewDistancia.setAdapter(adapterDistancia);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void registrarCarrera(View view) {
        // Implementa la lógica para registrar un usuario
    }
}
