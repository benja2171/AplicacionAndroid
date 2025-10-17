package com.devst.app;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Toolbar con botón de retroceso
        Toolbar toolbar = findViewById(R.id.toolbarConfig);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configuración");
        }

        // Ejemplo: switch para modo oscuro (solo simulado)
        Switch swDark = findViewById(R.id.switchDarkMode);
        swDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Modo oscuro activado" : "Modo oscuro desactivado";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}