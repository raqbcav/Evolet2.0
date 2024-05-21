package com.example.evolet20.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evolet20.MainActivity;
import com.example.evolet20.Model.Usuario;
import com.example.evolet20.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

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
                if (registrarUsuario(view)){
                    Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intentMain);
                }
            }
        });
    }

    private boolean registrarUsuario(View view){
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();

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

            if (!newPass.equals(newPass2)){
                Snackbar.make(view, "Las contraseñas deben ser iguales.", Snackbar.LENGTH_LONG).show();
                return false;
            }

            String hashPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
            Usuario usuario = new Usuario(perfil, nombre, email, hashPass);
            mDatabase.child("usuarios").child(UUID.randomUUID().toString()).setValue(usuario);
            return true;
        }
        catch (Exception e){
            Snackbar.make(view, "Error al registrar el usuario.", Snackbar.LENGTH_LONG).show();
            return false;
        }
    }
}