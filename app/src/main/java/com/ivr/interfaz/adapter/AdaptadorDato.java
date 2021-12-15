package com.ivr.interfaz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ivr.interfaz.entidades.ListaDatos;
import com.ivr.interfaz.R;

import java.util.ArrayList;

public class AdaptadorDato extends BaseAdapter {
    //Propiedades
    ArrayList<ListaDatos> listarDatos;
    Context context;
    LayoutInflater inflater;

    //Constructor
    public AdaptadorDato(Context context, ArrayList<ListaDatos> listarDatos) {

        this.listarDatos = listarDatos;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Base Adapter

    @Override
    public int getCount() {
        return listarDatos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    //Holder para crear View de cada item de la lista

    static class Holder {
        //Propiedades
        TextView tv1, tv2, tv3, tv4;
        ImageButton ibt;

    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.item_datos_listview, null);
        Holder holder = new Holder();

        //Init item_customlistivew
        holder.tv1 =  rowView.findViewById(R.id.tv_pasaje);
        holder.tv2 =  rowView.findViewById(R.id.tv_nombre);
        holder.tv3 =  rowView.findViewById(R.id.tv_grupo);
        holder.tv4 =  rowView.findViewById(R.id.tv_hora);
        holder.ibt =  rowView.findViewById(R.id.ibt_telefono);

        holder.tv1.setText(listarDatos.get(position).getPasaje());
        holder.tv2.setText(listarDatos.get(position).getNombre());
        holder.tv3.setText(listarDatos.get(position).getGrupo());
        holder.tv4.setText(listarDatos.get(position).getHora());

        holder.ibt.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_DIAL,
                    Uri.parse(String.format("tel:+591%s", listarDatos.get(position).getTelefono())));
            context.startActivity(i);
        });

        return rowView;
    }
}
