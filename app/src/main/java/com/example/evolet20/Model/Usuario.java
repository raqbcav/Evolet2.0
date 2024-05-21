package com.example.evolet20.Model;

public class Usuario {
    public String perfil;
    public String nombre;
    public String email;
    public String pass;

    public Usuario(){

    }

    public Usuario(String perfil, String nombre, String email, String pass){
        this.perfil = perfil;
        this.nombre = nombre;
        this.email = email;
        this.pass = pass;
    }
}
