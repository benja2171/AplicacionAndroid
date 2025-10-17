package com.devst.app;
// Paquete del proyecto donde se encuentra esta clase.

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

/**
 * Esta actividad representa la pantalla principal (Home) de la aplicación.
 * Contiene botones para distintas acciones:
 * - Abrir perfil, cámara, configuración, web, correo, compartir, Google Maps, linterna y llamadas.
 * - Incluye un menú superior con opciones adicionales.
 */
public class HomeActivity extends AppCompatActivity {

    // Variable para guardar el correo o nombre del usuario recibido desde el login
    private String emailUsuario = "";

    // Texto de bienvenida en la interfaz
    private TextView tvBienvenida;

    // -------------------------------
    // 🔦 VARIABLES PARA LA LINTERNA
    // -------------------------------
    private Button btnLinterna;       // Botón que enciende o apaga la linterna
    private CameraManager camara;     // Controlador para acceder a la cámara
    private String camaraID = null;   // ID de la cámara que tiene flash
    private boolean luz = false;      // Estado actual de la linterna (encendida/apagada)

    // -------------------------------
    // 📥 Activity Result para recibir datos desde PerfilActivity
    // -------------------------------
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Si el resultado viene con datos (por ejemplo, un nombre editado)
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if (nombre != null) {
                        // Se actualiza el mensaje de bienvenida con el nuevo nombre
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });

    // -------------------------------
    // 📸 Launcher para pedir permiso de cámara (necesario para linterna y cámara)
    // -------------------------------
    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    // Si el usuario acepta, se cambia el estado de la linterna
                    alternarluz();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Vincula el diseño XML

        // -------------------------------
        // 🧭 Configuración de la Toolbar
        // -------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // -------------------------------
        // 🔹 Referencias a los elementos del layout
        // -------------------------------
        tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        btnLinterna = findViewById(R.id.btnLinterna);
        Button btnGoogleMaps = findViewById(R.id.btnGoogleMaps);
        Button btnLlamar = findViewById(R.id.btnLlamar);
        Button btnCamara = findViewById(R.id.btnCamara);
        Button btnConfig = findViewById(R.id.btnConfig);

        // -------------------------------
        // 📧 Recibir el dato del LoginActivity
        // -------------------------------
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);

        // -------------------------------
        // 👤 Abrir PerfilActivity (intent explícito con resultado)
        // -------------------------------
        btnIrPerfil.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(i);
        });

        // -------------------------------
        // 🌐 Abrir sitio web con intent implícito
        // -------------------------------
        btnAbrirWeb.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.santotomas.cl"));
            startActivity(intent);
        });

        // -------------------------------
        // 📩 Enviar correo electrónico
        // -------------------------------
        btnEnviarCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:")); // Asegura que se abra solo en apps de correo
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"Benja@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Prueba Android");
            email.putExtra(Intent.EXTRA_TEXT, "Hola, Profe. Pónganos un 7.0 :)");
            startActivity(Intent.createChooser(email, "Enviar correo con:"));
        });

        // -------------------------------
        // 🔗 Compartir texto con otras apps
        // -------------------------------
        btnCompartir.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Hola desde mi app Android 😎");
            startActivity(Intent.createChooser(share, "Compartir usando:"));
        });

        // -------------------------------
        // 🗺️ Abrir Google Maps con una ubicación específica
        // -------------------------------
        btnGoogleMaps.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Vergara 165, Santiago, Chile"));
            startActivity(intent);
        });

        // -------------------------------
        // 📞 Abrir la app de teléfono
        // -------------------------------
        btnLlamar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
            startActivity(intent);
        });

        // -------------------------------
        // ⚙️ Abrir ConfigActivity (pantalla de configuración)
        // -------------------------------
        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ConfigActivity.class);
            startActivity(intent);
        });

        // -------------------------------
        // 🔦 Inicializar la cámara para usar la linterna
        // -------------------------------
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            // Recorre las cámaras del dispositivo para encontrar una con flash
            for (String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleFlash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = cc.get(CameraCharacteristics.LENS_FACING);

                // Se elige la cámara trasera que tenga flash disponible
                if (Boolean.TRUE.equals(disponibleFlash)
                        && lensFacing != null
                        && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    camaraID = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
        }

        // -------------------------------
        // 🟡 Evento del botón de linterna
        // -------------------------------
        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                Toast.makeText(this, "Este dispositivo no tiene flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica si se tiene permiso de cámara
            boolean camGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;

            // Si ya tiene permiso → alterna luz, si no → solicita permiso
            if (camGranted) {
                alternarluz();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // -------------------------------
        // 📸 Abrir CamaraActivity (para tomar foto)
        // -------------------------------
        btnCamara.setOnClickListener(v ->
                startActivity(new Intent(this, CamaraActivity.class))
        );
    }

    // -------------------------------
    // 💡 Método para encender o apagar la linterna
    // -------------------------------
    private void alternarluz() {
        try {
            luz = !luz; // Cambia el estado (de apagado a encendido o viceversa)
            camara.setTorchMode(camaraID, luz); // Activa o desactiva el flash del dispositivo
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------------------
    // 🚫 Apagar linterna al salir de la app o minimizar
    // -------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        if (camaraID != null && luz) {
            try {
                camara.setTorchMode(camaraID, false);
                luz = false;
                if (btnLinterna != null) btnLinterna.setText("Encender Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }

    // -------------------------------
    // 📋 Crear el menú superior (main_menu.xml)
    // -------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // -------------------------------
    // 🧭 Acciones al seleccionar opciones del menú
    // -------------------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_perfil) {
            // Ir al perfil (Intent explícito)
            Intent i = new Intent(this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(i);
            return true;

        } else if (id == R.id.action_web) {
            // Abrir sitio web (Intent implícito)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com")));
            return true;

        } else if (id == R.id.action_salir) {
            // Cierra la actividad (vuelve al login o sale de la app)
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
