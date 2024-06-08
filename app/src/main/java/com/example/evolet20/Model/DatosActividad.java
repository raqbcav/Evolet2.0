package com.example.evolet20.Model;

public class DatosActividad {
    public String idEntrenamiento;
    public double tiempoTotal;
    public double distanciaTotal;
    public double velocidadMedia;
    public int fc;
    public String comentario;
    public boolean fuerza;
    public boolean rodaje;

    public DatosActividad() {
    }

    public DatosActividad(String idEntrenamiento, double tiempoTotal, double distanciaTotal, double velocidadMedia, int fc, String comentario, boolean fuerza, boolean rodaje) {
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
