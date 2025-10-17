package com.devst.app; // Paquete donde se encuentra la clase

// Importaciones necesarias para el funcionamiento del Activity
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    // Declaración de variables para los campos de texto y el botón de inicio de sesión
    private EditText edtEmail, edtPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Permite diseño de pantalla completa (sin bordes)
        setContentView(R.layout.activity_login); // Conecta el layout XML con esta Activity

        // Vinculamos los elementos del layout con sus IDs definidos en activity_login.xml
        edtEmail = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        // Al hacer clic en el botón de login, se ejecuta el método intentoInicioSesion()
        btnLogin.setOnClickListener(v -> intentoInicioSesion());

        // Listener para "Recuperar contraseña" → muestra mensaje (función aún no implementada)
        findViewById(R.id.tvRecuperarpass).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: recuperar contraseña", Toast.LENGTH_SHORT).show());

        // Listener para "Crear cuenta" → muestra mensaje (función aún no implementada)
        findViewById(R.id.tvCrear).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: crear cuenta", Toast.LENGTH_SHORT).show());
    }

    // Método que maneja el intento de inicio de sesión
    private void intentoInicioSesion() {

        // Obtiene los valores ingresados en los campos de texto (con verificación de null)
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String pass  = edtPass.getText()  != null ? edtPass.getText().toString() : "";

        // Validación 1: el campo email no debe estar vacío
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Ingresa tu correo"); // Muestra error bajo el campo
            edtEmail.requestFocus(); // Enfoca el campo para corregirlo
            return;
        }

        // Validación 2: el formato del email debe ser válido (usa expresión regular)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Correo inválido");
            edtEmail.requestFocus();
            return;
        }

        // Validación 3: la contraseña no puede estar vacía
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Ingresa tu contraseña");
            edtPass.requestFocus();
            return;
        }

        // Validación 4: la contraseña debe tener mínimo 6 caracteres
        if (pass.length() < 6) {
            edtPass.setError("Mínimo 6 caracteres");
            edtPass.requestFocus();
            return;
        }

        // Simulación de un inicio de sesión con usuario fijo
        boolean ok = email.equals("estudiante@st.cl") && pass.equals("123456");

        if (ok) {
            // Si las credenciales son correctas, muestra mensaje de bienvenida
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

            // Crea un Intent para ir a la pantalla principal (HomeActivity)
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            // Envía el correo ingresado como dato extra al HomeActivity
            intent.putExtra("email_usuario", email);

            // Inicia la nueva actividad
            startActivity(intent);

            // Finaliza LoginActivity para que no se pueda volver atrás con el botón "Atrás"
            finish();

        } else {
            // Si el correo o contraseña son incorrectos, muestra mensaje de error
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
