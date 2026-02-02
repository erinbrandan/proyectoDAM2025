package com.example.wellnessgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Clase: Registro
 * Descripción: Gestiona el formulario de alta de nuevos usuarios en el sistema.
 * Recopila datos personales (DNI, Nombre, Apellidos, Contacto) y los envía
 * de forma segura (con hashing de contraseña) a la API REST de WellnessGo.
 * * @author erinBrandan
 */
public class Registro extends AppCompatActivity {
    protected ImageButton imaBoton;
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;

    // Campos de EditText:
    protected EditText caja1; // Nombre
    protected EditText caja2; // Apellido 1 (Nuevo)
    protected EditText caja3; // Apellido 2 (Nuevo)
    protected EditText caja4; // DNI (Antes caja2)
    protected EditText caja5; // Dirección (Nuevo)
    protected EditText caja6; // Código Postal (Nuevo)
    protected EditText caja7; // Teléfono (Nuevo)
    protected EditText caja8; // Email (Antes caja3)
    protected EditText caja9; // Contraseña (Antes caja4)

    protected Button boton1;
    protected Button boton2;
    protected Intent pasarPantalla;

    // URL base del servidor/API REST para las peticiones de red.
    private final String URL_BASE = "http://wellnessgo.ddns.net:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de Vistas (Textos y Botón de Volver)
        imaBoton=(ImageButton) findViewById(R.id.imaBoton1_registro);
        texto1=(TextView) findViewById(R.id.texto1_registro);
        texto2=(TextView) findViewById(R.id.texto2_registro);
        texto3=(TextView) findViewById(R.id.texto3_registro);
        texto4=(TextView) findViewById(R.id.texto4_registro);
        texto5=(TextView) findViewById(R.id.texto5_registro);

        // Inicialización de Cajas de Texto (EditTexts)
        caja1=(EditText) findViewById(R.id.caja1_registro); // Nombre
        caja2=(EditText) findViewById(R.id.caja2_registro); // Apellido 1
        caja3=(EditText) findViewById(R.id.caja3_registro); // Apellido 2
        caja4=(EditText) findViewById(R.id.caja4_registro); // DNI
        caja5=(EditText) findViewById(R.id.caja5_registro); // Dirección
        caja6=(EditText) findViewById(R.id.caja6_registro); // Código Postal
        caja7=(EditText) findViewById(R.id.caja7_registro); // Teléfono
        caja8=(EditText) findViewById(R.id.caja8_registro); // Email
        caja9=(EditText) findViewById(R.id.caja9_registro); // Contraseña

        boton1=(Button) findViewById(R.id.boton1_registro);
        boton2=(Button) findViewById(R.id.boton2_registro);

        /*
         * Evento para procesar el registro del usuario.
         */
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
        /*
         * Evento para cancelar y volver a la pantalla de Login.
         */
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla = new Intent(Registro.this, Login.class);
                startActivity(pasarPantalla);
                finish();
            }
        });
    }

    /**
     * Valida que ninguno de los campos de texto requeridos esté vacío.
     * @return true si todos los campos son válidos; false si falta alguno.
     */
    public boolean validarCampos(String dni, String nombre, String apellido1, String apellido2, String direccion, String cp, String telefono, String email, String contrasena) {
        return !(dni == null || dni.trim().isEmpty() ||
                nombre == null || nombre.trim().isEmpty() ||
                apellido1 == null || apellido1.trim().isEmpty() ||
                apellido2 == null || apellido2.trim().isEmpty() ||
                direccion == null || direccion.trim().isEmpty() ||
                cp == null || cp.trim().isEmpty() ||
                telefono == null || telefono.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                contrasena == null || contrasena.trim().isEmpty());
    }

    /**
     * Recoge los datos del formulario y llama al método de red para registrar al cliente.
     */
    private void registrarUsuario() {
        // 1. Obtener y validar datos de todos los campos (incluyendo los nuevos)
        String nombre = caja1.getText().toString().trim();
        String apellido1 = caja2.getText().toString().trim();
        String apellido2 = caja3.getText().toString().trim();
        String dni = caja4.getText().toString().trim();
        String direccion = caja5.getText().toString().trim();
        String codigoPostal = caja6.getText().toString().trim();
        String telefono = caja7.getText().toString().trim();
        String email = caja8.getText().toString().trim();
        String contrasena = caja9.getText().toString().trim();

        // Se asume la existencia de la clase ValidadorRegistro o se usa el método local:
        if (!validarCampos(dni, nombre, apellido1, apellido2, direccion, codigoPostal, telefono, email, contrasena)) {
            Toast.makeText(Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Llamar al método que realiza la conexión, pasando todos los datos.
        enviarDatosRegistro(dni, nombre, apellido1, apellido2, direccion, codigoPostal, telefono, email, contrasena);
    }

    /**
     * Envía los datos de registro (POST) al endpoint /clientes de la API REST.
     */
    private void enviarDatosRegistro(String dni, String nombre, String apellido1, String apellido2, String direccion, String codigoPostal, String telefono, String email, String contrasena) {

        // Encriptar contraseña
        String contrasenaHash = PasswordHasher.hashPassword(contrasena);

        if (contrasenaHash == null) {
            runOnUiThread(() -> Toast.makeText(Registro.this, "Error de seguridad al cifrar la contraseña.", Toast.LENGTH_LONG).show());
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            String finalMsg = "";
            try {
                // 1. Crear el objeto JSON con **TODOS** los datos del registro
                JSONObject registroData = new JSONObject();
                registroData.put("dni", dni);
                registroData.put("nombre", nombre);
                registroData.put("apellido1", apellido1);
                registroData.put("apellido2", apellido2);
                registroData.put("direccion", direccion);
                registroData.put("codigoPostal", codigoPostal);
                registroData.put("telefono", telefono);
                registroData.put("email", email);
                registroData.put("contrasena", contrasenaHash);


                // 2. Configurar la Conexión HTTP
                URL url = new URL(URL_BASE + "/clientes"); // Endpoint para Registro
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // 3. Enviar el JSON al servidor
                OutputStream os = conn.getOutputStream();
                os.write(registroData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                // 4. Leer la Respuesta del Servidor
                int responseCode = conn.getResponseCode();

                if (responseCode >= 200 && responseCode < 400) {
                    // Código de éxito (200 OK, 201 Created)
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    finalMsg = "Registro exitoso (" + responseCode + "): " + response.toString();
                    // Acción de éxito: Navegar al Login
                    runOnUiThread(() -> {
                        Toast.makeText(Registro.this, "Registro exitoso. ¡Inicia sesión!", Toast.LENGTH_LONG).show();
                        Intent pasarPantalla = new Intent(Registro.this, Login.class);
                        startActivity(pasarPantalla);
                        finish();
                    });
                } else {// Código de error (4xx o 5xx)
                    InputStream is = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    String mensajeErrorServidor = "Error desconocido o de red.";

                    try {
                        JSONObject errorJson = new JSONObject(response.toString());
                        if (errorJson.has("error")) {
                            mensajeErrorServidor = errorJson.getString("error");
                        } else if (errorJson.has("mensaje")) {
                            mensajeErrorServidor = errorJson.getString("mensaje");
                        } else {
                            mensajeErrorServidor = response.toString();
                        }
                    } catch (Exception jsonE) {
                        mensajeErrorServidor = response.toString();
                    }

                    finalMsg = "Error en el servidor (" + responseCode + "): " + response.toString();
                    System.out.println(finalMsg);

                    final String toastText = "Error en el registro (" + responseCode + "): " + mensajeErrorServidor;

                    // Acción de error: Mostrar Toast de error
                    runOnUiThread(() -> {
                        Toast.makeText(Registro.this, toastText, Toast.LENGTH_LONG).show();
                    });
                }

                System.out.println(finalMsg);

            } catch (Exception e) {
                e.printStackTrace();
                // Error de red (No se pudo conectar)
                runOnUiThread(() -> Toast.makeText(Registro.this, "Error de conexión: Verifica la red y la API.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}