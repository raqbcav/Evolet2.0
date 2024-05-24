package com.example.evolet20.Model;

public class Carrera {
    public String tipo;
    public String distancia;
    public String lugar;
    public String fecha;
    public String email;

    public Carrera(){

    }

    public Carrera(String tipo, String distancia, String lugar, String fecha, String email){
        this.tipo = tipo;
        this.distancia = distancia;
        this.lugar = lugar;
        this.fecha = fecha;
        this.email = email;
    }
}
