package com.example.evolet20.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.evolet20.Login.LoginActivity;
import com.example.evolet20.MainActivity;
import com.example.evolet20.Model.Carrera;
import com.example.evolet20.Model.DatosActividad;
import com.example.evolet20.Model.Entrenamiento;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HoyFragment extends Fragment {

    private View mView;
    private DatabaseReference mDatabase;
    private Spinner spDeportista;
    private CheckBox cbFuerza, cbRodaje;
    private EditText etTiempo, etDistancia, etVelocidad, etFrecuencia, etFeedback;
    private String idEntrenamiento;
    private Button btnEnviar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_hoy, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        spDeportista = mView.findViewById(R.id.spDeportista);
        cbFuerza = mView.findViewById(R.id.cbFuerza);
        cbRodaje = mView.findViewById(R.id.cbRodaje);
        etTiempo = mView.findViewById(R.id.etTiempo);
        etDistancia = mView.findViewById(R.id.etDistancia);
        etVelocidad = mView.findViewById(R.id.etVelocidad);
        etFrecuencia = mView.findViewById(R.id.etFrecuencia);
        etFeedback = mView.findViewById(R.id.etFeedback);
        btnEnviar = mView.findViewById(R.id.btnEnviar);

        spVisible(mView);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return mView;
    }

    private void enableCampos(boolean enabled){
        spDeportista.setVisibility(enabled ? View.GONE : View.VISIBLE);
        cbFuerza.setEnabled(enabled);
        cbRodaje.setEnabled(enabled);
        etTiempo.setEnabled(enabled);
        etDistancia.setEnabled(enabled);
        etVelocidad.setEnabled(enabled);
        etFrecuencia.setEnabled(enabled);
        etFeedback.setEnabled(enabled);
        btnEnviar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void cargarDatos(Usuario usuario){
        String fecha = Globals.LocalDateToText(Globals.fechaActual);
        mDatabase.child("entrenamiento").orderByChild("fecha").equalTo(fecha).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String idDeportista = snapshot.child("idDeportista").getValue(String.class);
                        if (idDeportista.equalsIgnoreCase(usuario.id)){
                            idEntrenamiento = snapshot.child("id").getValue(String.class);
                        }
                    }
                } else {
                    Snackbar.make(mView, "No se encontró entrenamiento para el dia: " + fecha, Snackbar.LENGTH_LONG).show();
                }
                if (!idEntrenamiento.isEmpty()){
                    cargarActividad(idEntrenamiento);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(mView, "Error en la obtención de datos: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void cargarActividad(String idEntrenamiento){
        mDatabase.child("actividad").orderByChild("idEntrenamiento").equalTo(idEntrenamiento).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        etTiempo.setText(snapshot.child("tiempoTotal").getValue(String.class));
                        etDistancia.setText(snapshot.child("distanciaTotal").getValue(String.class));
                        etVelocidad.setText(snapshot.child("velocidadMedia").getValue(String.class));
                        etFrecuencia.setText(snapshot.child("fc").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(mView, "Error en la obtención de datos: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void spVisible(View view) {
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            enableCampos(true);
            cargarDatos(Globals.usuario);
        } else {
            // Agregar un listener para obtener los datos de los usuarios
            enableCampos(false);
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
                            //cargarDatos(usuarioSeleccionado);
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
}