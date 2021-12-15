package com.ivr.interfaz.dialogo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ivr.interfaz.R;

public class DialogFragAyuda extends DialogFragment implements View.OnClickListener {

    private ImageButton ibUno, ibDos, ibTres, ibCuatro, ibCinco, ibAtras;
    private ImageView   ivUno, ivDos;

    AlertDialog.Builder builder;

    public DialogFragAyuda(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearDialogoFrag();
    }

    private AlertDialog crearDialogoFrag() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (requireActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.frag_dialog_ayuda, null);
        builder.setView(v);

        init(v);

        ivUno.setImageResource(R.drawable.ic_uno);
        ivDos.setImageResource(R.drawable.ic_dos);

        return builder.create();
    }

    private void init(View v) {
        ibUno = v.findViewById(R.id.ib_uno);
        ibDos = v.findViewById(R.id.ib_dos);
        ibTres = v.findViewById(R.id.ib_tres);
        ibCuatro = v.findViewById(R.id.ib_cuatro);
        ibCinco = v.findViewById(R.id.ib_cinco);
        ibAtras = v.findViewById(R.id.ib_atras);

        ivUno = v.findViewById(R.id.iv_img1);
        ivDos = v.findViewById(R.id.iv_img2);

        ibUno.setOnClickListener(this);
        ibDos.setOnClickListener(this);
        ibTres.setOnClickListener(this);
        ibCuatro.setOnClickListener(this);
        ibCinco.setOnClickListener(this);
        ibAtras.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        ivUno.setImageResource(0);
        ivDos.setImageResource(0);
        switch (v.getId()){
            case R.id.ib_uno:
                ivUno.setImageResource(R.drawable.ic_uno);
                ivDos.setVisibility(View.VISIBLE);
                ivDos.setImageResource(R.drawable.ic_dos);
                break;
            case R.id.ib_dos:
                ivUno.setImageResource(R.drawable.ic_tres);
                ivDos.setImageResource(R.drawable.ic_cuatro);
                break;
            case R.id.ib_tres:
                ivUno.setImageResource(R.drawable.ic_cinco);
                ivDos.setImageResource(R.drawable.ic_seis);
                break;
            case R.id.ib_cuatro:
                ivUno.setImageResource(R.drawable.ic_siete);
                ivDos.setImageResource(R.drawable.ic_nueve);
                break;
            case R.id.ib_cinco:
                ivUno.setImageResource(R.drawable.ic_ocho);
                ivDos.setVisibility(View.INVISIBLE);
                break;
            case R.id.ib_atras:
                dismiss();
                break;
        }


    }
}
