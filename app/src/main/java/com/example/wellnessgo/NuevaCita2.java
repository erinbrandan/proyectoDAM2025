package com.example.wellnessgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * Clase: NuevaCita2
 * Descripción: Segunda fase del proceso de reserva de citas.
 * Esta actividad recibe el ID de la especialidad seleccionada previamente y consulta
 * a la API REST el listado de profesionales (especialistas) disponibles para dicha rama.
 * Permite al usuario seleccionar un médico específico para continuar con la reserva.
 * @author erinBrandan
 */
public class NuevaCita2 extends AppCompatActivity {
    protected ImageButton imaBoton1;
    protected TextView texto1;
    protected TextView texto2;
    protected ListView lista1;
    protected String[] nombres;
    protected String[] especialidad;
    protected int[] imagenes;
    protected Bundle extras;
    protected String paquete1; //Contiene el id de la especialidad seleccionada
    protected String paquete2; //Contiene la especialidad seleccionada

    protected Intent pasarPantalla;
    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";
    private List<Especialista> listaEspecialistas;
    private AdaptadorEspecialistas adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_cita2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_nuevaCita2);
        texto1=(TextView) findViewById(R.id.texto1_nuevaCita2);
        texto2=(TextView)  findViewById(R.id.texto2_nuevaCita2);
        lista1=(ListView) findViewById(R.id.listView_nuevaCita2);
        extras = getIntent().getExtras();
        if(extras!=null){
            int idInt = extras.getInt("ESPECIALIDAD_ID", -1);
            paquete1 = String.valueOf(idInt);
            paquete2=extras.getString("ESPECIALIDAD_NOMBRE");
        }
        else{
            Toast.makeText(NuevaCita2.this,  "No se ha recibido ningún paquete", Toast.LENGTH_SHORT).show();
        }
        // 1. Inicializar la lista y el adaptador (vacíos al principio)
        listaEspecialistas = new ArrayList<>();
        adaptador = new AdaptadorEspecialistas(this, listaEspecialistas);
        lista1.setAdapter(adaptador);
        // 2. Cargar los datos desde la API si la especialidad está disponible
        if (paquete1 != null && !paquete1.isEmpty()) {
            cargarEspecialistasDesdeAPI(paquete1);
        } else {
            Toast.makeText(this, "Error: No se puede buscar especialistas sin ID de especialidad.", Toast.LENGTH_LONG).show();

        }
        /*
         * Evento de clic en un elemento de la lista.
         * Captura los datos del profesional seleccionado y los envía a la pantalla de calendario (NuevaCita3).
         */
        lista1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void  onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el especialista directamente de la lista que usa el adaptador
                Especialista especialistaSeleccionado = listaEspecialistas.get(position);
                //Obtener datos necesarios del item
                String nombreEspecialista = especialistaSeleccionado.getNombre();
                String idEspecialista = especialistaSeleccionado.getId();

                if(!nombreEspecialista.equalsIgnoreCase("")) {
                    pasarPantalla = new Intent(NuevaCita2.this, NuevaCita3.class);
                    pasarPantalla.putExtra("ESPECIALISTA", nombreEspecialista);
                    pasarPantalla.putExtra("ESPECIALISTA_ID",idEspecialista);
                    pasarPantalla.putExtra("ESPECIALIDAD",paquete2);
                    pasarPantalla.putExtra("ESPECIALIDAD_ID",paquete1);
                    startActivity(pasarPantalla);
                    finish();
                }
            }
        });
        /*
         * Listener del botón de retroceso para volver a la selección de especialidades.
         */
        imaBoton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla = new Intent(NuevaCita2.this, NuevaCita.class);
                startActivity(pasarPantalla);
                finish();
            }
        });

    }
    /**
     * Realiza una solicitud GET a la API para obtener la lista de especialistas
     * para una especialidad dada.
     * @param especialidadId Identificador único de la especialidad para filtrar en la API.
     */
    private void cargarEspecialistasDesdeAPI(String especialidadId) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String endpoint = "/cliente/especialidad/2";

                URL url = new URL(URL_BASE + endpoint);
                Log.i("API_URL", "URL de Solicitud: " + url.toString());

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("Accept", "application/json");

                // 2. Leer la Respuesta del Servidor
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200 OK (Éxito)
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    String jsonResponse = response.toString();
                    Log.i("API_DATA", "Especialistas JSON: " + jsonResponse);

                    // 3. Procesar el JSON y llenar la lista
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    List<Especialista> especialistasNuevos = new ArrayList<>();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("dni");
                        String nombre = jsonObject.getString("nombre");

                        // Nota: Usamos R.drawable.doctor como imagen fija, ya que no la cargamos de la API.

                        especialistasNuevos.add(new Especialista(id,nombre, paquete2, R.drawable.doctor));
                    }

                    // 4. Actualizar la UI en el Hilo Principal
                    runOnUiThread(() -> {
                        listaEspecialistas.clear();
                        listaEspecialistas.addAll(especialistasNuevos);
                        // Notificar al adaptador que los datos han cambiado
                        adaptador.notifyDataSetChanged();

                        //Toast.makeText(NuevaCita2.this, "Especialistas cargados: " + listaEspecialistas.size(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    // Manejo de errores de respuesta (4xx, 5xx)
                    String error = "Error al cargar especialistas. Código: " + responseCode;
                    Log.e("API_ERROR", error);
                    runOnUiThread(() -> Toast.makeText(NuevaCita2.this, error, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("CONN_ERROR", "Error de conexión/parsing: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NuevaCita2.this, "Error de conexión: No se pudo acceder a los especialistas.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}