package com.example.evolet20.Login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evolet20.MainActivity;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Button btnRegister = findViewById(R.id.btnRegister);
        Spinner spPerfil = findViewById(R.id.spPerfil);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.perfiles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPerfil.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    registrarUsuario(view);
                } else {
                    Snackbar.make(view, "No se puede realizar el registro sin conexión a internet.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registrarUsuario(View view) {
        Spinner spPerfil = findViewById(R.id.spPerfil);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPass = findViewById(R.id.etPass);
        EditText etPass2 = findViewById(R.id.etPass2);

        String perfil = spPerfil.getSelectedItem().toString();
        String nombre = etNombre.getText().toString();
        String email = etEmail.getText().toString();
        String newPass = etPass.getText().toString();
        String newPass2 = etPass2.getText().toString();

        if (email.isEmpty() || nombre.isEmpty() || newPass.isEmpty() || newPass2.isEmpty()) {
            Snackbar.make(view, "Todos los campos son obligatorios.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(email)) {
            Snackbar.make(view, "El formato del email no es válido.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!newPass.equals(newPass2)) {
            Snackbar.make(view, "Las contraseñas deben ser iguales.", Snackbar.LENGTH_LONG).show();
            return;
        }

        checkUserExists(email, new OnUserCheckListener() {
            @Override
            public void onCheck(boolean exists) {
                if (exists) {
                    Snackbar.make(view, "Ya existe un usuario con ese email.", Snackbar.LENGTH_LONG).show();
                } else {
                    String hashPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
                    Usuario usuario = new Usuario(perfil, nombre, email, hashPass);
                    mDatabase.child("usuarios").child(UUID.randomUUID().toString()).setValue(usuario)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(view, "Usuario registrado con éxito.", Snackbar.LENGTH_LONG).show();
                                    Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intentMain);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Snackbar.make(view, "Error al registrar el usuario: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    private void checkUserExists(String email, final OnUserCheckListener listener) {
        mDatabase.child("usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onCheck(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    interface OnUserCheckListener {
        void onCheck(boolean exists);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}