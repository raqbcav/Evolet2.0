package com.example.evolet20.Model;

public class DatosActividad {
    public String idEntrenamiento;
    public String tiempoTotal;
    public String distanciaTotal;
    public String velocidadMedia;
    public String fc;
    public String comentario;
    public String fuerza;
    public String rodaje;

    public DatosActividad() {
    }

    public DatosActividad(String idEntrenamiento, String tiempoTotal, String distanciaTotal, String velocidadMedia, String fc, String comentario, String fuerza, String rodaje) {
        this.idEntrenamiento = idEntrenamiento;
        this.tiempoTotal = tiempoTotal;
        this.distanciaTotal = distanciaTotal;
        this.velocidadMedia = velocidadMedia;
        this.fc = fc;
        this.comentario = comentario;
        this.fuerza = fuerza;
        this.rodaje = rodaje;
    }
}
