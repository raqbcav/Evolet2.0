package com.example.evolet20.Model;

public class Usuario {
    public String id;
    public String perfil;
    public String nombre;
    public String email;
    public String pass;

    public Usuario(){

    }

    public Usuario(String id, String perfil, String nombre, String email, String pass){
        this.id = id;
        this.perfil = perfil;
        this.nombre = nombre;
        this.email = email;
        this.pass = pass;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
