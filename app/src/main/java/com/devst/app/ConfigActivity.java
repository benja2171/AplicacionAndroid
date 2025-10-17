package com.devst.app;
// Paquete donde se encuentra esta clase (debe coincidir con el del resto del proyecto)

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Esta actividad muestra una pantalla de configuración simple.
 * Incluye una Toolbar con botón de retroceso y un Switch para activar o desactivar el modo oscuro (solo simulado).
 */
public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        // Se vincula el layout XML correspondiente a esta pantalla (activity_config.xml)

        // -------------------------------------------------------------
        // 🧭 CONFIGURACIÓN DE LA TOOLBAR
        // -------------------------------------------------------------
        // Se obtiene la referencia a la Toolbar definida en el layout.
        Toolbar toolbar = findViewById(R.id.toolbarConfig);

        // Se establece esta Toolbar como la ActionBar principal de la actividad.
        setSupportActionBar(toolbar);

        // Si la Toolbar existe correctamente:
        if (getSupportActionBar() != null) {
            // Muestra el botón de retroceso (flecha hacia atrás) en la parte superior.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Cambia el título visible en la barra superior.
            getSupportActionBar().setTitle("Configuración");
        }

        // -------------------------------------------------------------
        // 🌙 SWITCH DE MODO OSCURO (SIMULADO)
        // -------------------------------------------------------------
        // Se obtiene el Switch definido en el layout.
        Switch swDark = findViewById(R.id.switchDarkMode);

        // Se define qué ocurre cuando el usuario activa o desactiva el Switch.
        swDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Se muestra un mensaje tipo Toast dependiendo del estado del Switch.
            String msg = isChecked ? "Modo oscuro activado" : "Modo oscuro desactivado";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    // -------------------------------------------------------------
    // 🔙 BOTÓN DE RETROCESO EN LA TOOLBAR
    // -------------------------------------------------------------
    @Override
    public boolean onSupportNavigateUp() {
        // Al presionar la flecha de retroceso, vuelve a la actividad anterior.
        onBackPressed();
        return true; // Indica que la acción se manejó correctamente.
    }
}
