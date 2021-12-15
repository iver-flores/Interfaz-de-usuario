package com.ivr.interfaz;

import android.app.Application;

import com.ivr.interfaz.entidades.Ruta;

import java.util.List;

public class GlobalDatos extends Application {
    public static boolean conexion;
    public static boolean resultQr;
    public int netId ;
    public String cambio;
    public String usuario;
    public String dinero;
    public String modo;
    public String idEsp;

    public List<Ruta> miRuta;

    public boolean isConexion() {
        return conexion;
    }

    public void setConexion(boolean conexion) {
        GlobalDatos.conexion = conexion;
    }

    public boolean isResultQr() {
        return resultQr;
    }

    public void setResultQr(boolean resultQr) {
        GlobalDatos.resultQr = resultQr;
    }

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setDinero(String dinero) {
        this.dinero = dinero;
    }

    public String getDinero() {
        return dinero;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getModo() {
        return modo;
    }

    public void setIdEsp(String idEsp) {
        this.idEsp = idEsp;
    }

    public String getIdEsp() {
        return idEsp;
    }


    public void setMiRuta(List dinero) {
        this.miRuta = miRuta;
    }

    public List<Ruta> getMiRuta() { return miRuta; }

}
