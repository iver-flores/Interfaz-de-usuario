package com.ivr.interfaz.dialogo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.metodixrefactor.utils.DoubleExtensionsKt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ivr.interfaz.GlobalDatos;
import com.ivr.interfaz.sql.SqliteDato;
import com.ivr.interfaz.objeto.Validacion;
import com.ivr.interfaz.R;
import com.ivr.interfaz.utils.PreferenceUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class DialogFragPasaje extends DialogFragment implements View.OnClickListener{

    AlertDialog.Builder builder;

    private TextInputLayout tilInteg, tilMonto;
    private EditText etInteg, etMonto;
    private TextView tvMensaje, tvPagar;

    private float cambio = 0;
    private boolean resultado = false;

    private SqliteDato mDatabase;

    private String integ, monto, serial = "";
    private int contador = 0;
    private float pasajedb;

    private Validacion validacion;
    private DatabaseReference databaseReference;
    private Uri url;

    Bitmap myBitmap;

    public DialogFragPasaje(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return crearDialogoFrag();
    }

    private AlertDialog crearDialogoFrag() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (requireActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.frag_dialog_pasaje, null);
        builder.setView(v);
        validacion = new Validacion();
        serial = ((GlobalDatos) requireContext().getApplicationContext()).getIdEsp();
        init(v);
        pasajedb = mDatabase.leerPasaje();

        /*if (PreferenceUtil.INSTANCE.getResultRegistro(requireContext())){
            databaseReference = FirebaseDatabase.getInstance().getReference();
            if (pasajedb != -1) {
                if (pasajedb == 2.00){
                    databaseReference.child(serial).child("Url1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                            } else {
                                contador = 1;
                                url = Uri.parse(String.valueOf(task.getResult().getValue()));
                                verImage(url);
                            }
                        }
                    });
                }else if (pasajedb == 2.20){
                    databaseReference.child(serial).child("Url2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                            } else {
                                contador = 2;
                                url = Uri.parse(String.valueOf(task.getResult().getValue()));
                                verImage(url);
                            }
                        }
                    });
                }
            }

        }else {
        }*/
        entrada();
        return builder.create();
    }

    private void init( View v) {
        tilInteg = v.findViewById(R.id.til_integrantes);
        tilMonto = v.findViewById(R.id.til_monto);
        etInteg = v.findViewById(R.id.et_integrantes);
        etMonto = v.findViewById(R.id.et_monto);
        tvMensaje = v.findViewById(R.id.tv_mensaje);
        tvPagar = v.findViewById(R.id.tv_qr_pagar);
        Button btnAceptar = v.findViewById(R.id.btn_aceptar);
        ImageButton ibAtras = v.findViewById(R.id.ib_atras);

        btnAceptar.setOnClickListener(this);

        ibAtras.setOnClickListener(this);
        mDatabase = new SqliteDato(getActivity());

    }

    /*private void verImage(Uri photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        if (contador == 2){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ibPagar);
            tvPagar.setText(R.string.pasaje_2_20);
        } else if (contador == 1){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ibPagar);
            tvPagar.setText(R.string.pasaje_2_00);
        }

    }*/

    private void entrada() {
        etInteg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esNumeroValido(String.valueOf(s))){
                    tilInteg.setError("Numero inválido");
                    tvMensaje.setText("");
                }else {
                    tilInteg.setErrorEnabled(false);
                    if (etMonto.getText().length() > 0){
                        verificarCambio();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etMonto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validacion.esDecimalValido(String.valueOf(s))){
                    tilMonto.setError("Numero inválido");
                    tvMensaje.setText("");
                }else {
                    tilMonto.setErrorEnabled(false);
                    verificarCambio();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_aceptar:
                if (resultado){
                    ((GlobalDatos) requireContext().getApplicationContext()).setUsuario(integ);
                    ((GlobalDatos) requireContext().getApplicationContext()).setDinero(monto);
                }else {
                    ((GlobalDatos) requireContext().getApplicationContext()).setCambio("Cambio sin datos");
                }
                dismiss();
                break;
            case R.id.ib_atras:
                dismiss();
                break;
            case R.id.ib_qr_pagar:
                /*loadimage();
                saveImage(myBitmap);*/

                break;
        }
    }

    /*public void loadimage() {
        class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
            private String url;
            public ImageLoadTask(String url) {
                this.url = url;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL connection = new URL(url);
                    InputStream input = connection.openStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 1000, 400, true);
                    return resized;
                } catch (Exception e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
            }
        }
        ImageLoadTask obj=new ImageLoadTask("https://firebasestorage.googleapis.com/v0/b/interfaz-f129e.appspot.com/o/3I4V10ER%2FQR%2Fqr1?alt=media&token=7a7be567-e706-4032-b774-5bddf56a32c5");
        obj.execute();
    }*/

    /*private void saveImage(Bitmap bitmap) {
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
                Toast.makeText(getContext(), "Se guardo la imagen correctamente",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error",Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    /*private ContentValues contentValues() {
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void verificarCambio(){
        integ = Objects.requireNonNull(tilInteg.getEditText()).getText().toString();
        monto = Objects.requireNonNull(tilMonto.getEditText()).getText().toString();

        if (!integ.equals("") || !monto.equals("")) {
            boolean a = validacion.esNumeroValido(integ);
            boolean b = validacion.esDecimalValido(monto);
            if (a && b) {
                if (pasajedb == -1){
                    tvMensaje.setText(R.string.dinero_insuficiente);
                    tvMensaje.setTextColor(Color.RED);
                    resultado = false;
                }else {
                    cambio = Float.parseFloat(monto) - pasajedb * Integer.parseInt(integ);
                    if (cambio < 0) {
                        tvMensaje.setText(R.string.dinero_insuficiente);
                        tvMensaje.setTextColor(getResources().getColor(R.color.orange));
                        resultado = false;
                    } else {
                        if (Float.parseFloat(monto) == 50){
                            tvMensaje.setText("SU CAMBIO ES " + DoubleExtensionsKt.Formato2Digitos(cambio) +
                                    " Bs, es muy probable que el conductor no cuente con cambio" );
                            tvMensaje.setTextColor(Color.YELLOW);
                        }else {
                            tvMensaje.setText("SU CAMBIO ES " + DoubleExtensionsKt.Formato2Digitos(cambio) + " Bs");
                            tvMensaje.setTextColor(R.color.teal_700);
                        }
                        resultado = true;
                    }
                }
            } else {
                tvMensaje.setText(R.string.error);
                tvMensaje.setTextColor(Color.RED);
                resultado = false;
            }
        } else {
            tvMensaje.setText(R.string.error);
            tvMensaje.setTextColor(Color.RED);
            resultado = false;
        }
    }

}
