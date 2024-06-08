package com.example.evolet20.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class TuFragment extends Fragment {
    private DatabaseReference mDatabase;
    private ConnectivityManager connectivityManager;
    
    private Button btnGuardar, btnEditar;
    private EditText etNombre, etEmail, etPass, etPass2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tu, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnEditar = view.findViewById(R.id.btnEditar);
        etNombre = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etEmail);
        etPass = view.findViewById(R.id.etPass);
        etPass2 = view.findViewById(R.id.etPass2);

        etNombre.setText(Globals.usuario.nombre);
        etEmail.setText(Globals.usuario.email);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableCampos(true);
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Snackbar.make(view, "No se puede realizar el registro sin conexión a internet.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String nombre = etNombre.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String pass2 = etPass2.getText().toString();

                if (!isValidEmail(email)) {
                    Snackbar.make(view, "Por favor, introduce un correo electrónico válido", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!pass.equals(pass2)) {
                    Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String hashPass = "";
                if (!pass.equalsIgnoreCase("") && !pass2.equalsIgnoreCase("")) {
                    hashPass = BCrypt.hashpw(pass, BCrypt.gensalt());
                }

                updateUsuario(view, nombre, email, hashPass);
                enableCampos(false);
            }
        });

        return view;
    }

    private void enableCampos(boolean enabled) {
        btnEditar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        btnGuardar.setVisibility(enabled ? View.VISIBLE : View.GONE);
        etNombre.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPass.setEnabled(enabled);
        etPass2.setEnabled(enabled);
    }

    private void updateUsuario(View view, String nombre, String email, String pass) {
        // Crear un query para buscar el usuario por su id
        Query query = mDatabase.child("usuario").orderByChild("id").equalTo(Globals.usuario.id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        Usuario usuario = usuarioSnapshot.getValue(Usuario.class);
                        if (usuario != null) {
                            usuario.nombre = nombre;
                            usuario.email = email;
                            if (!pass.equalsIgnoreCase("")){
                                usuario.pass = pass;
                            }

                            // Actualizar el usuario en Firebase
                            usuarioSnapshot.getRef().setValue(usuario)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Manejo del éxito de la actualización
                                            Snackbar.make(view, "Datos actualizados correctamente", Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Manejo del fallo de la actualización
                                            Snackbar.make(view, "Error al actualizar los datos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                } else {
                    Snackbar.make(view, "Usuario no encontrado.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que falle la lectura de datos
                Snackbar.make(view, "Error al leer los datos de Firebase: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNetworkAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
