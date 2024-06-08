package com.example.evolet20.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.evolet20.Model.Carrera;
import com.example.evolet20.Model.Entrenamiento;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MesFragment extends Fragment {

    private View mView;
    private DatabaseReference mDatabase;
    private CalendarView calendarView;
    private EditText etTotalDias, etDistanciaE, etDistanciaC, etTotalCarreras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_mes, container, false);

        calendarView = mView.findViewById(R.id.calendarView);
        etTotalDias = mView.findViewById(R.id.etTotalDias);
        etDistanciaE = mView.findViewById(R.id.etDistanciaE);
        etDistanciaC = mView.findViewById(R.id.etDistanciaC);
        etTotalCarreras = mView.findViewById(R.id.etTotalCarreras);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        spVisible(mView);

        // Escuchar los eventos de selección de fecha
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Manejar la fecha seleccionada

            }
        });

        return mView;
    }

    private void spVisible(View view) {
        Spinner spDeportista = view.findViewById(R.id.spDeportista);
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
            cargarDatos(Globals.usuario);
        } else {
            // Agregar un listener para obtener los datos de los usuarios
            mDatabase.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Usuario> usuarios = new ArrayList<>();

                    // Iterar sobre los usuarios y agregarlos a la lista
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        String idUsuario = usuarioSnapshot.child("id").getValue(String.class);
                        String nombreUsuario = usuarioSnapshot.child("nombre").getValue(String.class);
                        String perfilusuario = usuarioSnapshot.child("perfil").getValue(String.class);
                        if (nombreUsuario != null && perfilusuario.equalsIgnoreCase("deportista")) {
                            Usuario usuario = new Usuario();
                            usuario.id = idUsuario;
                            usuario.nombre = nombreUsuario;
                            usuarios.add(usuario);
                        }
                    }

                    // Crear un ArrayAdapter para el Spinner
                    ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establecer el adaptador en el Spinner
                    spDeportista.setAdapter(adapter);
                    // Definir un listener para manejar la selección del Spinner si es necesario
                    spDeportista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Usuario usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                            cargarDatos(usuarioSeleccionado);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Método requerido pero no necesitamos implementarlo aquí
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error en caso de que falle la lectura de datos
                    Snackbar.make(view, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void cargarDatos(Usuario usuario) {
        List<Carrera> carreras = new ArrayList<>();
        List<Entrenamiento> entrenamientos = new ArrayList<>();

        mDatabase.child("carrera").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Carrera carrera = snapshot.getValue(Carrera.class);
                    carreras.add(carrera);
                }

                int totalCarreras = 0;
                double distanciaCarreras = 0;

                for (Carrera carrera : carreras) {
                    LocalDate fecha = Globals.textToLocalDate(carrera.fecha);
                    if (fecha.getMonth() == LocalDate.now().getMonth() && carrera.idUsuario.equalsIgnoreCase(usuario.id)){
                        totalCarreras ++;
                        distanciaCarreras += Double.parseDouble(carrera.distancia.replace("K", ""));
                    }
                }

                etTotalCarreras.setText(String.valueOf(totalCarreras));
                etDistanciaC.setText(String.valueOf(distanciaCarreras));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(mView, "Error al leer los datos de Firebase: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        mDatabase.child("entrenamiento").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Entrenamiento entrenamiento = snapshot.getValue(Entrenamiento.class);
                    entrenamientos.add(entrenamiento);
                }

                int totalDias = 0;
                double distanciaEntrenamientos = 0;

                for (Entrenamiento entrenamiento : entrenamientos) {
                    LocalDate fecha = Globals.textToLocalDate(entrenamiento.fecha);
                    if (fecha.getMonth() == LocalDate.now().getMonth() && entrenamiento.idDeportista.equalsIgnoreCase(usuario.id)
                    && !entrenamiento.sesion.equalsIgnoreCase("") && entrenamiento.km != 0){
                        totalDias ++;
                        distanciaEntrenamientos += entrenamiento.km;
                    }
                }

                etTotalDias.setText(String.valueOf(totalDias));
                etDistanciaE.setText(String.valueOf(distanciaEntrenamientos));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(mView, "Error al leer los datos de Firebase: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}