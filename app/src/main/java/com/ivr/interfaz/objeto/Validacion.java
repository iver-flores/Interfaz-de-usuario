package com.ivr.interfaz.objeto;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.ivr.interfaz.entidades.ListaDatos;
import com.ivr.interfaz.sql.SqliteDato;
import com.ivr.interfaz.utilizables.Fecha;
import com.ivr.interfaz.utils.PreferenceUtil;

import java.util.regex.Pattern;

public class Validacion {

    private SqliteDato mDatabase;

    public boolean esNumeroValido(String numero) {
        Pattern patron = Pattern.compile("^[0-9]+$");
        if (numero.equals("")){
            return false;
        }else {
            if(!patron.matcher(numero).matches() || numero.length() > 2
                    || Integer.parseInt(numero) > 14 || Integer.parseInt(numero) < 1) {
                return false;
            }
        }
        return true;
    }

    public boolean esDecimalValido(String numero) {
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
        if (numero.equals("")){
            return false;
        }else {
            if(!patron.matcher(numero).matches() || numero.length() > 4
                    || Float.parseFloat(numero) > 50){
                return false;
            }
        }
        return true;
    }


    public boolean esIpValido(String ip) {
        Pattern patron = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        if (ip.equals("")){
            return false;
        }else {
            if(!patron.matcher(ip).matches() || ip.length() < 10 || ip.length() > 15) {
                return false;
            }
        }
        return true;
    }

    public boolean esTextoValido(String texto) {
        Pattern patron = Pattern.compile("^[a-zA-Z-0-9\u00F1\u00D1 ]+$");
        if (texto.equals("")){
            return false;
        }else {
            if(!patron.matcher(texto).matches() || texto.length() < 10 || texto.length() > 35 ) {
                return false;
            }
        }
        return true;
    }

    public boolean esTelefonoValido(String telefono) {
        Pattern patron = Pattern.compile("^[0-9]+$");
        if (telefono.equals("")){
            return false;
        }else {
            if(!patron.matcher(telefono).matches() || telefono.length() < 8 || telefono.length() > 10 ) {
                return false;
            }
        }
        return true;
    }

    public boolean esSsidValido(String ssid) {
        Pattern patron = Pattern.compile("^[a-zA-Z-0-9 ]+$");
        if (ssid.equals("")){
            return false;
        }else {
            if(!patron.matcher(ssid).matches() || ssid.length() < 5 || ssid.length() > 10 ) {
                return false;
            }
        }
        return true;
    }

    public boolean esPasswordValido(String password) {
        Pattern patron = Pattern.compile("^[a-zA-Z-0-9 ]+$");
        if (password.equals("")){
            return false;
        }else {
            if(!patron.matcher(password).matches() || password.length() < 8 || password.length() > 18 ) {
                return false;
            }
        }
        return true;
    }

    public String[] sacarDatos(String dato, Context context){
        String sacarNombre, sacarTelefono, sacarGrupo, ip, nuevo, serial="", nombre="";
        String[] idEsp = PreferenceUtil.INSTANCE.getResultIdE(context).split("\\-");

        Fecha fecha = new Fecha();
        mDatabase = new SqliteDato(context);

        if (dato.length() > 20) {
            nuevo = (dato.replace(" ", "$")).trim();

            for (String s : idEsp) {
                if (nuevo.substring(0, 8).equals(s)) {
                    serial = s;
                    break;
                }
            }

            if(dato.contains("XWw")) {
                if (dato.contains("udp")) {
                    if (dato.contains("0dell")) {
                        if (dato.contains("1089")) {
                            if (dato.contains("hftth")) {
                                if (dato.contains("sj4")) {
                                    if (dato.contains("okyes")) {
                                        sacarNombre = dato.substring(dato.indexOf("XWw") + 3,
                                                dato.indexOf("udp"));
                                        sacarTelefono = dato.substring(dato.indexOf("udp") + 3,
                                                dato.indexOf("0dell"));
                                        sacarGrupo = dato.substring(dato.indexOf("0dell") + 5,
                                                dato.indexOf("1089"));
                                        nombre = sacarNombre.replace("$", " ");

                                        ListaDatos listaDatos = new ListaDatos(
                                                "Pasaje : ",
                                                "Conductor : " + nombre,
                                                "Telefono : " + sacarTelefono,
                                                "Grupo : " + sacarGrupo.replace("$", " "),
                                                "Fecha : " + fecha.sacarFechaHora());

                                        mDatabase.addDatos(listaDatos);
                                        String ssid = dato.substring(dato.indexOf("1089") + 4,
                                                dato.indexOf("hftth"));
                                        String password = dato.substring(dato.indexOf("hftth") + 5,
                                                dato.indexOf("sj4"));
                                        ip = (dato.substring(dato.indexOf("sj4") + 3,
                                                dato.indexOf("okyes")) + ":58491").trim();
                                        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            v.vibrate(VibrationEffect.createOneShot(500,
                                                    VibrationEffect.DEFAULT_AMPLITUDE));
                                        } else {
                                            v.vibrate(500);
                                        }
                                        if (serial.length() > 7){
                                            return new String[] {ssid, password, serial, ip, nombre};
                                        }else {
                                            return new String[]{"", "", ""};
                                        }
                                    }else {
                                        return new String[]{"", "", ""};
                                    }
                                }else {
                                    return new String[]{"", "", ""};
                                }
                            }else {
                                return new String[]{"", "", ""};
                            }
                        }else {
                            return new String[]{"", "", ""};
                        }
                    }else {
                        return new String[]{"", "", ""};
                    }
                }else {
                    return new String[]{"", "", ""};
                }
            }else {
                return new String[]{"", "", ""};
            }

        }else {
            return new String[]{"", "", ""};
        }
    }
}
