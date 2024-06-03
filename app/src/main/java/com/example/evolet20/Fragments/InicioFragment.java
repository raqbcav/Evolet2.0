package com.example.evolet20.Fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evolet20.R;

public class InicioFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Obtener la vista de la gráfica
        View graphView = view.findViewById(R.id.graphView);

        // Establecer un dibujante personalizado en la vista
        graphView.setWillNotDraw(false);
        graphView.setBackground(null);
        graphView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.invalidate();
            }
        });

        return view;
    }

    public static class GraphView extends View {

        public GraphView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Configurar el pincel para dibujar la línea
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(5);

            // Obtener dimensiones de la vista
            int width = getWidth();
            int height = getHeight();

            // Dibujar la línea de la gráfica
            canvas.drawLine(0, height / 2, width, height / 2, paint);
        }
    }
}