package com.ivr.interfaz.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.material.snackbar.Snackbar;
import com.ivr.interfaz.MainActivity;
import com.ivr.interfaz.objeto.Conexion;
import com.ivr.interfaz.objeto.Validacion;
import com.ivr.interfaz.R;
import com.ivr.interfaz.vista.SuperposiciónPuntos;

import java.util.Objects;


public class LeerQrFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        QRCodeReaderView.OnQRCodeReadListener, View.OnClickListener{

    View rootView;

    private ViewGroup contenedor;
    private QRCodeReaderView qrCodeReaderView;
    private SuperposiciónPuntos pointsOverlayView;
    private ImageView iv_linterna;

    private boolean estadoLinterna = false;

    private Bundle args;

    private Validacion validacion;
    private Conexion conexion;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_main, container, false);
        args = new Bundle();
        validacion = new Validacion();
        conexion = new Conexion();
        init(rootView);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        }
        validacion = new Validacion();
        conexion = new Conexion();
        return rootView;
    }

    private void init(View v) {
        contenedor = v.findViewById(R.id.fragment_container);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override public void onQRCodeRead(String text, PointF[] points) {
        String[] dato = validacion.sacarDatos(text, getContext());
        if (!dato[0].equals("") || !dato[1].equals("") || !dato[2].equals("") || !dato[4].equals("")){
            String serial = dato[2];
            String ip = dato[3];
            String nombre = dato[4];

            ((MainActivity) requireContext()).conexionEsp(dato[0], dato[1]);

            args.putString("dato", "1");
            args.putString("serial", serial);
            args.putString("ip", ip);

            Fragment fragment;
            if (nombre.equals("iverflores6@gmail.com")){
                fragment = new ConfiguracionFragment();
            }else {
                fragment = new EsquinaFragment();
            }
            cambiarFragment(fragment);
        }else {
            Snackbar.make(rootView.findViewById(android.R.id.content),
                    "Código Erróneo.",
                    Snackbar.LENGTH_LONG).show();
        }

        pointsOverlayView.setPoints(points);
    }

    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.fragment_leer_qr,
                contenedor, true);
        qrCodeReaderView =  content.findViewById(R.id.qrdecoderview);
        iv_linterna = content.findViewById(R.id.iv_linterna);
        pointsOverlayView =  content.findViewById(R.id.points_overlay_view);

        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        iv_linterna.setOnClickListener(this);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_linterna){
            if (estadoLinterna = !estadoLinterna){
                qrCodeReaderView.setTorchEnabled(true);
                iv_linterna.setImageResource(R.drawable.ic_linternaon);
            }else {
                qrCodeReaderView.setTorchEnabled(false);
                iv_linterna.setImageResource(R.drawable.ic_linternaoff);
            }
        }
    }

    private void cambiarFragment(Fragment fragment){
        fragment.setArguments(args);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
