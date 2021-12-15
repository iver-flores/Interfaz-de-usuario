package com.ivr.interfaz.fragment;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.metodixrefactor.utils.DoubleExtensionsKt;
import com.google.android.material.snackbar.Snackbar;
import com.ivr.interfaz.GlobalDatos;
import com.ivr.interfaz.MainActivity;
import com.ivr.interfaz.objeto.Conexion;
import com.ivr.interfaz.objeto.Validacion;
import com.ivr.interfaz.sql.SqliteDato;
import com.ivr.interfaz.R;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EsquinaFragment extends Fragment implements View.OnClickListener {

    View rootView;

    private TextView tvConexion, tvPasaje, tvCambio;
    private ImageView ivConexion, ivPasaje, ivCambio;
    private ImageButton ibCerrarConsejo, ibQr;
    private LinearLayout llConsejo, llQr, llParada;
    private LottieAnimationView laTimbre;

    public static final long PERIODO = 5000; // 5 segundos (5 * 1000 millisegundos)
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private String serial = "", ip = "", nombre = "";
    private SqliteDato mDatabase;

    private Handler handler;
    private RequestQueue requestQueue;

    private Validacion validacion;
    private Conexion conexion;

    private Bundle datosRecuperados;

    boolean estado = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_esquina, container, false);
        datosRecuperados = getArguments();
        if (datosRecuperados != null) {
            nombre = datosRecuperados.getString("dato");
            serial = datosRecuperados.getString("serial");
            ip     = datosRecuperados.getString("ip");

            ((GlobalDatos) requireContext().getApplicationContext()).setIdEsp(serial);

            if (nombre.equals("1")){
                estado = true;
            }else {
                estado = false;
            }
        }else {
            estado = false;
        }

        validacion = new Validacion();
        conexion = new Conexion();

        init(rootView);
        return rootView;
    }

    private void init(View v) {
        tvConexion = v.findViewById(R.id.tv_conexion);
        tvPasaje = v.findViewById(R.id.tv_pasaje);
        tvCambio = v.findViewById(R.id.tv_cambio);
        ivConexion = v.findViewById(R.id.iv_conexion);
        ivPasaje = v.findViewById(R.id.iv_pasaje);
        ivCambio = v.findViewById(R.id.iv_cambio);
        ibCerrarConsejo = v.findViewById(R.id.ib_cerrar_consejo);
        llConsejo = v.findViewById(R.id.ll_consejo);

        laTimbre = v.findViewById(R.id.la_timbre);
        ibQr = v.findViewById(R.id.ib_qr);
        ImageButton ibEsquina = v.findViewById(R.id.ib_esquina);
        ImageButton ibPasarela = v.findViewById(R.id.ib_pasarela);
        ImageButton ibMeQuedo = v.findViewById(R.id.ib_aqui);
        ImageButton ibAlFrente = v.findViewById(R.id.ib_al_frente);
        llQr = v.findViewById(R.id.ll_qr);
        llParada = v.findViewById(R.id.ll_paradas);

        ibQr.setOnClickListener(this);
        ibEsquina.setOnClickListener(this);
        ibPasarela.setOnClickListener(this);
        ibMeQuedo.setOnClickListener(this);
        ibAlFrente.setOnClickListener(this);

        ibCerrarConsejo.setOnClickListener(this);

        mDatabase = new SqliteDato(getActivity());
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_qr:
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (!((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                        LeerQrFragment fr=new LeerQrFragment();
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,fr)
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    requestCameraPermission();
                }
                break;
            case R.id.ib_esquina:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                   if (estado) activarBuzzer("ESQUINA");
                }
                break;
            case R.id.ib_pasarela:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (estado) activarBuzzer("PASARELA");
                }
                break;
            case R.id.ib_aqui:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (estado) activarBuzzer("AQUI");
                }
                break;
            case R.id.ib_al_frente:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (estado) activarBuzzer("AL FRENTE");
                }
                break;
            case R.id.ib_cerrar_consejo:
                llConsejo.setVisibility(View.GONE);
                break;
        }
    }

    private void timbreBien(){
        laTimbre.setVisibility(View.VISIBLE);
        laTimbre.setAnimation("bien.json");
        laTimbre.playAnimation();
        laTimbre.setRepeatCount(1);
        laTimbre.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:","end");
                laTimbre.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("Animation:","cancel");
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("Animation:","repeat");
            }
        });
    }

    private void timbreMal(){
        laTimbre.setVisibility(View.VISIBLE);
        laTimbre.setAnimation("mal.json");
        laTimbre.playAnimation();
        laTimbre.setRepeatCount(1);
        laTimbre.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:","end");
                laTimbre.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("Animation:","cancel");
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("Animation:","repeat");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (estado){
                    verificarConexion();
                }
                handler.postDelayed(this, PERIODO);
            }
        };
        handler.postDelayed(runnable, PERIODO);
    }

    public void verificarConexion(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial)) {
                    if (response.contains("Conexion = On")) {
                        if (response.contains("Pasaje = 1.50")) {
                            tvPasaje.setText(R.string.pasaje_1_50);
                            mDatabase.updatePasaje("Pasaje = 1.50 Bs");
                        }else if (response.contains("Pasaje = 1.80")){
                            tvPasaje.setText(R.string.pasaje_1_80);
                            mDatabase.updatePasaje("Pasaje = 1.80 Bs");
                        }else if (response.contains("Pasaje = 2.00")){
                            tvPasaje.setText(R.string.pasaje_2_00);
                            mDatabase.updatePasaje("Pasaje = 2.00 Bs");
                        }else if (response.contains("Pasaje = 2.20")){
                            tvPasaje.setText(R.string.pasaje_2_20);
                            mDatabase.updatePasaje("Pasaje = 2.20 Bs");
                        }else if (response.contains("Pasaje = 2.60")){
                            tvPasaje.setText(R.string.pasaje_2_60);
                            mDatabase.updatePasaje("Pasaje = 2.60 Bs");
                        }else if (response.contains("Pasaje = 2.80")){
                            tvPasaje.setText(R.string.pasaje_2_80);
                            mDatabase.updatePasaje("Pasaje = 2.80 Bs");
                        }
                        cambiarVistaSuperior((byte) 1);
                        ((GlobalDatos) requireContext().getApplicationContext()).setModo("usr");
                    } else {
                        cambiarVistaSuperior((byte) 0);
                        ((GlobalDatos) requireContext().getApplicationContext()).setModo("");
                    }
                } else {
                    cambiarVistaSuperior((byte) 0);
                    ((GlobalDatos) requireContext().getApplicationContext()).setModo("");
                }
            } catch (Exception e) {
                cambiarVistaSuperior( (byte)0);
                ((GlobalDatos) requireContext().getApplicationContext()).setModo("");
            }
        }, error -> {
            cambiarVistaSuperior((byte) 0);
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Conexion","On");
                params.put("idE", serial);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void activarBuzzer(String dato){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial)) {
                    if (response.contains("Buzzer = On")) {
                        timbreBien();
                    } else {
                        timbreMal();
                    }
                } else {
                    timbreMal();
                }
            } catch (Exception e) {
                timbreMal();
            }
        }, error -> {
            timbreMal();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Buzzer","On");
                params.put("idE", serial);
                if (((GlobalDatos) requireContext().getApplicationContext()).
                        getCambio().equals("Sin Datos")){
                    params.put("Cambio", "?");
                }else {
                    params.put("Cambio", ((GlobalDatos)requireContext().
                            getApplicationContext()).getCambio());
                }
                params.put("Funcion", dato);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void cambiarVistaSuperior(byte dato){
        if (dato == 1){
            ((GlobalDatos) requireContext().getApplicationContext()).setConexion(true);
            llQr.setVisibility(View.INVISIBLE);
            llParada.setVisibility(View.VISIBLE);
            ivConexion.setImageResource(R.drawable.ic_wifi_verde);
            tvConexion.setText(R.string.conectado);
            ivPasaje.setImageResource(R.drawable.ic_money_verde);
            if (((GlobalDatos) requireContext().getApplicationContext()).
                    getUsuario() == null || ((GlobalDatos) requireContext().
                    getApplicationContext()).getDinero() == null){
                tvCambio.setText("Sin cambio");
                ivCambio.setImageResource(R.drawable.ic_return_rojo);
                ((GlobalDatos) requireContext().getApplicationContext()).setCambio("Sin Datos");
            }else {
                float pasajeDB = mDatabase.leerPasaje();
                if (pasajeDB == -1){
                    tvCambio.setText(R.string.dinero_insuficiente);
                    ivCambio.setImageResource(R.drawable.ic_return_rojo);
                    ((GlobalDatos) requireContext().getApplicationContext()).setCambio("Sin Datos");
                }else {

                    float cambio = Float.parseFloat(((GlobalDatos) requireContext().
                            getApplicationContext()).getDinero()) - mDatabase.leerPasaje() *
                            Integer.parseInt(((GlobalDatos) requireContext().
                                    getApplicationContext()).getUsuario());
                    if (cambio < 0) {
                        tvCambio.setText(R.string.dinero_insuficiente);
                        ivCambio.setImageResource(R.drawable.ic_return_rojo);
                        ((GlobalDatos) requireContext().getApplicationContext()).setCambio("Sin Datos");
                    }else {
                        tvCambio.setText("Cambio " + DoubleExtensionsKt.Formato2Digitos(cambio) + " Bs");
                        ivCambio.setImageResource(R.drawable.ic_return_verde);
                        ((GlobalDatos) requireContext().getApplicationContext()).
                                setCambio(String.valueOf(DoubleExtensionsKt.Formato2Digitos(cambio)));
                    }
                }
            }
        }else {
            if (getContext() != null){
                ((GlobalDatos) requireContext().getApplicationContext()).setConexion(false);
                llQr.setVisibility(View.VISIBLE);
                llParada.setVisibility(View.INVISIBLE);
                tvConexion.setText(R.string.desconectado);
                ivConexion.setImageResource(R.drawable.ic_wifi_rojo);
                ivPasaje.setImageResource(R.drawable.ic_money_rojo);
                tvPasaje.setText(R.string.sin_pasaje);
                ivCambio.setImageResource(R.drawable.ic_return_rojo);
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) requireContext(),
                Manifest.permission.CAMERA)) {
            Snackbar.make(rootView.findViewById(android.R.id.content),
                    "Se requiere acceso a la cámara para mostrar la vista previa de la cámara.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", view ->
                    ActivityCompat.requestPermissions((Activity) requireContext(), new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA)).show();
        } else {
            Snackbar.make(rootView.findViewById(android.R.id.content),
                    "El permiso no está disponible. Solicitando permiso de cámara.",
                    Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((Activity) requireContext(), new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
            //((MainActivity) requireContext()).removerSSID();
        }
    }

}
