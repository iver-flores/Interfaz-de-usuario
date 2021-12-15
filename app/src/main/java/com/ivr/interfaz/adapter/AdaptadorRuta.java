package com.ivr.interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ivr.interfaz.entidades.Ruta;
import com.ivr.interfaz.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorRuta extends ArrayAdapter<Ruta> {

    Context context;
    int resource, textViewResourceId;
    List<Ruta> items, tempItems, suggestions;

    public AdaptadorRuta(Context context, int resource, int textViewResourceId, List<Ruta> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search, parent, false);
        }
        Ruta ruta = items.get(position);
        if (ruta != null) {
            TextView tvRuta = view.findViewById(R.id.tv_search);
            if (tvRuta != null)
                tvRuta.setText(ruta.getRuta());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Ruta) resultValue).getRuta();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Ruta ruta : tempItems) {
                    if (ruta.getRuta().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(ruta);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Ruta> filterList = (ArrayList<Ruta>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Ruta ruta : filterList) {
                    add(ruta);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
