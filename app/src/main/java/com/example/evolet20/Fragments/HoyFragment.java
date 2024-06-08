package com.example.evolet20.Fragments;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.evolet20.Login.RegisterActivity;
import com.example.evolet20.MainActivity;
import com.example.evolet20.Model.Carrera;
import com.example.evolet20.Model.DatosActividad;
import com.example.evolet20.Model.Entrenamiento;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Button btnEnviar, btnLunes, btnMartes, btnMiercoles, btnJueves, btnViernes, btnSabado, btnDomingo;
    private DatosActividad actividad;
    private Usuario usuarioSeleccionado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_hoy, container, false);

        try {
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
            btnLunes = mView.findViewById(R.id.btnLunes);
            btnMartes = mView.findViewById(R.id.btnMartes);
            btnMiercoles = mView.findViewById(R.id.btnMiercoles);
            btnJueves = mView.findViewById(R.id.btnJueves);
            btnViernes = mView.findViewById(R.id.btnViernes);
            btnSabado = mView.findViewById(R.id.btnSabado);
            btnDomingo = mView.findViewById(R.id.btnDomingo);

            spVisible(mView);

            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addActividad(view);
                }
            });
        } catch (Exception e) {
            Snackbar.make(mView, "Ha ocurrido un error inesperado: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

        return mView;
    }

    private void addActividad(View view) {
        String idActividad = mDatabase.child("actividad").push().getKey();
        actividad.idEntrenamiento = idEntrenamiento;
        actividad.tiempoTotal = etTiempo.getText().toString();
        actividad.distanciaTotal = etDistancia.getText().toString();
        actividad.velocidadMedia = etVelocidad.getText().toString();
        actividad.fc = etFrecuencia.getText().toString();
        actividad.fuerza = cbFuerza.isChecked() ? "true" : "false";
        actividad.rodaje = cbRodaje.isChecked() ? "true" : "false";
        actividad.comentario = etFeedback.getText().toString();

        mDatabase.child("actividad").child(idActividad).setValue(actividad)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Datos de la actividad registrados con éxito.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Snackbar.make(view, "Error al registrar los datos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void enableCampos(boolean enabled) {
        spDeportista.setVisibility(enabled ? View.GONE : View.VISIBLE);
        cbFuerza.setEnabled(enabled);
        cbRodaje.setEnabled(enabled);
        etTiempo.setEnabled(enabled);
        etDistancia.setEnabled(enabled);
        etVelocidad.setEnabled(enabled);
        etFrecuencia.setEnabled(enabled);
        btnEnviar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void cargarDatos(Usuario usuario) {
        String fecha = Globals.LocalDateToText(Globals.fechaActual);
        idEntrenamiento = null;
        mDatabase.child("entrenamiento").orderByChild("fecha").equalTo(fecha).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String idDeportista = snapshot.child("idDeportista").getValue(String.class);
                        String fechaDB = snapshot.child("fecha").getValue(String.class);
                        if (idDeportista != null && fechaDB != null &&
                                idDeportista.equalsIgnoreCase(usuario.id) && fechaDB.equalsIgnoreCase(fecha)) {
                            idEntrenamiento = snapshot.child("id").getValue(String.class);
                            btnEnviar.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                } else {
                    Snackbar.make(mView, "No se encontró entrenamiento para el dia: " + fecha, Snackbar.LENGTH_LONG).show();
                    btnEnviar.setVisibility(View.GONE);
                }
                if (!idEntrenamiento.isEmpty()) {
                    cargarActividad(idEntrenamiento);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(mView, "Error en la obtención de datos: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void cargarActividad(String idEntrenamiento) {
        actividad = new DatosActividad();
        mDatabase.child("actividad").orderByChild("idEntrenamiento").equalTo(idEntrenamiento).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        actividad.idEntrenamiento = idEntrenamiento;
                        actividad.tiempoTotal = snapshot.child("tiempoTotal").getValue(String.class);
                        actividad.distanciaTotal = snapshot.child("distanciaTotal").getValue(String.class);
                        actividad.velocidadMedia = snapshot.child("velocidadMedia").getValue(String.class);
                        actividad.fc = snapshot.child("fc").getValue(String.class);
                        actividad.fuerza = snapshot.child("fuerza").getValue(String.class);
                        actividad.rodaje = snapshot.child("rodaje").getValue(String.class);
                        actividad.comentario = snapshot.child("comentario").getValue(String.class);

                        etTiempo.setText(actividad.tiempoTotal);
                        etDistancia.setText(actividad.distanciaTotal);
                        etVelocidad.setText(actividad.velocidadMedia);
                        etFrecuencia.setText(actividad.fc);
                        cbFuerza.setChecked(actividad.fuerza.equalsIgnoreCase("true"));
                        cbRodaje.setChecked(actividad.rodaje.equalsIgnoreCase("true"));
                        etFeedback.setText(actividad.comentario);
                    }
                } else {
                    cbRodaje.setChecked(false);
                    cbFuerza.setChecked(false);
                    etTiempo.setText("");
                    etDistancia.setText("");
                    etVelocidad.setText("");
                    etFeedback.setText("");
                    etFrecuencia.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(mView, "Error en la obtención de datos: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    interface OnCheckCompleteListener {
        void onCheckComplete(boolean exists);
    }

    private void checkIfEntrenamientoExists(String fecha, String idDeportista, OnCheckCompleteListener listener) {
        Query query = mDatabase.child("entrenamiento").orderByChild("fecha").equalTo(fecha);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Entrenamiento entrenamiento = snapshot.getValue(Entrenamiento.class);
                        if (entrenamiento != null && entrenamiento.idDeportista.equals(idDeportista) &&
                                !entrenamiento.sesion.equalsIgnoreCase("") && entrenamiento.km != 0) {
                            exists = true;
                            break;
                        }
                    }
                }
                listener.onCheckComplete(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mView, "Error al comprobar el entrenamiento: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void changeColorBtn() {
        List<LocalDate> diasSemana = Globals.getSemanaCompleta(Globals.semanaActual);
        String idDeportista = usuarioSeleccionado != null ? usuarioSeleccionado.id : Globals.usuario.id;
        for (LocalDate dia : diasSemana) {
            checkIfEntrenamientoExists(Globals.LocalDateToText(dia), idDeportista, new OnCheckCompleteListener() {
                @Override
                public void onCheckComplete(boolean exists) {
                    switch (dia.getDayOfWeek()) {
                        case MONDAY:
                            btnLunes.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case TUESDAY:
                            btnMartes.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case WEDNESDAY:
                            btnMiercoles.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case THURSDAY:
                            btnJueves.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case FRIDAY:
                            btnViernes.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case SATURDAY:
                            btnSabado.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                        case SUNDAY:
                            btnDomingo.setBackgroundColor(exists ? Color.GREEN : getResources().getColor(R.color.hint));
                            break;
                    }
                }
            });
        }
    }

    private void spVisible(View view) {
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            enableCampos(true);
            cargarDatos(Globals.usuario);
            changeColorBtn();
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
                            usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                            cargarDatos(usuarioSeleccionado);
                            changeColorBtn();
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