package com.ivr.interfaz.utilizables;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fecha {

    public String sacarFechaHora(){
        String formato = "dd-MM-yyyy HH:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formato);
        return simpleDateFormat.format(new Date());
    }
}
