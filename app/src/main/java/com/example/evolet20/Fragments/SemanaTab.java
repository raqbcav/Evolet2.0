package com.example.evolet20.Fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.evolet20.Login.LoginActivity;
import com.example.evolet20.Login.RegisterActivity;
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
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class SemanaTab extends Fragment {

    private View mView;
    private DatabaseReference mDatabase;
    private EditText etFiltroFecha;
    private EditText etFiltroSemana;
    private Spinner spDeportista;
    private Button btnGuardar;
    private ImageButton ibFiltroFecha;

    private int semanaSelect;
    private String idDeportistaSel;

    private boolean esNuevo;

    private final Calendar calendarFiltro = Calendar.getInstance();
    private EditText etLunes, etMartes, etMiercoles, etJueves, etViernes, etSabado, etDomingo;
    private EditText etLunesKM, etMartesKM, etMiercolesKM, etJuevesKM, etViernesKM, etSabadoKM, etDomingoKM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_semana_tab, container, false);

        spDeportista = mView.findViewById(R.id.spDeportista);
        btnGuardar = mView.findViewById(R.id.btnGuardar);
        ibFiltroFecha = mView.findViewById(R.id.ibFiltroFecha);
        etLunes = mView.findViewById(R.id.etLunes);
        etMartes = mView.findViewById(R.id.etMartes);
        etMiercoles = mView.findViewById(R.id.etMiercoles);
        etJueves = mView.findViewById(R.id.etJueves);
        etViernes = mView.findViewById(R.id.etViernes);
        etSabado = mView.findViewById(R.id.etSabado);
        etDomingo = mView.findViewById(R.id.etDomingo);
        etLunesKM = mView.findViewById(R.id.etLunesKM);
        etMartesKM = mView.findViewById(R.id.etMartesKM);
        etMiercolesKM = mView.findViewById(R.id.etMiercolesKM);
        etJuevesKM = mView.findViewById(R.id.etJuevesKM);
        etViernesKM = mView.findViewById(R.id.etViernesKM);
        etSabadoKM = mView.findViewById(R.id.etSabadoKM);
        etDomingoKM = mView.findViewById(R.id.etDomingoKM);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        spVisible(mView);

        ibFiltroFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListenerFiltro,
                        calendarFiltro.get(Calendar.YEAR), calendarFiltro.get(Calendar.MONTH),
                        calendarFiltro.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int semana = Integer.parseInt(etFiltroSemana.getText().toString());
                List<Entrenamiento> entrenamientos = new ArrayList<>();

                List<LocalDate> diasSemana = getSemanaCompleta(semana);
                for (LocalDate dia : diasSemana) {
                    Entrenamiento entrenamiento = new Entrenamiento();
                    entrenamiento.id = UUID.randomUUID().toString();
                    entrenamiento.nSemana = semana;
                    if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
                        entrenamiento.idDeportista = Globals.usuario.id;
                    } else {
                        entrenamiento.idDeportista = idDeportistaSel;
                        entrenamiento.idEntrenador = Globals.usuario.id;
                    }
                    switch (dia.getDayOfWeek()) {
                        case MONDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etLunes.getText().toString();
                            entrenamiento.km = Double.parseDouble(etLunesKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case TUESDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etMartes.getText().toString();
                            entrenamiento.km = Double.parseDouble(etMartesKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case WEDNESDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etMiercoles.getText().toString();
                            entrenamiento.km = Double.parseDouble(etMiercolesKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case THURSDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etJueves.getText().toString();
                            entrenamiento.km = Double.parseDouble(etJuevesKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case FRIDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etViernes.getText().toString();
                            entrenamiento.km = Double.parseDouble(etViernesKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case SATURDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etSabado.getText().toString();
                            entrenamiento.km = Double.parseDouble(etSabadoKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                        case SUNDAY:
                            entrenamiento.fecha = Globals.LocalDateToText(dia);
                            entrenamiento.sesion = etDomingo.getText().toString();
                            entrenamiento.km = Double.parseDouble(etDomingoKM.getText().toString());
                            entrenamientos.add(entrenamiento);
                            break;
                    }
                }

                for (Entrenamiento entrenamiento: entrenamientos) {
                    if (esNuevo){
                        // Generar una nueva clave única para el entrenamiento
                        String id = mDatabase.child("entrenamiento").push().getKey();

                        mDatabase.child("entrenamiento").child(id).setValue(entrenamiento)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(view, "Entrenamiento registrado con éxito.", Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Snackbar.make(view, "Error al registrar el entrenamiento: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }else{
                        //TODO: modificar entrenamiento
                    }
                }
            }
        });

        return mView;
    }

    private List<LocalDate> getSemanaCompleta(int semana) {
        int anio = LocalDate.now().getYear();

        // Obtener el primer día de la semana (lunes) dada la semana del año y el año
        LocalDate primerDiaSemana = LocalDate.of(anio, 1, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), semana)
                .with(DayOfWeek.MONDAY);

        // Crear una lista para almacenar los 7 días de la semana
        List<LocalDate> diasSemana = new ArrayList<>();

        // Agregar el primer día de la semana y los siguientes 6 días a la lista
        for (int i = 0; i < 7; i++) {
            diasSemana.add(primerDiaSemana.plusDays(i));
        }

        // Imprimir la lista de días de la semana
        return diasSemana;
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
        semanaSelect = calendarFiltro.get(Calendar.WEEK_OF_YEAR);
        rellenarDatos(mView, Globals.usuario, semanaSelect);
    }

    private void spVisible(View view) {
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            selectDiaSemana(view);
            semanaSelect = Globals.semanaActual;
            rellenarDatos(view, Globals.usuario, semanaSelect);
        } else {
            // Agregar un listener para obtener los datos de los usuarios
            mDatabase.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Usuario> usuarios = new ArrayList<>();

                    // Iterar sobre los usuarios y agregarlos a la lista
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        idDeportistaSel = usuarioSnapshot.child("id").getValue(String.class);
                        String nombreUsuario = usuarioSnapshot.child("nombre").getValue(String.class);
                        if (nombreUsuario != null) {
                            Usuario usuario = new Usuario();
                            usuario.id = idDeportistaSel;
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
                            rellenarDatos(view, usuarioSeleccionado, semanaSelect);
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

    private void selectDiaSemana(View view) {
        etFiltroFecha = view.findViewById(R.id.etFiltroFecha);
        etFiltroSemana = view.findViewById(R.id.etFiltroSemana);

        // Mostrar la fecha actual en el EditText
        etFiltroFecha.setText(Globals.LocalDateToText(Globals.fechaActual));
        etFiltroSemana.setText("Semana " + Globals.semanaActual);
    }

    private void rellenarDatos(View view, Usuario usuario, int semana) {
        mDatabase.child("entrenamiento").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterar sobre los entrenamientos y agregarlos a la lista
                esNuevo = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    esNuevo = false;
                    Entrenamiento entrenamiento = snapshot.getValue(Entrenamiento.class);

                    LocalDate fecha = Globals.textToLocalDate(entrenamiento.fecha);
                    if (fecha.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == semana) {
                        DayOfWeek dia = fecha.getDayOfWeek();
                        switch (dia) {
                            case MONDAY:
                                etLunes.setText(entrenamiento.sesion);
                                etLunesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case TUESDAY:
                                etMartes.setText(entrenamiento.sesion);
                                etMartesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case WEDNESDAY:
                                etMiercoles.setText(entrenamiento.sesion);
                                etMiercolesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case THURSDAY:
                                etJueves.setText(entrenamiento.sesion);
                                etJuevesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case FRIDAY:
                                etViernes.setText(entrenamiento.sesion);
                                etViernesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case SATURDAY:
                                etSabado.setText(entrenamiento.sesion);
                                etSabadoKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case SUNDAY:
                                etDomingo.setText(entrenamiento.sesion);
                                etDomingoKM.setText(String.valueOf(entrenamiento.km));
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(view, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                esNuevo = true;
            }
        });
    }
}