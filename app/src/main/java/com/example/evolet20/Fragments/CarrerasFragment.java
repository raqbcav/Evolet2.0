package com.example.evolet20.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.evolet20.Adapters.CarreraAdapter;
import com.example.evolet20.Model.Carrera;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CarrerasFragment extends Fragment {

    private View mView;
    private DatabaseReference mDatabase;
    private Button btnNewCarrera;
    private Spinner spFiltroTipo, spFiltroDistancia, spNewTipo, spNewDistancia;
    private EditText etFiltroFecha, etNewFecha, etNewLugar;
    private ImageButton ibFiltroFecha, ibNewFecha;
    private final Calendar calendarFiltro = Calendar.getInstance();
    private final Calendar calendarNew = Calendar.getInstance();

    private Carrera filtroCarrera = new Carrera();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_carreras, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        spVisible(mView);
        rellenarSpinners(mView);

        etFiltroFecha = mView.findViewById(R.id.etFiltroFecha);
        ibFiltroFecha = mView.findViewById(R.id.ibFiltroFecha);
        ibFiltroFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListenerFiltro,
                        calendarFiltro.get(Calendar.YEAR), calendarFiltro.get(Calendar.MONTH),
                        calendarFiltro.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etNewFecha = mView.findViewById(R.id.etNewFecha);
        ibNewFecha = mView.findViewById(R.id.ibNewFecha);
        ibNewFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListenerNew,
                        calendarNew.get(Calendar.YEAR), calendarNew.get(Calendar.MONTH),
                        calendarNew.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etNewLugar = mView.findViewById(R.id.etNewLugar);

        btnNewCarrera = mView.findViewById(R.id.btnNewCarrera);
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

        return mView;
    }

    private void selectDiaSemana(View view) {
        etFiltroFecha = view.findViewById(R.id.etFiltroFecha);
        filtroCarrera.fecha = Globals.LocalDateToText(Globals.fechaActual);

        // Mostrar la fecha actual en el EditText
        etFiltroFecha.setText(Globals.LocalDateToText(Globals.fechaActual));
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
        filtroCarrera.fecha = etFiltroFecha.getText().toString();
        rellenarListView(mView, filtroCarrera);
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
            selectDiaSemana(view);
            filtroCarrera.idUsuario = Globals.usuario.id;
            rellenarListView(mView, filtroCarrera);
        } else {
            // Agregar un listener para obtener los datos de los usuarios
            mDatabase.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Usuario> usuarios = new ArrayList<>();

                    // Iterar sobre los usuarios y agregarlos a la lista
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        String idUsuario = usuarioSnapshot.child("id").getValue(String.class);
                        String nombreUsuario = usuarioSnapshot.child("nombreUsuario").getValue(String.class);
                        if (nombreUsuario != null) {
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
                            filtroCarrera.idUsuario = usuarioSeleccionado.id;
                            selectDiaSemana(view);
                            // Cargamos los datos de las carreras del usuario seleccionado
                            rellenarListView(mView, filtroCarrera); // Pasamos la referencia a la vista
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

    // Método para cargar los datos de las carreras del usuario seleccionado
    private void rellenarListView(View view, Carrera filtroCarrera) {
        List<Carrera> listCarreras = new ArrayList<Carrera>();
        // Agregar un listener para obtener los datos de las carreras del usuario seleccionado
        Query query = mDatabase.child("carrera");

        // Aplicar filtro por idUsuario si no es null ni una cadena vacía
        if (filtroCarrera.idUsuario != null && !filtroCarrera.idUsuario.isEmpty()) {
            query = query.orderByChild("idUsuario").equalTo(filtroCarrera.idUsuario);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista de carreras antes de cargar nuevas
                listCarreras.clear();

                // Iterar sobre los datos de las carreras y agregarlos a la lista
                for (DataSnapshot carreraSnapshot : dataSnapshot.getChildren()) {
                    String tipo = carreraSnapshot.child("tipo").getValue(String.class);
                    String distancia = carreraSnapshot.child("distancia").getValue(String.class).replace("K", "");
                    String lugar = carreraSnapshot.child("lugar").getValue(String.class);
                    String fecha = carreraSnapshot.child("fecha").getValue(String.class);
                    boolean filtroOK = filtroMultiple(tipo, distancia, fecha, filtroCarrera);
                    if (filtroOK) {
                        listCarreras.add(new Carrera(tipo, distancia, lugar, fecha, filtroCarrera.idUsuario));
                    }
                }
                ListView listView = view.findViewById(R.id.lvCarreras);
                if (listView != null) {
                    if (!listCarreras.isEmpty()) {
                        CarreraAdapter adapter = new CarreraAdapter(getContext(), listCarreras);
                        listView.setAdapter(adapter);
                    } else {
                        listView.setAdapter(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(view, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private boolean filtroMultiple(String tipo, String distancia, String fecha, Carrera filtroCarrera) {
        if (filtroCarrera.tipo != null && !filtroCarrera.tipo.equalsIgnoreCase(tipo)) {
            return false;
        }
        if (filtroCarrera.distancia != null && !filtroCarrera.distancia.equalsIgnoreCase(distancia)) {
            return false;
        }
        if (filtroCarrera.fecha != null && !filtroCarrera.fecha.equalsIgnoreCase(fecha)) {
            return false;
        }
        return true;
    }

    private void rellenarSpinners(View view) {
        // Obteniendo referencias a los Spinners
        spFiltroTipo = view.findViewById(R.id.spFiltroTipo);
        spFiltroDistancia = view.findViewById(R.id.spFiltroDistancia);
        spNewTipo = view.findViewById(R.id.spNewTipo);
        spNewDistancia = view.findViewById(R.id.spNewDistancia);

        // Creando adaptadores para los Spinners
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(view.getContext(), R.array.tipoCarrera, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterDistancia = ArrayAdapter.createFromResource(view.getContext(), R.array.distanciaCarrera, android.R.layout.simple_spinner_item);
        adapterDistancia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configurando los adaptadores en los Spinners
        spFiltroTipo.setAdapter(adapterTipo);
        spFiltroDistancia.setAdapter(adapterDistancia);
        spNewTipo.setAdapter(adapterTipo);
        spNewDistancia.setAdapter(adapterDistancia);

        // Agregando eventos de selección a los Spinners
        spFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Manejar la selección del tipo de carrera en el filtro
                filtroCarrera.tipo = (String) parent.getItemAtPosition(position);
                rellenarListView(mView, filtroCarrera);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido pero no necesitamos implementarlo aquí
            }
        });

        spFiltroDistancia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Manejar la selección de la distancia en el filtro
                String distanciaSeleccionada = (String) parent.getItemAtPosition(position);
                filtroCarrera.distancia = distanciaSeleccionada.replace("K", "");
                rellenarListView(mView, filtroCarrera);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido pero no necesitamos implementarlo aquí
            }
        });
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

        // Generar una nueva clave única para la carrera
        String carreraId = mDatabase.child("carrera").push().getKey();

        // Crear un objeto Map para los datos de la carrera
        Carrera carrera = new Carrera(tipoCarrera, distanciaCarrera, lugarCarrera, fechaCarrera, Globals.usuario.id);

        // Guardar los datos de la carrera en la base de datos
        mDatabase.child("carrera").child(carreraId).setValue(carrera)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al registrar la carrera
                        Snackbar.make(view, "Carrera registrada con éxito", Snackbar.LENGTH_LONG).show();
                        rellenarListView(mView, filtroCarrera);
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
