package com.ivr.interfaz.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.ivr.interfaz.GlobalDatos;
import com.ivr.interfaz.MainActivity;
import com.ivr.interfaz.R;
import com.ivr.interfaz.objeto.Conexion;
import com.ivr.interfaz.objeto.Validacion;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    View rootView;

    private TextView tvConexion, tvPasaje;
    private ImageView ivConexion, ivPasaje;
    private TextInputLayout tilIp, tilMascara, tilGateway, tilNombre, tilTelefono, tilGrupo,
            tilSsid, tilPassword;
    private EditText etIp, etMascara, etGateway, etNombre, etTelefono, etGrupo,
            etSsid, etPassword;
    private Spinner spPasaje;

    private Button btnVer, btnGuardar, btnGenerarQr, btnReiniciar, btnGuardarPasaje;

    private LottieAnimationView laRespuesta;

    public static final long PERIODO = 5000; // 5 segundos (5 * 1000 millisegundos)

    private String serial = "", ip = "", nombre = "", pasaje = "";
    private String eIp, eMascara, eGateway, eNombre, eTelefono, eGrupo, eSsid, ePassword;
    private String[] pasajes = {"Seleccione pasaje:", "1.50 - 1.80 Bs", "2.00 - 2.20 Bs", "2.60 - 2.80 Bs"};
    private boolean estado = false, verificacionDatos = false;
    private List<String> listaPasaje;

    private Handler handler;
    private RequestQueue requestQueue;

    private Validacion validacion;
    private Conexion conexion;
    private Bitmap myBitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_configuracion, container, false);

        Bundle datosRecuperados = getArguments();
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
        entrada();

        return rootView;
    }

    private void init(View v) {
        tvConexion = v.findViewById(R.id.tv_conexion);
        tvPasaje = v.findViewById(R.id.tv_pasaje);
        ivConexion = v.findViewById(R.id.iv_conexion);
        ivPasaje = v.findViewById(R.id.iv_pasaje);
        tilIp = v.findViewById(R.id.til_ip);
        tilMascara = v.findViewById(R.id.til_mascara);
        tilGateway = v.findViewById(R.id.til_gateway);
        tilNombre = v.findViewById(R.id.til_nombre);
        tilTelefono = v.findViewById(R.id.til_telefono);
        tilGrupo = v.findViewById(R.id.til_grupo);
        tilSsid = v.findViewById(R.id.til_ssid);
        tilPassword = v.findViewById(R.id.til_password);
        etIp = v.findViewById(R.id.et_ip);
        etMascara = v.findViewById(R.id.et_mascara);
        etGateway = v.findViewById(R.id.et_gateway);
        etNombre = v.findViewById(R.id.et_nombre);
        etTelefono = v.findViewById(R.id.et_telefono);
        etGrupo = v.findViewById(R.id.et_grupo);
        etSsid = v.findViewById(R.id.et_ssid);
        etPassword = v.findViewById(R.id.et_password);

        spPasaje =  v.findViewById(R.id.spn_pasaje);

        btnVer = v.findViewById(R.id.btn_ver);
        btnGuardar = v.findViewById(R.id.btn_guardar);
        btnGenerarQr = v.findViewById(R.id.btn_generar_qr);
        btnReiniciar = v.findViewById(R.id.btn_reiniciar);
        btnGuardarPasaje = v.findViewById(R.id.btn_guardar_pasaje);

        laRespuesta = v.findViewById(R.id.la_resultado);

        btnVer.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnGenerarQr.setOnClickListener(this);
        btnReiniciar.setOnClickListener(this);
        btnGuardarPasaje.setOnClickListener(this);

        spPasaje.setOnItemSelectedListener(this);

        listaPasaje = new ArrayList<>();
        Collections.addAll(listaPasaje, pasajes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.search, R.id.tv_search, listaPasaje);
        spPasaje.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ver:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    verDatos();
                }
                break;
            case R.id.btn_guardar:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (verificarDatos()){
                        guardarDatos();
                    }
                }
                break;
            case R.id.btn_generar_qr:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (verificarDatos()){

                        String sacaId = serial.trim() ;
                        String sacNom = etNombre.getText().toString().trim();
                        String sacTel = etTelefono.getText().toString().trim();
                        String sacGru = etGrupo.getText().toString().trim();
                        String sacSsi = etSsid.getText().toString().trim();
                        String sacPas = etPassword.getText().toString().trim();
                        String sacaIp = etIp.getText().toString().trim();

                        String qr = sacaId 	  + "XWw"    +
                                    sacNom 	  + "udp"    +
                                    sacTel    + "0dell"  +
                                    sacGru 	  + "1089"   +
                                    sacSsi 	  + "hftth"  +
                                    sacPas    + "sj4"    +
                                    sacaIp    + "okyes";

                        String replace = qr.trim().replaceAll("\n", "").replace(" ", "$");
                        myBitmap = QRCode.from(replace).bitmap();
                        saveImage(myBitmap);
                    }
                }
                break;
            case R.id.btn_reiniciar:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    reiniciar();
                }
                break;
            case R.id.btn_guardar_pasaje:
                if (((GlobalDatos) requireContext().getApplicationContext()).isConexion()) {
                    if (pasaje.equals("Seleccione pasaje:")){
                        mal();
                    }else {
                        dialogoDescargarQR();
                    }
                }
                break;
        }
    }

    private void entrada() {
        etIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esIpValido(String.valueOf(s))){
                    tilIp.setError("Ip inválido");
                }else {
                    tilIp.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etMascara.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esIpValido(String.valueOf(s))){
                    tilMascara.setError("Mascara inválido");
                }else {
                    tilMascara.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etGateway.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esIpValido(String.valueOf(s))){
                    tilGateway.setError("Gateway inválido");
                }else {
                    tilGateway.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esTextoValido(String.valueOf(s))){
                    tilNombre.setError("Nombre inválido");
                }else {
                    tilNombre.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etTelefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esTelefonoValido(String.valueOf(s))){
                    tilTelefono.setError("Telefono inválido");
                }else {
                    tilTelefono.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etGrupo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esTextoValido(String.valueOf(s))){
                    tilGrupo.setError("Grupo inválido");
                }else {
                    tilGrupo.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etSsid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esSsidValido(String.valueOf(s))){
                    tilSsid.setError("Ssid inválido");
                }else {
                    tilSsid.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esPasswordValido(String.valueOf(s))){
                    tilPassword.setError("Password inválido");
                }else {
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private boolean verificarDatos(){
        eIp = Objects.requireNonNull(tilIp.getEditText()).getText().toString();
        eMascara = Objects.requireNonNull(tilMascara.getEditText()).getText().toString();
        eGateway = Objects.requireNonNull(tilGateway.getEditText()).getText().toString();
        eNombre = Objects.requireNonNull(tilNombre.getEditText()).getText().toString();
        eTelefono = Objects.requireNonNull(tilTelefono.getEditText()).getText().toString();
        eGrupo = Objects.requireNonNull(tilGrupo.getEditText()).getText().toString();
        eSsid = Objects.requireNonNull(tilSsid.getEditText()).getText().toString();
        ePassword = Objects.requireNonNull(tilPassword.getEditText()).getText().toString();

        if (!eIp.equals("") || !eMascara.equals("") || !eGateway.equals("") || !eNombre.equals("")
                || !eTelefono.equals("") || !eGrupo.equals("") || !eSsid.equals("")
                || !ePassword.equals("")) {

            boolean a = validacion.esIpValido(eIp);
            boolean b = validacion.esIpValido(eMascara);
            boolean c = validacion.esIpValido(eGateway);
            boolean d = validacion.esTextoValido(eNombre);
            boolean e = validacion.esTelefonoValido(eTelefono);
            boolean f = validacion.esTextoValido(eGrupo);
            boolean g = validacion.esSsidValido(eSsid);
            boolean h = validacion.esPasswordValido(ePassword);

            if (a && b && c && d && e && f && g && h) {
                return true;
            } else {
                eIp = eMascara = eGateway = eNombre = eTelefono = eGrupo = eSsid = ePassword = "";
                return false;
            }
        } else {
            eIp = eMascara = eGateway = eNombre = eTelefono = eGrupo = eSsid = ePassword = "";
            return false;
        }
    }

    public void verificarConexion(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial)) {
                    if (response.contains("Conexion = On")) {
                        if (response.contains("Pasaje = 1.50")) {
                            tvPasaje.setText(R.string.pasaje_150_180);
                        }else if (response.contains("Pasaje = 2.00")){
                            tvPasaje.setText(R.string.pasaje_200_220);
                        }else if (response.contains("Pasaje = 2.60")){
                            tvPasaje.setText(R.string.pasaje_260_280);
                        }else {
                            tvPasaje.setText(R.string.sin_datos);
                        }
                        cambiarVistaSuperior((byte) 1);
                        ((GlobalDatos) requireContext().getApplicationContext()).setModo("pro");
                    } else {
                        cambiarVistaSuperior((byte) 0);
                    }
                } else {
                    cambiarVistaSuperior((byte) 0);
                }
            } catch (Exception e) {
                cambiarVistaSuperior( (byte)0);
            }
        }, error -> cambiarVistaSuperior((byte) 0)){
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

    private void guardarDatos(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial) && response.contains("Configuracion = Guardada")) {
                    bien();
                } else {
                    mal();
                }
            } catch (Exception e) {
                mal();
            }
        }, error -> {
            mal();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Configuracion","Guardar");
                params.put("idE", serial);
                params.put("ip", eIp);
                params.put("mascara", eMascara);
                params.put("gateway", eGateway);
                params.put("ssid", eSsid);
                params.put("password", ePassword);
                params.put("telefono", eTelefono);
                params.put("grupo", eGrupo);
                params.put("nombre", eNombre);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void guardarPasaje(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial) && response.contains("Configuracion = Pasaje")) {
                    bien();
                } else {
                    mal();
                }
            } catch (Exception e) {
                mal();
            }
        }, error -> {
            mal();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Configuracion","Pasaje");
                params.put("idE", serial);
                params.put("Pasaje", pasaje.replace(" ", "").substring(0,9) + "1");

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void verDatos(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial)/* && response.contains("Configuracion = Ver")*/) {

                    etIp.setText(response.substring(response.indexOf("XWw") + 8,
                            response.indexOf("udp")));
                    etMascara.setText(response.substring(response.indexOf("udp") + 13,
                            response.indexOf("0dell")));
                    etGateway.setText(response.substring(response.indexOf("0dell") + 15,
                            response.indexOf("1089")));
                    etSsid.setText(response.substring(response.indexOf("1089") + 11,
                            response.indexOf("hftth")));
                    etPassword.setText(response.substring(response.indexOf("hftth") + 16,
                            response.indexOf("sj4")));
                    etTelefono.setText(response.substring(response.indexOf("sj4") + 14,
                            response.indexOf("okyes")));
                    etGrupo.setText((response.substring(response.indexOf("okyes") + 13,
                            response.indexOf("skyw"))).replace("Ã±", "ñ"));
                    etNombre.setText((response.substring(response.indexOf("skyw") + 13,
                            response.indexOf("84iv"))).replace("Ã±", "ñ"));

                    bien();
                } else {
                    mal();
                }
            } catch (Exception e) {
                mal();
            }
        }, error -> {
            mal();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Configuracion","Ver");
                params.put("idE", serial);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void reiniciar(){
        String url = "http://" + ip + "/" + serial;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response.contains("idE = " + serial) &&
                        response.contains("Configuracion = Reiniciar")) {
                    bien();
                } else {
                    mal();
                }
            } catch (Exception e) {
                mal();
            }
        }, error -> {
            mal();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params= new HashMap<>();
                params.put("Configuracion","Reiniciar");
                params.put("idE", serial);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void cambiarVistaSuperior(byte dato){
        if (dato == 1){
            ((GlobalDatos) requireContext().getApplicationContext()).setConexion(true);
            ivConexion.setImageResource(R.drawable.ic_wifi_verde);
            ivPasaje.setImageResource(R.drawable.ic_money_verde);
            tvConexion.setText(R.string.conectado);
        }else {
            if (getContext() != null){
                ((GlobalDatos) requireContext().getApplicationContext()).setConexion(false);
                tvConexion.setText(R.string.desconectado);
                ivConexion.setImageResource(R.drawable.ic_wifi_rojo);
                ivPasaje.setImageResource(R.drawable.ic_money_rojo);
                tvPasaje.setText(R.string.sin_pasaje);
            }
        }
    }

    private void bien(){
        laRespuesta.setVisibility(View.VISIBLE);
        laRespuesta.setAnimation("bien.json");
        laRespuesta.playAnimation();
        laRespuesta.setRepeatCount(1);
        laRespuesta.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:","end");
                laRespuesta.setVisibility(View.INVISIBLE);
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

    private void mal(){
        laRespuesta.setVisibility(View.VISIBLE);
        laRespuesta.setAnimation("mal.json");
        laRespuesta.playAnimation();
        laRespuesta.setRepeatCount(1);
        laRespuesta.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:","end");
                laRespuesta.setVisibility(View.INVISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireContext()).removerSSID();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spn_pasaje) {
            pasaje = spPasaje.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void saveImage(Bitmap bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, getContext().getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContext().getContentResolver().update(uri, values, null, null);
                    Toast.makeText(getContext(), "Se guardo la imagen correctamente",Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error",Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name));
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                bien();
                Toast.makeText(getContext(), "Se guardo la imagen correctamente",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mal();
                Toast.makeText(getContext(), "Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                Bitmap newBitmmap;
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scaleWidth = ((float) 300) / width;
                float scaleHeight = ((float) 300) / height;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                newBitmmap = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, false);
                newBitmmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dialogoDescargarQR(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Descargar Imagen");
        builder.setMessage("¿Esta seguro de descargar la imagen QR para acceder servicio?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (myBitmap != null) {
                    guardarPasaje();
                }
            }
        }).setNegativeButton("Cancelar", null);
        builder.show();
    }
}
