package com.example.evolet20.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.evolet20.Model.Carrera;
import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CarrerasFragment extends Fragment {

    private Button btnNewCarrera;
    private Spinner spFiltroTipo, spFiltroDistancia, spNewTipo, spNewDistancia;
    private EditText etFiltroFecha, etNewFecha, etNewLugar;
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

        btnNewCarrera = view.findViewById(R.id.btnNewCarrera);
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

    private void spVisible(View view) {
        Spinner spDeportista = view.findViewById(R.id.spDeportista);
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
        }
    }

    private void rellenarSpinners(View view) {
        spFiltroTipo = view.findViewById(R.id.spFiltroTipo);
        spFiltroDistancia = view.findViewById(R.id.spFiltroDistancia);
        spNewTipo = view.findViewById(R.id.spNewTipo);
        spNewDistancia = view.findViewById(R.id.spNewDistancia);

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
        // Obtener los datos de los campos de entrada
        String tipoCarrera = spNewTipo.getSelectedItem().toString();
        String distanciaCarrera = spNewDistancia.getSelectedItem().toString();
        String fechaCarrera = etNewFecha.getText().toString();
        String lugarCarrera = etNewLugar.getText().toString();

        // Obtener una referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Crear un nuevo nodo "carreras" bajo la raíz de la base de datos
        DatabaseReference carrerasRef = databaseRef.child("carreras");

        // Generar una nueva clave única para la carrera
        String carreraId = carrerasRef.push().getKey();

        // Crear un objeto Map para los datos de la carrera
        Carrera carrera = new Carrera(tipoCarrera, distanciaCarrera, lugarCarrera, fechaCarrera, Globals.usuario.id);

        // Guardar los datos de la carrera en la base de datos
        carrerasRef.child(carreraId).setValue(carrera)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al registrar la carrera
                        Snackbar.make(view, "Carrera registrada con éxito", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al registrar la carrera
                        Snackbar.make(view, "Error al registrar la carrera: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

}
