package com.example.evolet20.Login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evolet20.MainActivity;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.example.evolet20.Static.Globals;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (isNetworkAvailable()) {
                    if (isValidEmail(email)) {
                        validateUser(view, email, password);
                    } else {
                        Snackbar.make(view, "El formato del email no es válido.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(view, "No se puede realizar el inicio de sesión sin conexión a internet.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validateUser(View view, String email, String password) {
        mDatabase.child("usuario").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedHash = userSnapshot.child("pass").getValue(String.class);
                        if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                            saveUsuarioGlobals(userSnapshot, storedHash);
                            Snackbar.make(view, "Inicio de sesión exitoso.", Snackbar.LENGTH_LONG).show();
                            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intentMain);
                        } else {
                            Snackbar.make(view, "Contraseña incorrecta.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Snackbar.make(view, "No se encontró un usuario con ese email.", Snackbar.LENGTH_LONG).show();
                }
            }

            private void saveUsuarioGlobals(DataSnapshot userSnapshot, String storedHash) {
                String storedId = userSnapshot.child("id").getValue(String.class);
                String storedEmail = userSnapshot.child("email").getValue(String.class);
                String storedNombre = userSnapshot.child("nombre").getValue(String.class);
                String storedPerfil = userSnapshot.child("perfil").getValue(String.class);
                Globals.usuario = new Usuario(storedId, storedPerfil, storedNombre, storedEmail, storedHash);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(view, "Error en la validación del usuario: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
