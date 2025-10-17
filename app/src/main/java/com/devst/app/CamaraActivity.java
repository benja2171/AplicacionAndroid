package com.devst.app;
// Paquete donde se encuentra la clase. Debe coincidir con el del proyecto.

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Esta actividad permite al usuario abrir la cámara del dispositivo,
 * tomar una foto y mostrarla en pantalla.
 * Utiliza FileProvider para guardar la imagen de forma segura.
 */
public class CamaraActivity extends AppCompatActivity {

    // Elemento del layout donde se mostrará la imagen capturada
    private ImageView imagenPrevia;

    // URI (dirección temporal) donde se guardará la foto capturada
    private Uri urlImagen;

    // ------------------------------
    // 1️⃣ SOLICITUD DE PERMISO DE CÁMARA
    // ------------------------------
    // Este launcher se encarga de pedir permiso para usar la cámara.
    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), carga -> {
                // Si el usuario concede el permiso, se toma la foto.
                if (carga) tomarFoto();
                    // Si el permiso se niega, muestra un mensaje.
                else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            });

    // ------------------------------
    // 2️⃣ CAPTURA DE FOTO CON LA CÁMARA
    // ------------------------------
    // Este launcher abre la cámara y guarda la foto en la URI proporcionada.
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), okay -> {
                // Si la foto se tomó correctamente:
                if (okay && urlImagen != null) {
                    // Se muestra la imagen en el ImageView.
                    imagenPrevia.setImageURI(urlImagen);
                    Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show();
                } else {
                    // Si se canceló la captura.
                    Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    // ------------------------------
    // 3️⃣ MÉTODO PRINCIPAL: onCreate()
    // ------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara); // Vincula el layout XML

        // Se obtienen las referencias de los elementos de la vista.
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        imagenPrevia = findViewById(R.id.imgPreview);

        // Al presionar el botón, se verifica el permiso y se toma la foto.
        btnTomarFoto.setOnClickListener(v -> checkPermisoYTomar());
    }

    // ------------------------------
    // 4️⃣ VERIFICAR PERMISO ANTES DE TOMAR FOTO
    // ------------------------------
    private void checkPermisoYTomar() {
        // Comprueba si el permiso de cámara ya fue concedido.
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        // Si el permiso está dado → tomar la foto directamente.
        if (granted) tomarFoto();
            // Si no → pedir permiso con el launcher.
        else permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
    }

    // ------------------------------
    // 5️⃣ TOMAR FOTO Y GUARDARLA EN UN ARCHIVO TEMPORAL
    // ------------------------------
    private void tomarFoto() {
        try {
            // Crea un archivo temporal donde se guardará la imagen.
            File archivoFoto = crearArchivoImagen();

            // Usa FileProvider para obtener una URI segura del archivo.
            urlImagen = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", archivoFoto);

            // Lanza la cámara para tomar la foto y guardarla en esa URI.
            takePictureLauncher.launch(urlImagen);

        } catch (IOException e) {
            // Si ocurre un error al crear el archivo, se muestra un mensaje.
            Toast.makeText(this, "No se pudo crear el archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------
    // 6️⃣ CREAR ARCHIVO DE IMAGEN CON NOMBRE ÚNICO
    // ------------------------------
    private File crearArchivoImagen() throws IOException {
        // Crea un nombre único basado en la fecha y hora actual (por ejemplo: IMG_20251017_234501_).
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre = "IMG_" + timeStamp + "_";

        // Directorio donde se guardará la imagen (carpeta Pictures de la app).
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crea un archivo temporal con ese nombre dentro del directorio.
        return File.createTempFile(nombre, ".jpg", dir);
    }
}
