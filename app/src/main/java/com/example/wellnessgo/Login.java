package com.example.wellnessgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.MotionEvent;
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
import android.util.Log;
/**
 * Clase: Login
 * Descripción: Gestiona el acceso de usuarios a la aplicación.
 * Se encarga de la validación de credenciales mediante una API REST externa,
 * el manejo de la visibilidad de contraseñas y la persistencia de sesión
 * local mediante SharedPreferences.
 * * @author erinBrandan
 */
public class Login extends AppCompatActivity {
    protected TextView texto1;
    protected TextView texto2;
    protected EditText caja1;
    protected EditText caja2;
    protected Button boton1;
    protected Button boton2;
    protected ImageButton imaboton1;
    protected Intent pasarPantalla;
    protected String contenidoCaja1;
    protected String contenidoCaja2;
    // URL base del servidor/API REST.
    private final String URL_BASE = "http://wellnessgo.ddns.net:8081";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        texto1=(TextView) findViewById(R.id.texto1_login);
        texto2=(TextView) findViewById(R.id.texto2_login);
        caja1=(EditText) findViewById(R.id.caja1_login);
        caja2=(EditText) findViewById(R.id.caja2_login);
        boton1=(Button) findViewById(R.id.boton1_login);
        boton2=(Button) findViewById(R.id.boton2_login);
        imaboton1=(ImageButton) findViewById(R.id.imagenBoton1);




        /*
         * Lógica para mostrar/ocultar contraseña al mantener pulsado el icono.
         */
        imaboton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        caja2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        caja2.setSelection(caja2.getText().length());
                        return true;
                    case MotionEvent.ACTION_UP:
                        caja2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        caja2.setSelection(caja2.getText().length());
                        return true;
                }
                return false;

            }
        });

        /*
         * Listener para el botón de Login (boton1).
         * Recoge las credenciales y llama a 'verificarCredenciales()'
         * para validar al usuario contra la API REST remota.
         */
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoCaja1 = caja1.getText().toString().trim(); // Email/Usuario
                contenidoCaja2 = caja2.getText().toString().trim(); // Contraseña

                if (contenidoCaja1.isEmpty() || contenidoCaja2.isEmpty()) {
                    Toast.makeText(Login.this, "Debe rellenar los dos campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamar al método de verificación que manejará la conexión
                    // y la transición a la pantalla Principal si el login es exitoso.
                    verificarCredenciales(contenidoCaja1, contenidoCaja2);
                }

            }
        });
        /*
         * Acción del botón de acceso.
         * Recoge las credenciales y llama a 'verificarCredenciales()'
         */
        boton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pasarPantalla = new Intent(Login.this, Principal.class);
                startActivity(pasarPantalla);
                finish();
                return false;
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla= new Intent(com.example.wellnessgo.Login.this, Registro.class);
                startActivity(pasarPantalla);
            }
        });





    }
    /**
     * Realiza una petición POST de red para validar el usuario.
     * Envía las credenciales (POST) al endpoint /login de la API REST para verificar el acceso.
     * * @param email Correo electrónico ingresado.
     * @param contrasena Contraseña en texto plano.
     */
    private void verificarCredenciales(String email, String contrasena) {
        //Hashing de la contraseña antes de enviarla a la API
        String contrasenaHash = PasswordHasher.hashPassword(contrasena);

        if (contrasenaHash == null) {
            runOnUiThread(() -> Toast.makeText(Login.this, "Error de seguridad al cifrar la contraseña.", Toast.LENGTH_LONG).show());
            return;
        }
        // Ejecución en hilo secundario
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {

                // 1. Crear el objeto JSON con las credenciales
                JSONObject loginData = new JSONObject();
                loginData.put("email", email);
                loginData.put("contrasena", contrasenaHash);

                // 2. Configurar la Conexión HTTP
                URL url = new URL(URL_BASE + "/clientes/login"); // Endpoint para el Login
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //Se usa post para que los datos viajen ocultos en el body (debido a que se le pasa la contraseña)
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // 3. Enviar el JSON al servidor
                OutputStream os = conn.getOutputStream();
                os.write(loginData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                // 4. Leer la Respuesta del Servidor
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200 OK (Éxito)

                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    Log.i("LOGIN_SUCCESS", "Respuesta API: " + response.toString());

                    // --- INICIO DE LA MODIFICACIÓN: Leer y guardar el DNI ---

                    String tempDniCliente = null;
                    try {
                        // Intentamos parsear la respuesta JSON del servidor
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        // Asumimos que la API devuelve el DNI bajo la clave "dni"
                        tempDniCliente = jsonResponse.optString("dni");
                    } catch (Exception e) {
                        Log.e("LOGIN_PARSE_ERROR", "Error al parsear JSON o obtener DNI: " + e.getMessage());
                    }
                    final String finalDniCliente = tempDniCliente;
                    if (finalDniCliente != null && !finalDniCliente.isEmpty()) {
                        // Guardamos el DNI como identificador del usuario (USER_ID)
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("USER_ID", finalDniCliente); // Guardamos el DNI!
                        editor.apply();

                        // Acción de éxito: Navegar a la pantalla principal
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Acceso concedido. DNI: " + finalDniCliente, Toast.LENGTH_LONG).show();
                            pasarPantalla = new Intent(Login.this, Principal.class);
                            startActivity(pasarPantalla);
                            finish();
                        });
                   } else {
                        // Si la API respondió 200 pero no devolvió el DNI esperado
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Error de API: DNI no recibido.", Toast.LENGTH_LONG).show();
                        });
                    }

                    // --- FIN DE LA MODIFICACIÓN ---

                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED || responseCode == 400) {
                    // Código 401 Unauthorized o 400 Bad Request (Credenciales incorrectas)
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "Credenciales incorrectas. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                    });
                } else {
                    // Otros errores del servidor (500, etc.)
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "Error del servidor. Código: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Error de red
                runOnUiThread(() -> Toast.makeText(Login.this, "Error de conexión: No se pudo acceder al servidor.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
