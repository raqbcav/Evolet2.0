package com.example.evolet20.Model;

public class DatosActividad {
    public String idEntrenamiento;
    public double tiempoTotal;
    public double distanciaTotal;
    public double velocidadMedia;
    public int fc;
    public String comentario;
    public boolean realizado;

    public DatosActividad() {
    }

    public DatosActividad(String idEntrenamiento, double tiempoTotal, double distanciaTotal, double velocidadMedia, int fc, String comentario, boolean realizado) {
        this.idEntrenamiento = idEntrenamiento;
        this.tiempoTotal = tiempoTotal;
        this.distanciaTotal = distanciaTotal;
        this.velocidadMedia = velocidadMedia;
        this.fc = fc;
        this.comentario = comentario;
        this.realizado = realizado;
    }
}
