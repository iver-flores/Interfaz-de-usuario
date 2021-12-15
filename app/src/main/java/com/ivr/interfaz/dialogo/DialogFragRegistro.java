package com.ivr.interfaz.dialogo;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.metodixrefactor.utils.DoubleExtensionsKt;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ivr.interfaz.GlobalDatos;
import com.ivr.interfaz.R;
import com.ivr.interfaz.fragment.EsquinaFragment;
import com.ivr.interfaz.sql.SqliteDato;
import com.ivr.interfaz.utils.PreferenceUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DialogFragRegistro extends DialogFragment implements View.OnClickListener{

    AlertDialog.Builder builder;

    private String PATH_PROFILE = "", serial = "3I4V10ER";

    private TextView tvUsuario, tvEmail, tvProveedor, tvQrPagar, tvTitulo;
    private Button btnCerrarSesion, btnSubir;
    private ImageButton ibAtras, ib1, ib2, ibQrPagar;
    private ImageView ivRegistro;
    private LinearLayout llQr, llPagar;

    private static final int RC_SING_IN = 123;
    private static final int RC_FROM_GALLERY = 124;
    private static final String PROVEEDOR_DESCONOCIDO = "Proveedor desconocido";
    private static final String PASSWORD_FIREBASE = "password";
    private static final String GOOGLE = "google.com";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference databaseReference;

    private int contador = 0;
    private Double pasajedb = 0.0;

    private SqliteDato mDatabase;
    private Uri urlFB = null;
    private Bitmap myBitmap = null;

    public DialogFragRegistro(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearDialogoFrag();
    }

    private AlertDialog crearDialogoFrag() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (requireActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.frag_dialog_registro, null);
        serial = ((GlobalDatos) requireContext().getApplicationContext()).getIdEsp();
        PATH_PROFILE = serial + "/QR/";
        builder.setView(v);
        setHasOptionsMenu(true);
        init(v);
        llQr.setVisibility(View.GONE);
        llPagar.setVisibility(View.GONE);
        btnSubir.setVisibility(View.GONE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null){
                PreferenceUtil.INSTANCE.saveResultRegistro(requireContext(), true);
                String name="Nombre", email="Correo", providerId=PROVEEDOR_DESCONOCIDO;
                for (UserInfo profile : user.getProviderData()) {
                    name = profile.getDisplayName();
                    email = profile.getEmail();
                    providerId = profile.getProviderId();
                }
                onSetDataUser(name, email,  providerId);
                if (((GlobalDatos) requireContext().getApplicationContext()).getModo().equals("usr")){
                    tvTitulo.setText(R.string.registro_de_usuario);
                    llPagar.setVisibility(View.VISIBLE);
                    btnSubir.setVisibility(View.VISIBLE);
                    btnSubir.setText(R.string.subir_comprobante);
                    pasajedb = DoubleExtensionsKt.Formato1Digito(mDatabase.leerPasaje());
                    if (pasajedb != -1) {
                        if (pasajedb == 2.2) {
                            databaseReference.child(serial).child("Url2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                    } else {
                                        contador = 2;
                                        urlFB = Uri.parse(String.valueOf(task.getResult().getValue()));
                                        verImagePago(urlFB);
                                    }
                                }
                            });
                        } else if (pasajedb == 2.0) {
                            databaseReference.child(serial).child("Url1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                    } else {
                                        contador = 1;
                                        urlFB = Uri.parse(String.valueOf(task.getResult().getValue()));
                                        verImagePago(urlFB);
                                    }
                                }
                            });
                        }
                    }else {
                        llPagar.setVisibility(View.GONE);
                        btnSubir.setVisibility(View.GONE);
                    }
                }else if (((GlobalDatos) requireContext().getApplicationContext()).getModo().equals("pro")){
                    tvTitulo.setText(R.string.registro_de_conductor);
                    llQr.setVisibility(View.VISIBLE);
                    btnSubir.setVisibility(View.VISIBLE);
                    btnSubir.setText(R.string.borrar_qr);
                    if (contador == 0) {
                        databaseReference.child(serial).child("Url1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                } else {
                                    contador = 1;
                                    verImage(Uri.parse(String.valueOf(task.getResult().getValue())));
                                }
                            }
                        });
                        databaseReference.child(serial).child("Url2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {

                                } else {
                                    contador = 2;
                                    verImage(Uri.parse(String.valueOf(task.getResult().getValue())));
                                }
                            }
                        });
                    }
                }
            }else {
                PreferenceUtil.INSTANCE.saveResultRegistro(requireContext(), false);
                onSignedOutCleanUp();

                AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                        //.setScopes(Arrays.asList(Scopes.GAMES))
                        .build();
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false).setTosUrl("https://sites.google.com/d/1v-KCj2GdWsIOJx7PG52qKs2SGffD4g0K/p/1ps4JNj4rpkkuEjoFXcinkENCqStODvur/edit")
                        .setAvailableProviders(Arrays.asList(new
                                        AuthUI.IdpConfig.EmailBuilder().build(), googleIdp))
                        .setTheme(R.style.NoActionBar)
                        .setLogo(R.drawable.logo)
                        .build(), RC_SING_IN);
            }
        };

        return builder.create();
    }

    private void init( View v) {
        tvUsuario = v.findViewById(R.id.tv_user_name);
        tvEmail = v.findViewById(R.id.tv_email);
        tvProveedor = v.findViewById(R.id.tv_provider_name);
        tvQrPagar = v.findViewById(R.id.tv_qr_pagar);
        tvTitulo = v.findViewById(R.id.tv_titulo);
        ibAtras = v.findViewById(R.id.ib_atras);
        ib1 = v.findViewById(R.id.ib_qr_1);
        ib2 = v.findViewById(R.id.ib_qr_2);
        ibQrPagar = v.findViewById(R.id.ib_qr_pagar);
        btnCerrarSesion = v.findViewById(R.id.btn_cerrar);
        btnSubir = v.findViewById(R.id.btn_subir);
        llQr = v.findViewById(R.id.ll_qr);
        llPagar = v.findViewById(R.id.ll_pagar);

        ibAtras.setOnClickListener(this);
        ib1.setOnClickListener(this);
        ib2.setOnClickListener(this);
        ibQrPagar.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
        btnSubir.setOnClickListener(this);

        mDatabase = new SqliteDato(requireContext());
    }

    private void onSignedOutCleanUp() {
        onSetDataUser("", "", "");
    }

    private void onSetDataUser(String userName, String email, String provider) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(serial);
        reference.child("Nombre").setValue(userName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()){
                            //userUpdate = null;
                        }
                    }
                });
        tvUsuario.setText(userName);
        tvEmail.setText(email);
        int drawableRes;
        switch (provider){
            case PASSWORD_FIREBASE:
                drawableRes = R.drawable.ic_firebase;
                break;
            case GOOGLE:
                drawableRes = R.drawable.fui_ic_googleg_color_24dp;
                break;
            default:
                drawableRes = R.drawable.ic_block_helper;
                provider = PROVEEDOR_DESCONOCIDO;
                break;
        }

        tvProveedor.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0, 0, 0);
        tvProveedor.setText(provider);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SING_IN){
            if (resultCode == RESULT_OK){
                PreferenceUtil.INSTANCE.saveResultRegistro(requireContext(), true);
                Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getContext(), "Algo fallo", Toast.LENGTH_LONG).show();
                PreferenceUtil.INSTANCE.saveResultRegistro(requireContext(), false);
            }
        }else if (requestCode == RC_FROM_GALLERY && resultCode == RESULT_OK){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference();
            if (contador == 1){
                 reference = storage.getReference().child(PATH_PROFILE).child("qr1");
            }else if (contador == 2){
                 reference = storage.getReference().child(PATH_PROFILE).child("qr2");
            }
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null){
                StorageReference finalReference = reference;
                reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finalReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null){
                                    UserProfileChangeRequest request = new UserProfileChangeRequest
                                            .Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(request)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        loadImage(user.getPhotoUrl());
                                                    }
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error......", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }
    }

    private void loadImage(Uri photoUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(serial);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        if (contador == 2){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ib2);
            reference.child("Url2").setValue(photoUrl.toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()){
                                //userUpdate = null;
                            }
                        }
                    });
        } else if (contador == 1){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ib1);
            reference.child("Url1").setValue(photoUrl.toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()){
                                //userUpdate = null;
                            }
                        }
                    });
        }

    }

    private void verImage(Uri photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        if (contador == 2){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ib2);

        } else if (contador == 1){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ib1);
        }
    }

    private void verImagePago(Uri photoUrl) {
        loadimage();
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        if (contador == 2){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ibQrPagar);
            tvQrPagar.setText(R.string.pasaje_2_20);
        } else if (contador == 1){
            Glide.with(requireContext())
                    .load(photoUrl)
                    .apply(options)
                    .into(ibQrPagar);
            tvQrPagar.setText(R.string.pasaje_2_00);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((GlobalDatos) requireContext().getApplicationContext()).setConexion(false);
        ((GlobalDatos) requireContext().getApplicationContext()).setIdEsp(null);
        EsquinaFragment fr = new EsquinaFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fr)
                .addToBackStack(null)
                .commit();
        dismiss();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_atras:
                dismiss();
                break;
            case R.id.btn_cerrar:
                AuthUI.getInstance().signOut(requireContext());
                PreferenceUtil.INSTANCE.saveResultRegistro(requireContext(), false);
                break;
            case R.id.ib_qr_1:
                contador = 1;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RC_FROM_GALLERY);
                break;
            case R.id.ib_qr_2:
                contador = 2;
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent2, RC_FROM_GALLERY);
                break;
            case R.id.ib_qr_pagar:
                dialogoPago();
                break;
            case R.id.btn_subir:
                if (btnSubir.getText().toString().equals("Borrar QR")){
                    contador = 1;
                    loadImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/interfaz-f129e.appspot.com/o/qr%20error.jpg?alt=media&token=e7a9080b-19d1-4d60-83be-7422423522b9"));
                    contador = 2;
                    loadImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/interfaz-f129e.appspot.com/o/qr%20error.jpg?alt=media&token=e7a9080b-19d1-4d60-83be-7422423522b9"));
                }else if (btnSubir.getText().toString().equals("Subir comprobante")){

                }
                break;

        }
    }

    public void loadimage() {
        @SuppressLint("StaticFieldLeak")
        class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
            private final String url;
            public ImageLoadTask(String url) {
                this.url = url;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL connection = new URL(url);
                    InputStream input = connection.openStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    return Bitmap.createScaledBitmap(myBitmap, 1000, 400, true);
                } catch (Exception e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
            }
        }
        ImageLoadTask obj = new ImageLoadTask(urlFB.toString());
        obj.execute();
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
                Toast.makeText(getContext(), "Se guardo la imagen correctamente",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dialogoPago(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Descargar Imagen");
        builder.setMessage("¿Esta seguro de descargar la imagen QR para pagar el servicio?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadimage();
                if (myBitmap != null) {
                    saveImage(myBitmap);
                }
            }
        }).setNegativeButton("Cancelar", null);
        builder.show();
    }
}
