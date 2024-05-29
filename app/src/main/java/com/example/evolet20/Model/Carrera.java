package com.example.evolet20.Model;

public class Carrera {
    public String tipo;
    public String distancia;
    public String lugar;
    public String fecha;
    public String idUsuario;

    public Carrera(){

    }

    public Carrera(String tipo, String distancia, String lugar, String fecha, String idUsuario){
        this.tipo = tipo;
        this.distancia = distancia;
        this.lugar = lugar;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
    }
}
