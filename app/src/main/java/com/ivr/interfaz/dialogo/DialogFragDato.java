package com.ivr.interfaz.dialogo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.ivr.interfaz.adapter.AdaptadorDato;
import com.ivr.interfaz.entidades.ListaDatos;
import com.ivr.interfaz.fragment.EsquinaFragment;
import com.ivr.interfaz.GlobalDatos;
import com.ivr.interfaz.sql.SqliteDato;
import com.ivr.interfaz.R;

import java.util.ArrayList;

public class DialogFragDato extends DialogFragment implements View.OnClickListener {

    AlertDialog.Builder builder;
    AdaptadorDato adapter;
    ListView lvDatos;
    private ImageButton ibBorrar, ibAtras, ibCerAd;
    private LinearLayout llCerAdv;

    private SqliteDato mDatabase;

    public DialogFragDato(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearDialogoFrag();
    }

    private AlertDialog crearDialogoFrag() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (requireActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.frag_dialog_informacion, null);
        builder.setView(v);

        lvDatos = v.findViewById(R.id.lv_datos);

        mDatabase = new SqliteDato(getContext());
        ArrayList<ListaDatos> allDispositivos = mDatabase.listarDatos();

        if(allDispositivos.size() > 0){
            adapter = new AdaptadorDato(requireActivity(), allDispositivos);
            lvDatos.setAdapter(adapter);
            lvDatos.setSelection(adapter.getCount() - 1);
        }

        init(v);

        return builder.create();
    }

    private void init(View v) {
        ibBorrar = v.findViewById(R.id.ib_borrar);
        ibAtras = v.findViewById(R.id.ib_atras);
        ibCerAd = v.findViewById(R.id.ib_cerrar_adver);
        llCerAdv = v.findViewById(R.id.ll_adver);

        ibBorrar.setOnClickListener(this);
        ibAtras.setOnClickListener(this);
        ibCerAd.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ib_borrar:
                mDatabase.borrarDatos();
                ((GlobalDatos) requireContext().getApplicationContext()).setConexion(false);
                ((GlobalDatos) requireContext().getApplicationContext()).setIdEsp(null);
                EsquinaFragment fr = new EsquinaFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fr)
                        .addToBackStack(null)
                        .commit();
                dismiss();
                break;
            case R.id.ib_atras:
                dismiss();
                break;
            case R.id.ib_cerrar_adver:
                llCerAdv.setVisibility(View.INVISIBLE);
                break;
        }


    }
}
