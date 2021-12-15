package com.ivr.interfaz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ivr.interfaz.dialogo.DialogFragAyuda;
import com.ivr.interfaz.dialogo.DialogFragDato;
import com.ivr.interfaz.dialogo.DialogFragPasaje;
import com.ivr.interfaz.dialogo.DialogFragRegistro;
import com.ivr.interfaz.dialogo.DialogFragRuta;
import com.ivr.interfaz.fragment.ConfiguracionFragment;
import com.ivr.interfaz.fragment.EsquinaFragment;
import com.ivr.interfaz.objeto.Conexion;
import com.ivr.interfaz.utils.PreferenceUtil;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static long  INTERVALO = 0;

    private Conexion conexion;

    WifiManager wifiManager;
    ConnectivityManager mConnectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((GlobalDatos) this.getApplicationContext()).setModo("");

        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        conexion = new Conexion();

        setInitialFragment();

        requestCameraPermission();

        init();

        if (!conexion.verEstadoWifi(this)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivityForResult(panelIntent, 0);
            } else {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                conexion.habilitarWifi(this);
            }
        }

        if (Objects.equals(PreferenceUtil.INSTANCE.getResultIdE(this), "")){
            PreferenceUtil.INSTANCE.saveResultIdE(this,"3I4V10ER-9I2V54ER-0I8V99ER-" +
                    "9I3V03ER-6I7V24ER-2I5V65ER-I4V10ER-4I7V00ER-6I0V04ER-2I8V47ER-7I2V20ER-5I3V60ER-"
                    + "4I0V53ER-3I4V10ER-5I2V44ER");
        }
    }

    private void init() {

        TextView tv_ubicacion = findViewById(R.id.tv_ubicacion);
        TextView tv_informacion = findViewById(R.id.tv_informacion);
        TextView tv_registro = findViewById(R.id.tv_registro);
        TextView tv_pasaje = findViewById(R.id.tv_pasaje);
        TextView tv_ayuda = findViewById(R.id.tv_ayuda);
        TextView tv_salir = findViewById(R.id.tv_salir);

        tv_ubicacion.setOnClickListener(this);
        tv_informacion.setOnClickListener(this);
        tv_pasaje.setOnClickListener(this);
        tv_registro.setOnClickListener(this);
        tv_ayuda.setOnClickListener(this);
        tv_salir.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setInitialFragment() {
        Fragment fragment;
        fragment = new EsquinaFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_ubicacion:
                DialogFragRuta dialogFragRuta = new DialogFragRuta();
                assert getFragmentManager() != null;
                dialogFragRuta.show(getSupportFragmentManager(), "DialogFragUbicacion");
                break;
            case R.id.tv_informacion:
                DialogFragDato dialogFragDato = new DialogFragDato();
                assert getFragmentManager() != null;
                dialogFragDato.show(getSupportFragmentManager(), "DialogFragInformacion");
                break;
            case R.id.tv_pasaje:
                if(((GlobalDatos)this.getApplication()).isConexion()) {
                    DialogFragPasaje dialogFragPasaje = new DialogFragPasaje();
                    assert getFragmentManager() != null;
                    dialogFragPasaje.show(getSupportFragmentManager(), "DialogoFragPasaje");
                }else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Sin Conexion",
                            Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.tv_registro:

                if (((GlobalDatos) this.getApplicationContext()).getIdEsp() != null) {
                    if (((GlobalDatos) this.getApplicationContext()).getModo().equals("usr")) {
                        dialogoRegistro();
                    } else if (((GlobalDatos) this.getApplicationContext()).getModo().equals("pro")) {
                        dialogoRegistro();
                    }

                }else {
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.escanee_codigo, Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.tv_ayuda:
                DialogFragAyuda dialogFragAyuda = new DialogFragAyuda();
                assert getFragmentManager() != null;
                dialogFragAyuda.show(getSupportFragmentManager(), "dialogFragAyuda");
                break;

            case R.id.tv_salir:
                if(networkCallback != null){
                    removerSSID();
                }
                finishAffinity();
                break;
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Se requiere acceso a la cámara para mostrar la vista previa de la cámara.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", view ->
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA)).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Solicitando permiso de cámara.",
                    Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private  boolean requeststoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Se requiere acceso a la memoria",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", view ->
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)).show();
            return false;
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Solicitando permiso de memoria.",
                    Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return true;
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSION_REQUEST_CAMERA:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Snackbar.make(findViewById(android.R.id.content),
                            "Se concedió el permiso de la cámara.", Snackbar.LENGTH_LONG).show();*/
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Se rechazó la solicitud de permiso de la cámara.", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Snackbar.make(findViewById(android.R.id.content),
                            "Se concedió el permiso de la memoria.", Snackbar.LENGTH_LONG).show();*/
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Se rechazó la solicitud de permiso de la memoria.", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            default:
        }
    }

    private void dialogoRegistro(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Se requiere internet");
        builder.setMessage("Por favor conectese a un plan de datos para ultilizar esta funcion");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //api 29
                    if (networkCallback !=null){
                        removerSSID();
                    }
                }else {
                    removerSSID();
                }
                if (conexion.executeCommand()){
                    DialogFragRegistro dialogFragRegistro = new DialogFragRegistro();
                    assert getFragmentManager() != null;
                    dialogFragRegistro.show(getSupportFragmentManager(), "dialogFragRegistro");
                }else {
                    builder.setCancelable(true);
                }
            }
        }).setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - INTERVALO > 2000) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Presione Atrás nuevamente para salir.", Snackbar.LENGTH_SHORT).show();
            INTERVALO = System.currentTimeMillis();
        } else {
            if(networkCallback != null){
                removerSSID();
            }
            finish();
            super.onBackPressed();
        }
    }

    public void conexionEsp(String ssid, String password){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //api 29

            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
            builder.setSsid(ssid);
            builder.setWpa2Passphrase(password);

            WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
            NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
            NetworkRequest networkRequest = networkRequestBuilder.build();
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            networkCallback = new
                    ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            super.onAvailable(network);
                            cm.bindProcessToNetwork(network);
                        }
                    };
            cm.requestNetwork(networkRequest, networkCallback);
        } else {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", ssid);
            wifiConfig.preSharedKey = String.format("\"%s\"", password);
            ((GlobalDatos)getApplicationContext()).setNetId(wifiManager.addNetwork(wifiConfig));
            wifiManager.disconnect();
            wifiManager.enableNetwork(((GlobalDatos)getApplicationContext()).getNetId(), true);
            wifiManager.reconnect();
        }
    }

    public void removerSSID(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ((GlobalDatos)getApplicationContext()).setConexion(false);
            mConnectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        } else{
            ((GlobalDatos)getApplicationContext()).setConexion(wifiManager.
                    removeNetwork(((GlobalDatos)getApplicationContext()).getNetId()) && wifiManager.saveConfiguration());
        }
    }
}