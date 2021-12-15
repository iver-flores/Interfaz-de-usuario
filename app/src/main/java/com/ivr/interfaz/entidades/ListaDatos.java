package com.ivr.interfaz.entidades;

public class ListaDatos {

    String pasaje;
    String nombre;
    String telefono;
    String grupo;
    String hora;


    public ListaDatos(String pasaje, String nombre, String telefono, String grupo, String hora) {
        this.pasaje = pasaje;
        this.nombre = nombre;
        this.telefono = telefono;
        this.grupo = grupo;
        this.hora = hora;
    }

    public String getPasaje() {
        return pasaje;
    }

    public void setPasaje(String pasaje) {
        this.pasaje = pasaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.grupo = hora;
    }
}
