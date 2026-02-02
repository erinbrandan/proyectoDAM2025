package com.example.wellnessgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Clase: NuevaCita5
 * Descripción: Actividad de confirmación y cierre del proceso de reserva de citas.
 * Muestra al usuario un resumen detallado de la especialidad, el profesional, la
 * fecha y la hora seleccionada. Al confirmar, recupera el identificador del cliente
 * de SharedPreferences y realiza una petición POST/UPDATE a la API REST para
 * formalizar la cita en la base de datos.
 * * @author erinBrandan
 */
public class NuevaCita5 extends AppCompatActivity {
    protected ImageButton imaBoton1;
    protected Button boton1;
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;
    protected TextView texto6;
    protected TextView texto7;
    protected TextView texto8;

    protected ImageView ima1;
    protected ImageView ima2;
    protected String paquete1="";
    protected String paquete2="";
    protected String paquete3="";
    protected String paquete4="";
    protected String paquete5="";
    protected String paquete6="";
    protected String paquete7="";
    protected String paquete8="";
    protected String paquete9="";
    protected String paquete10="";
    protected String paquete11="";
    protected String paquete12="";
    protected String paquete13="";
    protected Bundle extras;
    protected Intent pasarPantalla;
    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";
    private String clienteID; // Almacenará el Email del cliente (USER_ID)



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_cita5);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_nuevaCita5);
        texto1=(TextView) findViewById(R.id.texto1_nuevaCita5);
        texto2=(TextView) findViewById(R.id.texto2_nuevaCita5);
        texto3=(TextView) findViewById(R.id.texto3_nuevaCita5);
        texto4=(TextView) findViewById(R.id.texto4_nuevaCita5);
        texto5=(TextView) findViewById(R.id.texto5_nuevaCita5);
        texto6=(TextView) findViewById(R.id.texto6_nuevaCita5);
        texto7=(TextView) findViewById(R.id.texto7_nuevaCita5);
        texto8=(TextView) findViewById(R.id.texto8_nuevaCita5);
        ima1=(ImageView) findViewById(R.id.ima1_nuevaCita5);
        ima2=(ImageView) findViewById(R.id.ima2_nuevaCita5);
        boton1=(Button) findViewById(R.id.boton1_nuevaCita5);
        extras = getIntent().getExtras();

        // ================== RECUPERAR DATOS DEL LOGIN (USER_ID) ==================
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Leemos el email/ID guardado en Login.java. "" es el valor por defecto si no lo encuentra.
        clienteID = prefs.getString("USER_ID", "");
        /*
         * Evento para finalizar la reserva e insertar los datos en el servidor remoto.
         */
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertarCitaEnAPI();
            }
        });
        if(extras!=null){
            paquete1=extras.getString("ESPECIALIDAD");
            paquete2=extras.getString("PROFESIONAL");
            paquete3=extras.getString("FECHA");
            paquete4=extras.getString("HORA");
            paquete5=extras.getString("RESUMEN");
            paquete6=extras.getString("PROFESIONAL_ID");
            paquete7=extras.getString("DIRECCION");
            paquete8=extras.getString("LOCALIDAD");
            paquete9=extras.getString("CP");
            paquete10=extras.getString("ESPECIALIDAD_ID");
            paquete11=extras.getString("ESTADO");
            paquete12=extras.getString("PROVINCIA");
            paquete13=extras.getString("ID_CITA");
        }
        else{
            Toast.makeText(NuevaCita5.this,  "No se ha recibido ningún paquete", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,paquete3, Toast.LENGTH_SHORT).show();
        texto3.setText(paquete2);
        texto4.setText(paquete1);
        texto5.setText("Fecha y hora");
        texto6.setText(paquete3+"   "+paquete4);
        texto8.setText(paquete5);
    }
    /**
     * Realiza una petición de red asíncrona para actualizar el estado de la cita en el servidor.
     * Envía un objeto JSON con los datos requeridos por el endpoint /cita/update.
     */
    private void insertarCitaEnAPI() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {

                // Crear el objeto JSON con todos los datos
                JSONObject citaData = new JSONObject();
                citaData.put("id_cita",paquete13);
                citaData.put("cliente",clienteID);
                citaData.put("profesional",paquete6);
                citaData.put("fecha",paquete3);
                citaData.put("hora",paquete4);
                citaData.put("direccion",paquete7);
                citaData.put("localidad",paquete8);
                citaData.put("cp",paquete9);
                citaData.put("id_especialidad",paquete10);
                citaData.put("estado", "CONFIRMADA");
                citaData.put("provincia",paquete12);

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
                        Toast.makeText(NuevaCita5.this, "Cita creada correctamente", Toast.LENGTH_LONG).show();
                        pasarPantalla = new Intent(NuevaCita5.this, Principal.class);
                        startActivity(pasarPantalla);
                        finish();
                    });
                } else {
                    // Error en la API (ej: 400 Bad Request, 500 Internal Server Error)
                    String mensajeError = new JSONObject(jsonResponse).optString("error", "Error desconocido.");
                    runOnUiThread(() -> {
                        Toast.makeText(NuevaCita5.this, "Error al crear cita: " + mensajeError, Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Error de red
                runOnUiThread(() -> Toast.makeText(NuevaCita5.this, "Error de conexión: No se pudo registrar la cita.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}


