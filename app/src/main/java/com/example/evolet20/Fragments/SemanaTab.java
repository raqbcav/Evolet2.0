package com.example.evolet20.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SemanaTab extends Fragment {

    private View mView;
    private DatabaseReference mDatabase;
    private EditText etFiltroFecha;
    private EditText etFiltroSemana;

    private Spinner spDeportista;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_semana_tab, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        spVisible(mView);

        return mView;
    }

    private void spVisible(View view) {
        spDeportista = view.findViewById(R.id.spDeportista);
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
            selectDiaSemana(view);
            rellenarDatos(view, Globals.usuario);
        } else {
            // Agregar un listener para obtener los datos de los usuarios
            mDatabase.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Usuario> usuarios = new ArrayList<>();

                    // Iterar sobre los usuarios y agregarlos a la lista
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        String idUsuario = usuarioSnapshot.getKey();
                        String nombreUsuario = usuarioSnapshot.child("nombreUsuario").getValue(String.class);
                        if (nombreUsuario != null) {
                            Usuario usuario = new Usuario();
                            usuario.id = idUsuario;
                            usuario.nombre = nombreUsuario;
                            usuarios.add(usuario);
                        }
                    }

                    // Crear un ArrayAdapter para el Spinner
                    ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establecer el adaptador en el Spinner
                    spDeportista.setAdapter(adapter);
                    // Definir un listener para manejar la selección del Spinner si es necesario
                    spDeportista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Usuario usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                            selectDiaSemana(view);
                            rellenarDatos(view, usuarioSeleccionado);
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

    private void selectDiaSemana(View view){
        etFiltroFecha = view.findViewById(R.id.etFiltroFecha);
        etFiltroSemana = view.findViewById(R.id.etFiltroSemana);

        // Mostrar la fecha actual en el EditText
        etFiltroFecha.setText(Globals.LocalDateToText(Globals.fechaActual));
        etFiltroSemana.setText("Semana " + Globals.semanaActual);
    }

    private void rellenarDatos(View view, Usuario usuario){
        mDatabase.child("entrenamiento").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entrenamiento> entrenamiento = new ArrayList<>();

                // Iterar sobre los entrenamientos y agregarlos a la lista
                for (DataSnapshot entrenamientoSnapshot : dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(view, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}