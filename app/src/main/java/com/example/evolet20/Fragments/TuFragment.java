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

import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class TuFragment extends Fragment {
    private DatabaseReference mDatabase;
    private ConnectivityManager connectivityManager;

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

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnEditar = view.findViewById(R.id.btnEditar);
        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPass = view.findViewById(R.id.etPass);
        EditText etPass2 = view.findViewById(R.id.etPass2);

        etNombre.setText(Globals.usuario.nombre);
        etEmail.setText(Globals.usuario.email);
        etPass.setText(Globals.usuario.pass);
        etPass2.setText(Globals.usuario.pass);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEditar.setVisibility(View.GONE);
                btnGuardar.setVisibility(View.VISIBLE);
                etNombre.setEnabled(true);
                etEmail.setEnabled(true);
                etPass.setEnabled(true);
                etPass2.setEnabled(true);
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
                if (!pass.equalsIgnoreCase("") && !pass2.equalsIgnoreCase("")){
                    hashPass = BCrypt.hashpw(pass, BCrypt.gensalt());
                }
                updateUsuario(view, nombre, email, hashPass);
                btnEditar.setVisibility(View.VISIBLE);
                btnGuardar.setVisibility(View.GONE);
                etNombre.setEnabled(false);
                etEmail.setEnabled(false);
                etPass.setEnabled(false);
                etPass2.setEnabled(false);
            }
        });

        return view;
    }

    private void updateUsuario(View view, String nombre, String email, String pass) {
        DatabaseReference usuarioRef = mDatabase.child("usuario").child(Globals.usuario.id);
        Map<String, Object> actualizacionDatos = new HashMap<>();
        actualizacionDatos.put("nombre", nombre);
        actualizacionDatos.put("email", email);
        if (!pass.equalsIgnoreCase("")){
            actualizacionDatos.put("pass", pass);
        }

        usuarioRef.updateChildren(actualizacionDatos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Datos actualizados correctamente", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Error al actualizar los datos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
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
