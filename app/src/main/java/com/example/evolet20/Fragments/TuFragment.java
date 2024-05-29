package com.example.evolet20.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.evolet20.R;

public class TuFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tu, container, false);

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnEditar = view.findViewById(R.id.btnEditar);
        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPass = view.findViewById(R.id.etPass);
        EditText etPass2 = view.findViewById(R.id.etPass2);

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
}