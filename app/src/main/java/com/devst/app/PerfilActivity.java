package com.devst.app;
// Indica el paquete donde se encuentra esta clase (estructura del proyecto Android)

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Clase que representa la pantalla de perfil del usuario
public class PerfilActivity extends AppCompatActivity {

    // Método que se ejecuta al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita el modo de diseño EdgeToEdge (para aprovechar toda la pantalla)
        EdgeToEdge.enable(this);

        // Define el layout (diseño visual) que se mostrará en esta Activity
        setContentView(R.layout.activity_perfil);

        // Se obtiene una referencia al botón "Volver" definido en el XML (activity_perfil.xml)
        Button btnVolver = findViewById(R.id.btnVolver);

        // Configura un evento al hacer clic en el botón
        btnVolver.setOnClickListener(v -> {
            // Crea un Intent explícito para volver a la pantalla principal (HomeActivity)
            Intent intent = new Intent(PerfilActivity.this, HomeActivity.class);

            // Inicia la nueva actividad (HomeActivity)
            startActivity(intent);

            // Cierra la pantalla actual (PerfilActivity) para que no quede en el historial
            finish();
        });
    }
}
