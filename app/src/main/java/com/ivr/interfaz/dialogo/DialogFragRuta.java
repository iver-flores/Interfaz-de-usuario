package com.ivr.interfaz.dialogo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ivr.interfaz.adapter.AdaptadorRuta;
import com.ivr.interfaz.entidades.Ruta;
import com.ivr.interfaz.sql.SqliteRuta;
import com.ivr.interfaz.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DialogFragRuta extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    AlertDialog.Builder builder;

    private AutoCompleteTextView autoComOrigen, autoComDestino;

    private List<Ruta> mListOrigen, mListDestino;

    private TextView tvLinea;
    private TextView tvIda;
    private TextView tvBuelta;
    private Spinner spinner;
    private Button btnTramo;

    private LinearLayout llLinea, llRuta, llOpcion;

    private boolean cambiarRuta = true;

    SqliteRuta sqliteRuta;

    String[] strLineas;
    List<String> listaLineas;
    String nombreLinea;

    public DialogFragRuta(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearDialogoFrag();
    }

    private AlertDialog crearDialogoFrag() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (requireActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.frag_dialog_ruta, null);
        builder.setView(v);

        sqliteRuta = new SqliteRuta(getContext());
        sqliteRuta.createDataBase();
        sqliteRuta.verTabla();
        mListOrigen = sqliteRuta.datosOrigen();
        mListDestino = sqliteRuta.datosDestino();
        init(v);

        autoComOrigen.setOnItemClickListener((adapterView, view, pos, id) -> { });
        autoComDestino.setOnItemClickListener((adapterView, view, pos, id) -> { });

        return builder.create();
    }

    private void init(View v) {
        autoComOrigen = v.findViewById(R.id.act_origen);
        autoComDestino = v.findViewById(R.id.act_destino);
        ImageButton ibAtras = v.findViewById(R.id.ib_atras);
        Button btnLinea = v.findViewById(R.id.btn_linea);
        Button btnRuta = v.findViewById(R.id.btn_ruta);
        btnTramo = v.findViewById(R.id.btn_tramo);
        tvLinea = v.findViewById(R.id.tv_linea);
        tvIda= v.findViewById(R.id.tv_ida);
        tvBuelta = v.findViewById(R.id.tv_buelta);
        spinner = v.findViewById(R.id.spn);
        Button btnBuscarLinea = v.findViewById(R.id.btn_buscar_linea);
        Button btnBuscarRuta = v.findViewById(R.id.btn_buscar_ruta);
        llLinea = v.findViewById(R.id.ll_linea);
        llRuta  = v.findViewById(R.id.ll_ruta);
        llOpcion = v.findViewById(R.id.ll_opciones);

        AdaptadorRuta adapRutaOrigen = new AdaptadorRuta(getContext(), R.layout.activity_main, R.id.tv_search, mListOrigen);
        AdaptadorRuta adapRutaDestino = new AdaptadorRuta(getContext(), R.layout.activity_main, R.id.tv_search, mListDestino);

        btnLinea.setOnClickListener(this);
        btnRuta.setOnClickListener(this);
        btnTramo.setOnClickListener(this);
        ibAtras.setOnClickListener(this);

        autoComOrigen.setAdapter(adapRutaOrigen);
        autoComDestino.setAdapter(adapRutaDestino);
        spinner.setOnItemSelectedListener(this);
        btnBuscarLinea.setOnClickListener(this);
        btnBuscarRuta.setOnClickListener(this);

        listaLineas = new ArrayList<>();
        strLineas = sqliteRuta.sacaLinea();
        Collections.addAll(listaLineas, strLineas);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.search, R.id.tv_search, listaLineas);
        spinner.setAdapter(adapter);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_atras:
                dismiss();
                break;
            case R.id.btn_linea:
                String origen, destino;
                origen  = autoComOrigen.getText().toString().trim();
                destino = autoComDestino.getText().toString().trim();
                if (!destino.equals("")){
                    if (origen.equals("")){
                        mostResDestino(destino);
                    }else {
                        mostResOrigenDestino(origen, destino);
                    }
                }else {
                    Toast.makeText(getContext(), "Ingrese el destino por favor", Toast.LENGTH_LONG).show();
                }
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(autoComDestino.getWindowToken(), 0);
                break;
            case R.id.btn_ruta:
                cambiarRuta = true;
                String salRuta = "RUTA DE IDA\n," +  sqliteRuta.sacaIdaBue(spinner.getSelectedItem().toString())[0];
                tvIda.setVisibility(View.VISIBLE);
                tvBuelta.setVisibility(View.GONE);
                btnTramo.setVisibility(View.VISIBLE);
                btnTramo.setText(R.string.ver_buelta);
                tvIda.setText(salRuta.replace(",", "\n-").trim());
                tvIda.setMovementMethod(new ScrollingMovementMethod());
                break;
            case R.id.btn_buscar_linea:
                llLinea.setVisibility(View.VISIBLE);
                llOpcion.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_buscar_ruta:
                llRuta.setVisibility(View.VISIBLE);
                tvIda.setVisibility(View.GONE);
                tvBuelta.setVisibility(View.GONE);
                btnTramo.setVisibility(View.GONE);
                llOpcion.setVisibility(View.GONE);
                break;
            case R.id.btn_tramo:
                if (cambiarRuta = !cambiarRuta){
                    salRuta = "RUTA DE IDA\n," + sqliteRuta.sacaIdaBue(spinner.getSelectedItem().toString())[0];
                    tvIda.setVisibility(View.VISIBLE);
                    tvBuelta.setVisibility(View.GONE);
                    tvIda.setText(salRuta.replace(",", "\n-").trim());
                    tvIda.setMovementMethod(new ScrollingMovementMethod());
                    btnTramo.setText(R.string.ver_buelta);
                }else {
                    tvIda.setVisibility(View.GONE);
                    tvBuelta.setVisibility(View.VISIBLE);
                    salRuta = "RUTA DE BUELTA\n," + sqliteRuta.sacaIdaBue(spinner.getSelectedItem().toString())[1];
                    tvBuelta.setText(salRuta.replace(",", "\n-").trim());
                    tvBuelta.setMovementMethod(new ScrollingMovementMethod());
                    btnTramo.setText(R.string.ver_ida);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spn) {
            nombreLinea = Arrays.toString(strLineas).split(",")[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @SuppressLint("SetTextI18n")
    private void mostResDestino(String destino) {
        String result = sqliteRuta.verRutaIncompleta(destino);

        if (result.equals("")){
            tvLinea.setTextColor(getResources().getColor(R.color.purple_500));
            tvLinea.setText(Html.fromHtml("No se encontró el destino : <font color='#FF0015' ><b>" + destino.toUpperCase() + "</b></font>" +
                    "\n Por favor intente con otro nombre de origen."));
        }else {
            tvLinea.setTextColor(getResources().getColor(R.color.purple_500));
            tvLinea.setText(Html.fromHtml("La <font color='#018786' ><b>" + result.toUpperCase() + "</b></font> " +
                    "tiene como ruta el destino " + destino.toUpperCase() + "."  ));
        }
    }

    @SuppressLint("SetTextI18n")
    private void mostResOrigenDestino(String origen, String destino) {
        String result = sqliteRuta.verRutaCompleta(origen, destino);
        if (result.equals("")){
            tvLinea.setTextColor(getResources().getColor(R.color.purple_500));
            tvLinea.setText(Html.fromHtml("No se encontro el destino : <font color='#FF0015' ><b>" + destino.toUpperCase() + "</b></font>" +
                    "\n Por favor intente con otro nombre de origen."));
        }else {
            tvLinea.setTextColor(getResources().getColor(R.color.purple_500));
            tvLinea.setText(Html.fromHtml("La <font color='#018786' ><b>" + result.toUpperCase() + "</b></font> " +
                    "tiene como ruta el destino " + destino.toUpperCase() + "."  ));
        }
    }
}
