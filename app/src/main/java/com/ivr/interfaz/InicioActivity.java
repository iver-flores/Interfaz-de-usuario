package com.ivr.interfaz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class InicioActivity extends AppCompatActivity {

    private ImageView ivInicio1, ivInicio2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        ivInicio1 = findViewById(R.id.iv_inicio1);
        ivInicio2 = findViewById(R.id.iv_inicio2);

        animacion("scale");


    }

    private void animacion(String scale) {
        Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        ivInicio2.startAnimation(animationScale);

        ivInicio1.setVisibility(View.VISIBLE);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.movex);
        ivInicio1.startAnimation(animation2);

        Intent intent = new Intent(this, MainActivity.class);
        new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(intent), 3000);
    }

}