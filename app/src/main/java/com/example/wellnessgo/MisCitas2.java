package com.example.wellnessgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MisCitas2 extends AppCompatActivity {
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;
    protected ImageView ima1;
    protected ImageView ima2;
    protected ImageButton imaBoton1; // Botón de Volver
    protected Button boton1; // Botón de Cancelar Cita
    // Datos de la cita
    private String idCita; // Nuevo campo para guardar el ID de la cita
    private String especialista;
    private String especialidad;
    private String fecha;
    private String hora;

    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";

    protected Intent pasarPantalla;
    private String clienteID = ""; // DNI del usuario logueado
    private String profesionalID = ""; // DNI del profesional

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_citas2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        texto1=(TextView) findViewById(R.id.texto1_misCitas2);
        texto2=(TextView) findViewById(R.id.texto2_misCitas2); //Especialista
        texto3=(TextView) findViewById(R.id.texto3_misCitas2); //Especialidad
        texto4=(TextView) findViewById(R.id.texto4_misCitas2); //Fecha
        texto5=(TextView) findViewById(R.id.texto5_misCitas2); //Hora
        ima1=(ImageView) findViewById(R.id.imagen1_misCitas2);
        ima2=(ImageView) findViewById(R.id.imagen2_misCitas2);
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_misCitas2); // Botón de Volver
        boton1=(Button) findViewById(R.id.boton1_misCitas2); // Botón Cancelar
        // ================== RECUPERAR DATOS DEL LOGIN (USER_ID) ==================
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Leemos el email/ID guardado en Login.java. "" es el valor por defecto si no lo encuentra.
        clienteID = prefs.getString("USER_ID", "");

        // ================== 1. RECUPERAR DATOS DEL INTENT ==================
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idCita = extras.getString("ID_CITA");
            especialista = extras.getString("ESPECIALISTA");
            especialidad = extras.getString("PROFESIONAL");
            fecha = extras.getString("FECHA");
            hora = extras.getString("HORA");
            profesionalID = extras.getString("PROFESIONAL_ID");

            // Asignar los datos a los TextViews
            texto2.setText(especialista);
            texto3.setText(especialidad);
            texto4.setText(fecha);
            texto5.setText(hora);
            texto1.setText("Detalles de tu cita"); // Ajustar el título si es necesario
        } else {
            Toast.makeText(this, "Error: No se han recibido los detalles de la cita.", Toast.LENGTH_LONG).show();
            // Si no hay datos, deshabilitar el botón de cancelar para prevenir errores.
            boton1.setEnabled(false);
        }

        // ================== 2. MANEJADOR DEL BOTÓN VOLVER ==================
        imaBoton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volver a la pantalla de listado de citas
                pasarPantalla = new Intent(MisCitas2.this, MisCitas.class);
                startActivity(pasarPantalla);
                finish();
            }
        });

        // ================== 3. MANEJADOR DEL BOTÓN CANCELAR ==================
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idCita != null && !idCita.isEmpty()) {
                    // Llama al método para eliminar la cita
                    cancelarCitaEnAPI();
                } else {
                    Toast.makeText(MisCitas2.this, "No se encontró el ID de la cita para cancelar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Envía una petición DELETE al endpoint /citas/{idCita} de la API REST para eliminar la cita.
     */
    private void cancelarCitaEnAPI() {

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // Aseguramos que la hora solo tenga formato HH:mm
                String horaFormateada = hora;
                if (hora != null && hora.length() > 5) {
                    // Si viene como "16:15:00", nos quedamos con los primeros 5 caracteres "16:15"
                    horaFormateada = hora.substring(0, 5);
                }
                // Crear el objeto JSON con todos los datos
                JSONObject citaData = new JSONObject();

                citaData.put("id_cita",idCita);
                citaData.put("cliente",clienteID);
                citaData.put("profesional",profesionalID);
                citaData.put("fecha",fecha);
                citaData.put("hora",horaFormateada);
                citaData.put("estado", "CANCELADA");
                citaData.put("provincia"," ");
                citaData.put("id_especialidad"," ");
                citaData.put("cp"," ");
                citaData.put("direccion"," ");
                citaData.put("localidad"," ");
                Log.d("API_JSON_DEBUG", citaData.toString());


                // Configurar la Conexión HTTP al endpoint /citas
                URL url = new URL(URL_BASE + "/cita/update");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // 3. Enviar el JSON al servidor
                OutputStream os = conn.getOutputStream();
                os.write(citaData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                // 4. Leer la Respuesta del Servidor
                int responseCode = conn.getResponseCode();

                // Leemos la respuesta para depuración, aunque el código sea 201
                InputStream is;
                if (responseCode >= 200 && responseCode <= 299) { // Éxito (200 OK, 201 Created)
                    is = conn.getInputStream();
                } else { // Error
                    is = conn.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                String jsonResponse = response.toString();
                Log.i("CITA_API", "Respuesta API Citas (" + responseCode + "): " + jsonResponse);


                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) { // 201 Creado (o 200 OK)

                    // Registro exitoso: Navegar a la pantalla principal
                    runOnUiThread(() -> {
                        Toast.makeText(MisCitas2.this, "Cita borrada correctamente", Toast.LENGTH_LONG).show();
                        pasarPantalla = new Intent(MisCitas2.this, MisCitas.class);
                        startActivity(pasarPantalla);
                        finish();
                    });
                } else {
                    // Error en la API (ej: 400 Bad Request, 500 Internal Server Error)
                    String mensajeError = new JSONObject(jsonResponse).optString("error", "Error desconocido.");
                    runOnUiThread(() -> {
                        Toast.makeText(MisCitas2.this, "Error al crear cita: " + mensajeError, Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Error de red
                runOnUiThread(() -> Toast.makeText(MisCitas2.this, "Error de conexión: No se pudo registrar la cita.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}