package com.ivr.interfaz.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ivr.interfaz.entidades.ListaDatos;

import java.util.ArrayList;

public class SqliteDato extends SQLiteOpenHelper {

    private	static final int DATABASE_VERSION     =	 5;
    private	static final String	DATABASE_NAME     = "esquina";
    private	static final String TABLE             = "datos";

    public static final String CAMPO_ID           = "id";

    public static final String CAMPO_PASAJE       = "pasaje";
    public static final String CAMPO_NOMBRE       = "nombre";
    public static final String CAMPO_TELEFONO     = "telefono";
    public static final String CAMPO_GRUPO        = "grupo";
    public static final String CAMPO_HORA         = "hora";

    public SqliteDato(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREAR_TABLA = "CREATE TABLE " +  TABLE + "("+
                CAMPO_ID            + " INTEGER PRIMARY KEY   AUTOINCREMENT, "  +
                CAMPO_PASAJE        + " TEXT, "     +
                CAMPO_NOMBRE        + " TEXT, "     +
                CAMPO_TELEFONO      + " TEXT, "     +
                CAMPO_GRUPO         + " TEXT, "     +
                CAMPO_HORA          + " TEXT) ";

        db.execSQL(CREAR_TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public ArrayList<ListaDatos> listarDatos(){
        String sql = "select * from " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListaDatos> storeDispositivos = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor!=null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String pasaje = cursor.getString(1);
                    String nombre = cursor.getString(2);
                    String telefono = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String hora = cursor.getString(5);
                    storeDispositivos.add(new ListaDatos(pasaje, nombre, telefono, grupo, hora));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return storeDispositivos;
    }

    public void addDatos(ListaDatos dispositivos){
        ContentValues values = new ContentValues();
        values.put(CAMPO_PASAJE, dispositivos.getPasaje());
        values.put(CAMPO_NOMBRE, dispositivos.getNombre());
        values.put(CAMPO_TELEFONO, dispositivos.getTelefono());
        values.put(CAMPO_GRUPO, dispositivos.getGrupo());
        values.put(CAMPO_HORA, dispositivos.getHora());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE, null, values);
        db.close();
    }

    public void borrarDatos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

    public void updatePasaje(String pasaje){
        ContentValues values = new ContentValues();
        values.put(CAMPO_PASAJE, pasaje);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE, values,  CAMPO_ID + " = ? ", new String[]{getLastId()});
        db.close();
    }

    public String getLastId() {
        String id = "";
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE, new String[] {CAMPO_ID}, null, null, null, null, null);

        if (cursor.moveToLast()) {
            id = cursor.getString(0);
        }
        return id;
    }

    public float leerPasaje(){
        SQLiteDatabase db = this.getReadableDatabase();
        float pasaje = -1;

        try {
            Cursor cursor = db.rawQuery("SELECT " + CAMPO_PASAJE  + " FROM " + TABLE, null);
            if (cursor != null && cursor.moveToFirst()){
                cursor.moveToLast();
                @SuppressLint("Range") String pasa = cursor.getString(cursor.getColumnIndex(CAMPO_PASAJE));
                pasaje = Float.parseFloat(pasa.substring(9, 13));
            }
            cursor.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        db.close();
        return pasaje;
    }

}
