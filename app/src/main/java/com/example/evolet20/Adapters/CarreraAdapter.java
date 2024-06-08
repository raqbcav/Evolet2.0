package com.example.evolet20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.evolet20.Model.Carrera;
import com.example.evolet20.R;

import java.util.List;

public class CarreraAdapter extends ArrayAdapter<Carrera> {
    private Context mContext;
    private List<Carrera> mCarreras;

    public CarreraAdapter(Context context, List<Carrera> carreras) {
        super(context, 0, carreras);
        mContext = context;
        mCarreras = carreras;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_carrera, parent, false);
        }

        Carrera carrera = mCarreras.get(position);

        TextView tvTipo = listItem.findViewById(R.id.tvTipo);
        TextView tvDistancia = listItem.findViewById(R.id.tvDistancia);
        TextView tvLugar = listItem.findViewById(R.id.tvLugar);
        TextView tvFecha = listItem.findViewById(R.id.tvFecha);
        tvTipo.setText("Tipo: " + carrera.tipo);
        tvDistancia.setText(carrera.distancia + "K");
        tvLugar.setText("Lugar: " + carrera.lugar);
        tvFecha.setText("Fecha: " + carrera.fecha);

        return listItem;
    }
}

