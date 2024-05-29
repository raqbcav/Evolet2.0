package com.example.evolet20.Model;

public class Entrenamiento {
    public String id;
    public String idDeportista;
    public String idEntrenador;
    public String fecha;
    public String sesion;
    public double km;
    public int nSemana;

    public Entrenamiento() {
    }

    public Entrenamiento(String id, String idDeportista, String idEntrenador, String fecha, String sesion, double km, int nSemana) {
        this.id = id;
        this.idDeportista = idDeportista;
        this.idEntrenador = idEntrenador;
        this.fecha = fecha;
        this.sesion = sesion;
        this.km = km;
        this.nSemana = nSemana;
    }
}
