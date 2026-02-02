package com.example.wellnessgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MisCitas extends AppCompatActivity {
    protected ImageButton imaBoton1;
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected ListView lista1; //CitasFuturas
    protected ListView lista2; //CitasPasadas
    protected Intent pasarPantalla;

    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";



    private List<Cita> listaFuturasCitas = new ArrayList<>();;
    private List<Cita> listaPasadasCitas = new ArrayList<>();
    private AdaptadorCitas adaptadorFuturas;
    private AdaptadorCitas adaptadorPasadas;

    private String clienteID = ""; // DNI del usuario logueado
    private String profesionalID = ""; // DNI del profesional


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_citas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_misCitas1);
        texto1=(TextView) findViewById(R.id.texto1_misCitas1);
        texto2=(TextView) findViewById(R.id.texto2_misCitas1);
        texto3=(TextView) findViewById(R.id.texto3_misCitas1);
        lista1=(ListView) findViewById(R.id.listView_misCitas1);
        lista2=(ListView) findViewById(R.id.listView2_misCitas1);

        //Obtener el DNI/USER_ID guardado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        clienteID = prefs.getString("USER_ID", null); // Recuperamos el DNI




        adaptadorFuturas = new AdaptadorCitas(this, listaFuturasCitas);
        adaptadorPasadas = new AdaptadorCitas(this, listaPasadasCitas);

        lista1.setAdapter(adaptadorFuturas);
        lista2.setAdapter(adaptadorPasadas);

        //Cargar datos desde API si el ID está disponible
        if(clienteID != null && !clienteID.isEmpty()) {
            cargarCitasDesdeAPI(clienteID);
        }else {
            Toast.makeText(this, "Error: No se pudo obtener el ID de usuario.", Toast.LENGTH_LONG).show();
            Log.e("MIS_CITAS", "USER_ID (DNI) no encontrado en SharedPreferences.");
        }





        lista1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cita citaSeleccionada = listaFuturasCitas.get(position);
                pasarPantalla = new Intent(MisCitas.this,MisCitas2.class);
                pasarPantalla.putExtra("ID_CITA", citaSeleccionada.getIdCita());
                pasarPantalla.putExtra("PROFESIONAL", citaSeleccionada.getEspecialista());
                pasarPantalla.putExtra("ESPECIALIDAD",citaSeleccionada.getEspecialidad());
                pasarPantalla.putExtra("FECHA",citaSeleccionada.getFecha());
                pasarPantalla.putExtra("HORA", citaSeleccionada.getHora());
                pasarPantalla.putExtra("PROFESIONAL_ID", profesionalID);

                startActivity(pasarPantalla);
                finish();
            }
        });
        imaBoton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla = new Intent(MisCitas.this, Principal.class);
                startActivity(pasarPantalla);
                finish();
            }
        });


    }
    /**
     * Realiza una solicitud GET a la API para obtener la lista de todas las citas del usuario
     * y las clasifica como futuras o pasadas comparando la fecha con la hora actual
     */
    private void cargarCitasDesdeAPI(String idUsuario) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            // Definimos el formato esperado de la fecha y hora, basado en el ejemplo: "20-oct-2025 10.30 am"
            // Usamos Locale.US porque el mes "oct" y el indicador "am/pm" están en inglés.
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            try {
                //Configurar la URL de la Conexión HTTP
                String urlString = URL_BASE + "/cita/cliente/"+idUsuario;
                Log.d("API_CALL", "URL de Citas: " + urlString);

                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("Accept", "application/json");

                // 2. Leer la Respuesta del Servidor
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200 OK
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    String jsonResponse = response.toString();
                    Log.i("API_DATA", "Citas JSON RECIBIDO: " + jsonResponse);

                    if (jsonResponse.isEmpty() || jsonResponse.trim().equals("[]")) {
                        Log.e("API_DATA", "Respuesta JSON Vacía o '[]' recibida.");
                        runOnUiThread(() -> Toast.makeText(MisCitas.this, "Respuesta del servidor vacía.", Toast.LENGTH_LONG).show());
                        return; // Termina la función aquí si no hay datos.
                    }

                    // 3. Procesar el JSON y llenar las listas
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    Log.i("API_DATA", "Elementos JSON detectados: " + jsonArray.length());
                    List<Cita> futuras = new ArrayList<>();
                    List<Cita> pasadas = new ArrayList<>();

                    // Obtenemos la hora actual para la comparación
                    Date now = new Date();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String estado = jsonObject.getString("estado");
                        if (!"CONFIRMADA".equalsIgnoreCase(estado)) {
                            continue; // Ignoramos cualquier otro estado (Pendiente, Cancelada, etc.)
                        }

                        String idCita = jsonObject.getString("id");
                        profesionalID = jsonObject.getString("id_profesional");
                        String especialidad = jsonObject.getString("especialidad");
                        String fecha = jsonObject.getString("fecha");
                        String hora = jsonObject.getString("hora");
                        String nombreEspecialista = "Cargando...";


                        try {
                            nombreEspecialista = obtenerNombreProfesional(profesionalID);
                        } catch (Exception e) {
                            Log.e("API_CALL", "No se pudo obtener el nombre para DNI: " + profesionalID + ". Usando ID.");
                            nombreEspecialista = profesionalID; // Usar el ID como respaldo
                        }

                        // Combinar fecha y hora para el parsing
                        String dateTimeString = fecha + " " + hora;
                        Date citaDate = null;

                        try {
                            // Parsear la cadena completa de fecha y hora
                            citaDate = sdf.parse(dateTimeString);
                        } catch (ParseException e) {
                            Log.e("DATE_PARSE", "Error al parsear la fecha: " + dateTimeString + ". Cita omitida.");
                            // Si falla el parseo de la fecha, omitimos esta cita y pasamos a la siguiente.
                            continue;
                        }

                        // Crear el objeto Cita
                        Cita nuevaCita = new Cita(idCita,nombreEspecialista, especialidad, fecha, hora, R.drawable.doctor);

                        // Lógica para clasificar la cita por comparación
                        if (citaDate != null) {
                            if (citaDate.after(now)) {
                                // La fecha de la cita es posterior a la hora actual (Futura)
                                futuras.add(nuevaCita);
                            } else {
                                // La fecha de la cita es igual o anterior a la hora actual (Pasada)
                                pasadas.add(nuevaCita);
                            }
                        }
                    }

                    // 4. Actualizar la UI en el Hilo Principal con los citas recogidas de la API

                    runOnUiThread(() -> {
                        listaFuturasCitas.clear(); //Limpiar lista vinculada al adaptador
                        listaFuturasCitas.addAll(futuras); //Agregar nuevos datos desde la API
                        adaptadorFuturas.notifyDataSetChanged(); //NOtificar cambios al adaptador para forzar al ListView a actualizarse

                        listaPasadasCitas.clear();
                        listaPasadasCitas.addAll(pasadas);
                        adaptadorPasadas.notifyDataSetChanged();
                        //Monstrar un toast para confirmar que la carga se completó
                        //Toast.makeText(MisCitas.this,"Citas cargadas: " + (futuras.size() + pasadas.size()), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    BufferedReader errorReader = null;
                    String errorMessage = "Error desconocido.";
                    try {
                        // Intenta leer el mensaje de error del cuerpo de la respuesta
                        errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) errorResponse.append(line);
                        errorMessage = errorResponse.toString();
                    } catch (Exception e) {
                        errorMessage = "Error al leer el stream de error: " + e.getMessage();
                    } finally {
                        if (errorReader != null) errorReader.close();
                    }

                    String error = "Error al cargar citas. Código: " + responseCode + ". Mensaje del servidor: " + errorMessage;
                    Log.e("API_ERROR", error);
                    runOnUiThread(() -> Toast.makeText(MisCitas.this, error, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("CONN_ERROR", "Error de conexión/parsing de citas: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MisCitas.this, "Error de conexión: No se pudieron cargar las citas.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
    /**
     * Función auxiliar para realizar la llamada al nuevo endpoint y obtener solo el nombre.
     * Esta función debe ser llamada desde el hilo de red (cargarCitasDesdeAPI).
     */
    private String obtenerNombreProfesional(String dni) throws IOException, org.json.JSONException {
        HttpURLConnection conn = null;
        String nombre = dni; // Valor por defecto en caso de fallo

        // URL para obtener el profesional: http://wellnessgo.ddns.net:8080/profesional/dni/DNI
        String urlString = URL_BASE + "/profesional/dni/" + dni;

        URL url = new URL(urlString);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            // El JSON devuelto es el objeto Profesional completo.
            JSONObject jsonObject = new JSONObject(response.toString());

            // Extraer solo el nombre
            if (jsonObject.has("nombre")) {
                nombre = jsonObject.getString("nombre");
            } else {
                Log.w("API_PROF", "JSON de profesional no tiene campo 'nombre'. Usando DNI.");
            }
        } else {
            Log.e("API_PROF", "Error al obtener profesional. Código: " + responseCode);
        }

        if (conn != null) conn.disconnect();
        return nombre;
    }
}