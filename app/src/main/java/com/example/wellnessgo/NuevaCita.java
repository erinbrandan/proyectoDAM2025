package com.example.wellnessgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Clase: NuevaCita
 * Descripción: Gestiona la primera fase del proceso de reserva de citas.
 * Se encarga de conectar con la API REST para obtener el catálogo de especialidades
 * médicas disponibles, asociar cada una con su icono correspondiente y permitir
 * al usuario seleccionar una para avanzar al siguiente paso de la reserva.
 *  @author erinBrandan
 */
public class NuevaCita extends AppCompatActivity {
    protected ImageButton imaBoton1;
    protected TextView texto1;
    protected TextView texto2;
    protected ListView lista1;
    protected List<EspecialidadItem> listaEspecialidades;
    protected List<String> nombresEspecialidades;
    protected int[] imagenes;
    protected String contenidoItem="";
    protected Intent pasarPantalla;
    // URL base del servidor/API REST.
    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";


    /*
     * Mapa estático que vincula el nombre de la especialidad devuelto por la API
     * con los recursos gráficos (drawables) locales de la aplicación.
     */
    private static final Map<String, Integer> MAPA_IMAGENES = new HashMap<>();
    static {
        // Inicializar el mapa con las especialidades y sus recursos fijos
        MAPA_IMAGENES.put("Fisioterapia", R.drawable.huesos);
        MAPA_IMAGENES.put("Psicología", R.drawable.cerebro);
        MAPA_IMAGENES.put("Cardiología", R.drawable.corazon);
        MAPA_IMAGENES.put("Yoga", R.drawable.yoga);
        MAPA_IMAGENES.put("Mindfullness", R.drawable.mindfullness);
        // Añadir más mapeos fijos aquí si es necesario en el futuro.
    }
    private AdaptadorEspecialidades adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_cita);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1 = (ImageButton) findViewById(R.id.imaBoton1_nuevaCita);
        texto1 = (TextView) findViewById(R.id.texto1_nuevaCita);
        texto2 = (TextView) findViewById(R.id.texto2_nuevaCita);
        lista1 = (ListView) findViewById(R.id.listView_nuevaCita);
        // Inicializar la lista de especialidades como una lista dinámica y vacía
        nombresEspecialidades = new ArrayList<>();
        listaEspecialidades = new ArrayList<>();

        // Inicializar el adaptador con una lista vacía y un array de imágenes vacío
        String[] nombresArrayInicial = nombresEspecialidades.toArray(new String[0]);
        int[] imagenesArrayInicial = new int[0];

        adaptador = new AdaptadorEspecialidades(this, nombresArrayInicial, imagenesArrayInicial);
        lista1.setAdapter(adaptador);
        // Iniciar la carga de datos desde la API
        cargarEspecialidadesDesdeAPI();
        /*
         * Listener para la selección de una especialidad.
         * Envía el ID y el nombre de la especialidad a la siguiente pantalla (NuevaCita2).
         */
        lista1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el item completo (EspecialidadItem) de la lista principal
                EspecialidadItem itemSeleccionado = listaEspecialidades.get(position);

                if (itemSeleccionado != null) {
                    String nombreEspecialidad = itemSeleccionado.getNombre();
                    int idSeleccionado = itemSeleccionado.getId(); // NUEVO: OBTENER EL ID

                    pasarPantalla = new Intent(NuevaCita.this, NuevaCita2.class);
                    pasarPantalla.putExtra("ESPECIALIDAD_NOMBRE", nombreEspecialidad);
                    pasarPantalla.putExtra("ESPECIALIDAD_ID", idSeleccionado);

                    startActivity(pasarPantalla);
                    finish();
                    //Toast.makeText(NuevaCita.this, "Especialidad: " + nombreEspecialidad + " (ID: " + idSeleccionado + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Realiza una solicitud GET a la API para obtener la lista de especialidades
     * y actualiza el ListView y el Adaptador.
     */
    private void cargarEspecialidadesDesdeAPI() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // 1. Configurar la Conexión HTTP
                URL url = new URL(URL_BASE + "/especialidad"); // Endpoint sugerido
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
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
                    Log.i("API_DATA", "Especialidades JSON: " + jsonResponse);

                    // 3. Procesar el JSON y llenar la lista


                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    List<EspecialidadItem> itemsNuevos = new ArrayList<>();
                    List<String> nombresNuevos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        Object item = jsonArray.get(i);
                        if (item instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) item;
                            int id = jsonObject.getInt("id_especialidad");
                            String nombre = jsonObject.getString("especialidad");
                            // extraemos el campo 'nombre'
                            itemsNuevos.add(new EspecialidadItem(id, nombre));
                            nombresNuevos.add(nombre);
                        } else if (item instanceof String) {
                            // Si es un array de strings simples
                            Log.w("API_PARSE", "Elemento JSON es solo String, ID no disponible.");
                        }
                    }

                    // 4. Actualizar la UI en el Hilo Principal
                    runOnUiThread(() -> {
                        listaEspecialidades.clear();
                        listaEspecialidades.addAll(itemsNuevos);
                        nombresEspecialidades.clear();
                        nombresEspecialidades.addAll(nombresNuevos);

                        String[] nombresArray = nombresEspecialidades.toArray(new String[0]);
                        int[] imagenesArray = getImagenesParaEspecialidades(nombresEspecialidades);

                        // Reestablecer el adaptador con los datos actualizados:
                        adaptador = new AdaptadorEspecialidades(NuevaCita.this, nombresArray, imagenesArray);
                        lista1.setAdapter(adaptador);

                        //Toast.makeText(NuevaCita.this, "Especialidades cargadas: " + nombresEspecialidades.size(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    // Manejo de errores de respuesta (4xx, 5xx)
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) errorResponse.append(errorLine);
                    errorReader.close();
                    Log.e("API_ERROR", "Error al cargar especialidades. Código: " + responseCode + ", Mensaje: " + errorResponse.toString());

                    runOnUiThread(() -> Toast.makeText(NuevaCita.this, "Error al cargar especialidades (HTTP: " + responseCode + ")", Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("CONN_ERROR", "Error de conexión/parsing: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NuevaCita.this, "Error de conexión: No se pudo acceder al listado.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
    /**
     * Genera un array de IDs de recursos de imágenes (int[]) basándose en
     * la lista de nombres de especialidades y el mapeo fijo (MAPA_IMAGENES).
     * @param nombresEspecialidades Lista de nombres obtenidos de la API.
     * @return Array de enteros con los IDs de los recursos drawable.
     */
    private int[] getImagenesParaEspecialidades(List<String> nombresEspecialidades) {
        int size = nombresEspecialidades.size();
        int[] resourceImages = new int[size];

        // Define un ícono por defecto si no se encuentra la especialidad en el mapa
        final int ICONO_POR_DEFECTO = R.drawable.corazon; //En el futuro incluir un icono_generico

        for (int i = 0; i < size; i++) {
            String nombre = nombresEspecialidades.get(i);

            // Busca la imagen en el mapa
            Integer recursoId = MAPA_IMAGENES.get(nombre);

            if (recursoId != null) {
                // Asigna la imagen correcta
                resourceImages[i] = recursoId;
            } else {
                // Usa el icono por defecto
                resourceImages[i] = ICONO_POR_DEFECTO;
                Log.w("IMAGENES", "No se encontró imagen mapeada para la especialidad: " + nombre);
            }
        }
        return resourceImages;
    }



}