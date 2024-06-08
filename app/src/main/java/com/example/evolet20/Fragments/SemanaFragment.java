package com.example.evolet20.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SemanaFragment extends Fragment {

    private static View mView;
    private static DatabaseReference mDatabase;
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
    private TextView idLunes, idMartes, idMiercoles, idJueves, idViernes, idSabado, idDomingo;
    private EditText etLunesKM, etMartesKM, etMiercolesKM, etJuevesKM, etViernesKM, etSabadoKM, etDomingoKM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_semana, container, false);

        spDeportista = mView.findViewById(R.id.spDeportista);
        btnGuardar = mView.findViewById(R.id.btnGuardar);
        ibFiltroFecha = mView.findViewById(R.id.ibFiltroFecha);

        idLunes = mView.findViewById(R.id.idLunes);
        idMartes = mView.findViewById(R.id.idMartes);
        idMiercoles = mView.findViewById(R.id.idMiercoles);
        idJueves = mView.findViewById(R.id.idJueves);
        idViernes = mView.findViewById(R.id.idViernes);
        idSabado = mView.findViewById(R.id.idSabado);
        idDomingo = mView.findViewById(R.id.idDomingo);

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
        spVisible();

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
                int semana = Integer.parseInt(etFiltroSemana.getText().toString().replace("Semana ", ""));
                List<LocalDate> diasSemana = getSemanaCompleta(semana);
                List<Entrenamiento> entrenamientos = new ArrayList<>();

                String lunes = etLunes.getText().toString();
                String lunesKM = etLunesKM.getText().toString();
                String martes = etMartes.getText().toString();
                String martesKM = etMartesKM.getText().toString();
                String miercoles = etMiercoles.getText().toString();
                String miercolesKM = etMiercolesKM.getText().toString();
                String jueves = etJueves.getText().toString();
                String juevesKM = etJuevesKM.getText().toString();
                String viernes = etViernes.getText().toString();
                String viernesKM = etViernesKM.getText().toString();
                String sabado = etSabado.getText().toString();
                String sabadoKM = etSabadoKM.getText().toString();
                String domingo = etDomingo.getText().toString();
                String domingoKM = etDomingoKM.getText().toString();

                if (lunes.isEmpty() && lunesKM.isEmpty() &&
                        martes.isEmpty() && martesKM.isEmpty() &&
                        miercoles.isEmpty() && miercolesKM.isEmpty() &&
                        jueves.isEmpty() && juevesKM.isEmpty() &&
                        viernes.isEmpty() && viernesKM.isEmpty() &&
                        sabado.isEmpty() && sabadoKM.isEmpty() &&
                        domingo.isEmpty() && domingoKM.isEmpty()) {
                    Snackbar.make(view, "Debe rellenar los campos de al menos 1 día.", Snackbar.LENGTH_LONG).show();
                } else {
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
                                addEntrenamiento(dia, entrenamiento, lunes, lunesKM, entrenamientos);
                                break;
                            case TUESDAY:
                                addEntrenamiento(dia, entrenamiento, martes, martesKM, entrenamientos);
                                break;
                            case WEDNESDAY:
                                addEntrenamiento(dia, entrenamiento, miercoles, miercolesKM, entrenamientos);
                                break;
                            case THURSDAY:
                                addEntrenamiento(dia, entrenamiento, jueves, juevesKM, entrenamientos);
                                break;
                            case FRIDAY:
                                addEntrenamiento(dia, entrenamiento, viernes, viernesKM, entrenamientos);
                                break;
                            case SATURDAY:
                                addEntrenamiento(dia, entrenamiento, sabado, sabadoKM, entrenamientos);
                                break;
                            case SUNDAY:
                                addEntrenamiento(dia, entrenamiento, domingo, domingoKM, entrenamientos);
                                break;
                        }
                    }

                    if (!entrenamientos.isEmpty()) {
                        for (Entrenamiento entrenamiento : entrenamientos) {
                            if (esNuevo) {
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
                            } else {
                                for (LocalDate dia : diasSemana) {
                                    LocalDate diaEntrenamiento = Globals.textToLocalDate(entrenamiento.fecha);
                                    if (dia.isEqual(diaEntrenamiento)) {
                                        switch (dia.getDayOfWeek()) {
                                            case MONDAY:
                                                updateEntrenamiento(idLunes.getText().toString(), dia, entrenamiento, etLunes.getText().toString(), etLunesKM.getText().toString());
                                                break;
                                            case TUESDAY:
                                                updateEntrenamiento(idMartes.getText().toString(), dia, entrenamiento, etMartes.getText().toString(), etMartesKM.getText().toString());
                                                break;
                                            case WEDNESDAY:
                                                updateEntrenamiento(idMiercoles.getText().toString(), dia, entrenamiento, etMiercoles.getText().toString(), etMiercolesKM.getText().toString());
                                                break;
                                            case THURSDAY:
                                                updateEntrenamiento(idJueves.getText().toString(), dia, entrenamiento, etJueves.getText().toString(), etJuevesKM.getText().toString());
                                                break;
                                            case FRIDAY:
                                                updateEntrenamiento(idViernes.getText().toString(), dia, entrenamiento, etViernes.getText().toString(), etViernesKM.getText().toString());
                                                break;
                                            case SATURDAY:
                                                updateEntrenamiento(idSabado.getText().toString(), dia, entrenamiento, etSabado.getText().toString(), etSabadoKM.getText().toString());
                                                break;
                                            case SUNDAY:
                                                updateEntrenamiento(idDomingo.getText().toString(), dia, entrenamiento, etDomingo.getText().toString(), etDomingoKM.getText().toString());
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Snackbar.make(view, "Sin datos que registrar. Rellene los dos campos del día que quiera registrar.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        return mView;
    }

    private static void addEntrenamiento(LocalDate fecha, Entrenamiento entrenamiento, String sesiondia, String sesionkm, List<Entrenamiento> entrenamientos) {
        entrenamiento.fecha = Globals.LocalDateToText(fecha);
        entrenamiento.sesion = sesiondia;
        entrenamiento.km = sesionkm.isEmpty() ? 0 : Double.parseDouble(sesionkm);
        entrenamientos.add(entrenamiento);
    }

    private static void updateEntrenamiento(String id, LocalDate fecha, Entrenamiento entrenamiento, String sesiondia, String sesionkm) {
        entrenamiento.id = id;
        entrenamiento.fecha = Globals.LocalDateToText(fecha);
        entrenamiento.sesion = sesiondia;
        entrenamiento.km = sesionkm.isEmpty() ? 0 : Double.parseDouble(sesionkm);

        Query query = mDatabase.child("entrenamiento").orderByChild("id").equalTo(entrenamiento.id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot entrenamientoSnapshot : dataSnapshot.getChildren()) {
                        Entrenamiento entrenamientoBBDD = entrenamientoSnapshot.getValue(Entrenamiento.class);
                        if (entrenamientoBBDD != null) {
                            // Actualizar el usuario en Firebase
                            entrenamientoSnapshot.getRef().setValue(entrenamiento)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Manejo del éxito de la actualización
                                            Snackbar.make(mView, "Datos actualizados correctamente", Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Manejo del fallo de la actualización
                                            Snackbar.make(mView, "Error al actualizar los datos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                } else {
                    Snackbar.make(mView, "Entrenamiento no encontrado.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(mView, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
        etFiltroSemana.setText("Semana " + semanaSelect);
        if (spDeportista.getVisibility() == View.GONE){
            rellenarDatos(Globals.usuario, semanaSelect);
        }
        else{
            Usuario usuario = new Usuario();
            usuario.id = idDeportistaSel;
            rellenarDatos(usuario, semanaSelect);
        }
    }

    private void spVisible() {
        if (Globals.usuario.perfil.equalsIgnoreCase("deportista")) {
            spDeportista.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            selectDiaSemana();
            rellenarDatos(Globals.usuario, semanaSelect);
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
                    ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establecer el adaptador en el Spinner
                    spDeportista.setAdapter(adapter);
                    // Definir un listener para manejar la selección del Spinner si es necesario
                    spDeportista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Usuario usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                            idDeportistaSel = usuarioSeleccionado.id;
                            selectDiaSemana();
                            rellenarDatos(usuarioSeleccionado, semanaSelect);
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
                    Snackbar.make(mView, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void selectDiaSemana() {
        etFiltroFecha = mView.findViewById(R.id.etFiltroFecha);
        etFiltroSemana = mView.findViewById(R.id.etFiltroSemana);

        // Mostrar la fecha actual en el EditText
        etFiltroFecha.setText(Globals.LocalDateToText(Globals.fechaActual));
        etFiltroSemana.setText("Semana " + Globals.semanaActual);
        semanaSelect = Globals.semanaActual;
    }

    private void rellenarDatos(Usuario usuario, int semana) {
        Query query = mDatabase.child("entrenamiento").orderByChild("idDeportista").equalTo(usuario.id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterar sobre los entrenamientos y agregarlos a la lista
                esNuevo = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Entrenamiento entrenamiento = snapshot.getValue(Entrenamiento.class);
                    LocalDate fecha = Globals.textToLocalDate(entrenamiento.fecha);
                    if (fecha.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == semana) {
                        esNuevo = false;
                        DayOfWeek dia = fecha.getDayOfWeek();
                        switch (dia) {
                            case MONDAY:
                                idLunes.setText(entrenamiento.id);
                                etLunes.setText(entrenamiento.sesion);
                                etLunesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case TUESDAY:
                                idMartes.setText(entrenamiento.id);
                                etMartes.setText(entrenamiento.sesion);
                                etMartesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case WEDNESDAY:
                                idMiercoles.setText(entrenamiento.id);
                                etMiercoles.setText(entrenamiento.sesion);
                                etMiercolesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case THURSDAY:
                                idJueves.setText(entrenamiento.id);
                                etJueves.setText(entrenamiento.sesion);
                                etJuevesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case FRIDAY:
                                idViernes.setText(entrenamiento.id);
                                etViernes.setText(entrenamiento.sesion);
                                etViernesKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case SATURDAY:
                                idSabado.setText(entrenamiento.id);
                                etSabado.setText(entrenamiento.sesion);
                                etSabadoKM.setText(String.valueOf(entrenamiento.km));
                                break;
                            case SUNDAY:
                                idDomingo.setText(entrenamiento.id);
                                etDomingo.setText(entrenamiento.sesion);
                                etDomingoKM.setText(String.valueOf(entrenamiento.km));
                                break;
                        }
                    }
                }
                if (esNuevo){
                    limpiarCampos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(mView, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                esNuevo = true;
            }
        });
    }

    private void limpiarCampos(){
        idLunes.setText("");
        etLunes.setText("");
        etLunesKM.setText("");

        idMartes.setText("");
        etMartes.setText("");
        etMartesKM.setText("");

        idMiercoles.setText("");
        etMiercoles.setText("");
        etMiercolesKM.setText("");

        idJueves.setText("");
        etJueves.setText("");
        etJuevesKM.setText("");

        idViernes.setText("");
        etViernes.setText("");
        etViernesKM.setText("");

        idSabado.setText("");
        etSabado.setText("");
        etSabadoKM.setText("");

        idDomingo.setText("");
        etDomingo.setText("");
        etDomingoKM.setText("");
    }
}