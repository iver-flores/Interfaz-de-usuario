package com.ivr.interfaz.sql;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ivr.interfaz.entidades.Ruta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SqliteRuta extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static final String DB_NAME = "minibus";


    private SQLiteDatabase myDataBase;
    private final Context mContext;

    String[] lineasOrigen, lineasDestino;
    ArrayList<String> arrTblName = new ArrayList<>();

    @SuppressLint("SdCardPath")
    public SqliteRuta(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}



    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    private boolean checkDataBase(){
        SQLiteDatabase tempDB = null;
        try {
            String path = DB_PATH + DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (Exception ignored){ }

        if (tempDB != null){
            tempDB.close();
        }

        return tempDB != null;

    }

    public void copyDataBase(){
        try {
            InputStream inputStream = mContext.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH + DB_NAME;
            OutputStream outputStream = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openDataBase(){
        String path = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void createDataBase() {
        boolean isDbExist = checkDataBase();
        if (!isDbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressLint("Range")
    public void verTabla(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND " +
                    "name!='android_metadata' AND name!='sqlite_sequence' ORDER BY name ",
                    null);
            if (c != null) {
                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        arrTblName.add(c.getString(c.getColumnIndex("name")));
                        c.moveToNext();
                    }
                }
            }
            assert c != null;
            c.close();
        }catch (Exception ignored){
        }
        db.close();
        llenarRutas(arrTblName, arrTblName.size());
    }

    @SuppressLint("Recycle")
    public void llenarRutas(ArrayList<String> arrTblName, int size){

        StringBuilder linOri = new StringBuilder();
        StringBuilder linDes = new StringBuilder();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            for (int i = 0; i < size; i++) {
                c = db.rawQuery("SELECT tramoida, tramobuelta FROM " + arrTblName.get(i), null);
                if (c != null) {
                    c.moveToFirst();
                    linOri.append(c.getString(0)).append(", ");
                    linDes.append(c.getString(1)).append(", ");
                }

            }
            lineasOrigen = linOri.toString().split(",");
            lineasDestino = linDes.toString().split(",");

        }catch (Exception ignored){ }
    }

    public List<Ruta> datosOrigen(){
        List<Ruta> list = new ArrayList<>();
        List<String> llLis = new ArrayList<>();
        for (String value : lineasOrigen) {
            llLis.add(value.substring(value.indexOf(".") + 1).trim());
        }
        Set<String> hashSet = new HashSet<>(llLis);
        llLis.clear();
        llLis.addAll(hashSet);
        for (String s : llLis) {
            list.add(new Ruta(s.trim()));
        }
        return list;
    }

    public List<Ruta> datosDestino(){
        List<Ruta> list = new ArrayList<>();
        List<String> llLis = new ArrayList<>();
        for (String value : lineasDestino) {
            llLis.add(value.substring(value.indexOf(".") + 1).trim());
        }
        Set<String> hashSet = new HashSet<>(llLis);
        llLis.clear();
        llLis.addAll(hashSet);
        for (String s : llLis) {
            list.add(new Ruta(s.trim()));
        }
        return list;
    }

    @SuppressLint("Recycle")
    public String verRutaCompleta(String origen, String destino) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder nueLin = new StringBuilder();
        Cursor c = null;
        int cont = 0;
        try {
            for (int i=0 ; i<arrTblName.size() ; i++) {
                String[] linIda;
                String[] linBue;
                c = db.rawQuery("SELECT tramoida, tramobuelta FROM " + arrTblName.get(i), null);
                if (c != null) {
                    c.moveToFirst();
                    linIda = (c.getString(0).trim()).split(",");

                    for (String value : linIda) {
                        String e = value.substring(value.indexOf(".") + 1).trim();
                        if (origen.equals(e)) {
                            cont = 1;
                        }
                        if (destino.equals(e) && cont == 1) {
                            cont = 2;
                            nueLin.append(arrTblName.get(i)).append(", ");
                        }
                    }
                    if (cont != 2){
                        linBue = (c.getString(1)).split(",");
                        cont = 0;
                        for (String s : linBue) {
                            final String d = s.substring(s.indexOf(".") + 1).trim();
                            if (origen.equals(d)) {
                                cont = 1;
                            }
                            if (destino.equals(d) && cont == 1) {
                                cont = 2;
                                nueLin.append(arrTblName.get(i)).append(", ");
                            }
                        }
                    }
                    cont = 0;
                }
            }
        }catch (Exception ignored){}
        assert c != null;
        c.close();
        db.close();
        return nueLin.toString();
    }

    public String verRutaIncompleta(String destino) {

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder nueLin = new StringBuilder();
        Cursor c = null;
        int cont = 0;
        try {
            for (int i=0 ; i<arrTblName.size() ; i++) {
                String[] linIda;
                String[] linBue;
                c = db.rawQuery("SELECT tramoida, tramobuelta FROM " + arrTblName.get(i), null);
                if (c != null) {
                    c.moveToFirst();
                    linIda = (c.getString(0).trim()).split(",");
                    for (String value : linIda) {
                        if (destino.equals(value.trim())) {
                            nueLin.append(arrTblName.get(i)).append(", ");
                            cont = 1;
                        }
                    }
                    if (cont != 1) {
                        linBue = (c.getString(1)).split(",");
                        for (String s : linBue) {
                            if (destino.equals(s.trim())) {
                                nueLin.append(arrTblName.get(i)).append(", ");
                            }
                        }
                    }
                }
            }
        }catch (Exception ignored){ }
        assert c != null;
        c.close();
        db.close();
        return nueLin.toString();
    }

    public String[] sacaLinea(){
        String[] ruta;
        ruta = new String[arrTblName.size()];
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            for (int i = 0; i < arrTblName.size(); i++) {
                ruta[i] = arrTblName.get(i);
            }
        }catch (Exception ignored){ }
        db.close();

        return ruta;
    }

    public  String[] sacaIdaBue(String linea){
        String[] ruta = {"a", "b"};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT tramoida, tramobuelta FROM " + linea.trim(), null);
            if (c != null) {
                c.moveToFirst();
                ruta[0] = c.getString(0);
                ruta[1] = c.getString(1);
            }
            assert c != null;
            c.close();
        }catch (Exception ignored){ }
        db.close();
        return ruta;
    }

}
