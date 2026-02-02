package com.example.wellnessgo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
/**
 * Clase: NuevaCita3
 * Descripción: Tercera fase del proceso de reserva de citas.
 * Permite al usuario seleccionar una fecha mediante un calendario (DatePicker) y
 * visualizar las horas disponibles para el especialista elegido. Filtra la
 * disponibilidad consultando a la API y mostrando únicamente las citas en estado "PENDIENTE".
 * * @author erinBrandan
 */
public class NuevaCita3 extends AppCompatActivity {
    protected Button boton1;
    protected Button boton2;
    protected List<Button> botonesHoras;

    protected ImageButton imaboton1;
    protected TextView texto1;
    protected TextView texto2; // Muestra Especialista
    protected TextView texto3; // Muestra Fecha
    protected TextView texto4; // Muestra "Horas Disponibles"
    protected String id_cita;
    protected String cliente;
    protected String profesional;
    protected String direccion;
    protected String localidad;
    protected String cp;
    protected String id_especialidad;
    protected String estado;
    protected String provincia;
    protected Bundle extras;
    protected String paquete1; // Especialidad
    protected String paquete2; // Especialista
    protected String paquete3; // ESPECIALISTA_ID
    protected String paquete4; //ESPECIALIDAD_ID


    protected Intent pasarPantalla;
    protected String hora = "";
    protected String fecha=""; // Fecha en formato DD/MM/YYYY
    protected String fechaParaAPI=""; //Fecha en formato YYYY-MM-DD (Para el Intent/API)

    // URL base del servidor/API REST
    private final String URL_BASE = "http://wellnessgo.ddns.net:8080";
    // Mapa para almacenar la disponibilidad: Clave=Fecha (YYYY-MM-DD), Valor=Lista de horas disponibles (HH:MM)
    private Map<String, List<String>> disponibilidadPorFecha = new HashMap<>();
    // Formatos para la fecha: uno para la UI y otro para la API
    private final SimpleDateFormat UI_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_cita3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicialización de Vistas
        boton1=(Button) findViewById(R.id.boton1_nuevaCita3);
        boton2=(Button) findViewById(R.id.boton2_nuevaCita3);
        imaboton1=(ImageButton) findViewById(R.id.imaBoton1_nuevaCita3);
        texto1=(TextView) findViewById(R.id.texto1_nuevaCita3);
        texto2=(TextView) findViewById(R.id.texto2_nuevaCita3);
        texto3=(TextView) findViewById(R.id.texto3_nuevaCita3);
        texto4=(TextView) findViewById(R.id.texto4_nuevaCita3);
        // Agrupación de botones de hora
        botonesHoras = new ArrayList<>();
        botonesHoras.add((Button) findViewById(R.id.botonHoras1_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras2_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras3_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras4_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras5_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras6_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras7_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras8_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras9_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras10_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras11_nuevaCita3));
        botonesHoras.add((Button) findViewById(R.id.botonHoras12_nuevaCita3));
        // Obtener parámetros y cargar disponibilidad
        extras = getIntent().getExtras();
        if(extras!=null){
            paquete1=extras.getString("ESPECIALIDAD");
            paquete2=extras.getString("ESPECIALISTA");
            paquete3=extras.getString("ESPECIALISTA_ID");
            paquete4=extras.getString("ESPECIALIDAD_ID");
            if (paquete2 != null && !paquete2.isEmpty() && !paquete3.isEmpty() && paquete1 != null && paquete3 != null && !paquete1.isEmpty()) {
                cargarDisponibilidadDesdeAPI(paquete3, paquete1);
            } else {
                Toast.makeText(this, "Error: Faltan datos (especialidad o especialista).", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(NuevaCita3.this,  "No se ha recibido ningún paquete", Toast.LENGTH_SHORT).show();
        }
        // Inicialmente, ocultamos todos los botones de hora
        ocultarTodosLosBotonesDeHora();
        /*
         * Listener para abrir el selector de fecha (Calendario).
         */
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int anio = calendario.get(Calendar.YEAR);
                int mes = calendario.get(Calendar.MONTH);
                int dia = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NuevaCita3.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar fechaSeleccionada = Calendar.getInstance();
                                fechaSeleccionada.set(year, month, dayOfMonth);
                                // Formato para la UI (DD/MM/YYYY)
                                fecha = UI_DATE_FORMAT.format(fechaSeleccionada.getTime());
                                texto3.setText("Fecha: " + fecha);

                                // Formato para la API/Mapa (YYYY-MM-DD)
                                String fechaParaMapa = API_DATE_FORMAT.format(fechaSeleccionada.getTime());
                                fechaParaAPI = fechaParaMapa;
                                // Lógica: Actualizar visibilidad de horas según la fecha
                                actualizarHorasDisponibles(fechaParaMapa);
                            }
                        },
                        anio, mes, dia);


                // Impedir selección de fechas pasadas
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        //Creo un único evento para todos los botones llamado "horaClickListener"
        View.OnClickListener horaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button boton = (Button) v;
                hora = boton.getText().toString();

                // Resaltar el botón seleccionado y desmarcar otros
                int colorGris = getResources().getColor(android.R.color.darker_gray);
                int colorVerde = getResources().getColor(android.R.color.holo_green_dark);

                for (Button b : botonesHoras) {
                    // Solo resetear el color si el botón está visible
                    if (b.getVisibility() == View.VISIBLE) {
                        b.setBackgroundColor(colorGris);
                    }
                }
                boton.setBackgroundColor(colorVerde);
            }
        };
        for (Button b : botonesHoras) {
            b.setOnClickListener(horaClickListener);
        }
        /*
         * Botón de confirmación para avanzar a la última pantalla de resumen.
         */
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fecha.equalsIgnoreCase("")||hora.equalsIgnoreCase("")){
                    Toast.makeText(NuevaCita3.this, "Debe escoger una fecha y una hora para poder continuar", Toast.LENGTH_SHORT).show();
                }
                else{
                    pasarPantalla=new Intent(NuevaCita3.this,NuevaCita4.class);
                    pasarPantalla.putExtra("ESPECIALIDAD",paquete1);
                    pasarPantalla.putExtra("PROFESIONAL_ID", paquete3);
                    pasarPantalla.putExtra("PROFESIONAL",paquete2);
                    pasarPantalla.putExtra("FECHA",fechaParaAPI);
                    pasarPantalla.putExtra("HORA",hora);
                    pasarPantalla.putExtra("DIRECCION",direccion);
                    pasarPantalla.putExtra("LOCALIDAD",localidad);
                    pasarPantalla.putExtra("CP",cp);
                    pasarPantalla.putExtra("ESPECIALIDAD_ID",paquete4);
                    pasarPantalla.putExtra("ESTADO",estado);
                    pasarPantalla.putExtra("PROVINCIA",provincia);
                    pasarPantalla.putExtra("ID_CITA",id_cita);
                    startActivity(pasarPantalla);
                    Toast.makeText(NuevaCita3.this,fechaParaAPI, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
        //Botón de regreso
        imaboton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vuelve a la pantalla anterior (NuevaCita2.class)
                pasarPantalla = new Intent(NuevaCita3.this, NuevaCita2.class);
                startActivity(pasarPantalla);
                finish();
            }
        });

    }

    // ================ LÓGICA DE VISIBILIDAD DE HORAS ================
    /**
     * Oculta todos los botones de hora (para resetear la vista).
     */
    private void ocultarTodosLosBotonesDeHora() {
        for (Button b : botonesHoras) {
            b.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Gestiona la visibilidad y el color de los botones de hora basándose en la fecha.
     * @param fechaParaMapa Fecha en formato YYYY-MM-DD.
     */
    private void actualizarHorasDisponibles(String fechaParaMapa) {
        ocultarTodosLosBotonesDeHora();
        hora = ""; // Resetear la hora seleccionada al cambiar la fecha

        // 1. Obtener las horas disponibles para la fecha
        List<String> horasDisponibles = disponibilidadPorFecha.get(fechaParaMapa);

        if (horasDisponibles == null || horasDisponibles.isEmpty()) {
            // Mantenemos texto4 como encabezado, pero podemos añadir un mensaje temporal
            texto4.setText(texto4.getText().toString());
            Toast.makeText(this, "No hay horas disponibles para el " + fecha, Toast.LENGTH_SHORT).show();
            return;
        }
        // -----------------------------------------------------------
        //  MOSTRAR TOAST CON LAS HORAS DISPONIBLES
        // -----------------------------------------------------------
        String horasString = String.join(", ", horasDisponibles);
        //Toast.makeText(this, "Horas disponibles para " + fecha + ": " + horasString, Toast.LENGTH_LONG).show();
        // -----------------------------------------------------------

        // 2. Mostrar solo los botones que coincidan con las horas disponibles

        int colorGris = getResources().getColor(android.R.color.darker_gray);

        for (Button boton : botonesHoras) {
            String horaBoton = boton.getText().toString().trim();

            if (horasDisponibles.contains(horaBoton)) {
                boton.setVisibility(View.VISIBLE);
                // Restaurar el color a un estado 'no seleccionado'
                boton.setBackgroundColor(colorGris);
            } else {
                // Asegurar que las horas no disponibles permanezcan invisibles
                boton.setVisibility(View.INVISIBLE);
            }
        }
    }
    // ================ CONEXIÓN A LA API ================
    /**
     * Realiza una solicitud GET a la API para obtener la lista de disponibilidad
     * para el especialista y la especialidad.
     */
    private void cargarDisponibilidadDesdeAPI(String especialistaDni, String especialidad) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // 1. CONSTRUCCIÓN DEL ENDPOINT
                String dniCodificado = URLEncoder.encode(especialistaDni, "UTF-8");
                String urlString = URL_BASE + "/citas/profesional/" + dniCodificado;

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

                    String jsonResponse = response.toString();
                    Log.i("API_DATA", "Citas JSON: " + jsonResponse);

                    // 2. PROCESAR EL JSON DE CITAS DISPONIBLES (SOLO PENDIENTE)
                    Map<String, List<String>> horasDisponiblesEncontradas = new HashMap<>();

                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(jsonResponse);

                        // Iterar sobre cada objeto (cita)
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject citaJson = jsonArray.getJSONObject(i);

                            //  SOLO AÑADIR SI EL ESTADO ES PENDIENTE
                            String estadoCita = citaJson.getString("estado");

                            if (!"PENDIENTE".equalsIgnoreCase(estadoCita)) {
                                continue; // Ignoramos cualquier otro estado (Aceptada, Cancelada, etc.)
                            }

                            // Si es PENDIENTE, esta es una hora DISPONIBLE
                            String fechaCita = citaJson.getString("fecha"); // YYYY-MM-DD
                            String horaCita = citaJson.getString("hora");   // HH:MM:SS
                            id_cita = citaJson.getString("id");
                            cliente = citaJson.getString("id_cliente");
                            profesional = citaJson.getString("id_profesional");
                            direccion = citaJson.getString("direccion");
                            localidad = citaJson.getString("localidad");
                            cp = citaJson.getString("cp");
                            id_especialidad = citaJson.getString("id_especialidad");
                            estado = estadoCita;
                            provincia = citaJson.getString("provincia");

                            // Nos aseguramos de que la hora sea HH:MM
                            if (horaCita.length() > 5) {
                                horaCita = horaCita.substring(0, 5);
                            }

                            // Almacenar la hora DISPONIBLE
                            if (!horasDisponiblesEncontradas.containsKey(fechaCita)) {
                                horasDisponiblesEncontradas.put(fechaCita, new ArrayList<>());
                            }
                            horasDisponiblesEncontradas.get(fechaCita).add(horaCita);
                        }
                    } catch (org.json.JSONException e) {
                        Log.e("JSON_ERROR", "Error al parsear el arreglo JSON: " + e.getMessage());
                        throw new RuntimeException("JSON Parsing Failed: " + e.getMessage());
                    }

                    // 3. LA DISPONIBILIDAD ES DIRECTAMENTE EL MAPA CREADO
                    Map<String, List<String>> nuevaDisponibilidad = horasDisponiblesEncontradas;
                    String primeraFechaDisponible = null;
                    if (!nuevaDisponibilidad.isEmpty()) {
                        primeraFechaDisponible = nuevaDisponibilidad.keySet().iterator().next();
                    }

                    // 4. Actualizar la UI en el Hilo Principal
                    final String finalPrimeraFechaDisponible = primeraFechaDisponible;
                    runOnUiThread(() -> {
                        disponibilidadPorFecha = nuevaDisponibilidad;

                        if (disponibilidadPorFecha.isEmpty()) {
                            Toast.makeText(NuevaCita3.this, "No se encontró disponibilidad futura (PENDIENTE).", Toast.LENGTH_LONG).show();
                            texto3.setText("Fecha: Sin disponibilidad");
                        } else if (finalPrimeraFechaDisponible != null) {
                            // Inicializar la interfaz con la primera fecha disponible
                            try {
                                // Asignamos el valor a fechaParaAPI para que no esté vacío al confirmar
                                fechaParaAPI = finalPrimeraFechaDisponible;
                                // ... (Lógica de formato de fecha) ...
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(API_DATE_FORMAT.parse(finalPrimeraFechaDisponible));
                                fecha = UI_DATE_FORMAT.format(cal.getTime());

                                texto3.setText("Fecha: " + fecha + " (Próximo disponible)");

                                // Muestra las horas disponibles para esa primera fecha
                                actualizarHorasDisponibles(finalPrimeraFechaDisponible);
                            } catch (Exception e) {
                                Log.e("DATE_ERROR", "Error al parsear la fecha: " + e.getMessage());
                                Toast.makeText(NuevaCita3.this, "Error de formato de fecha.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    String error = "Error al cargar citas. Código: " + responseCode;
                    Log.e("API_ERROR", error);
                    runOnUiThread(() -> Toast.makeText(NuevaCita3.this, error, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("CONN_ERROR", "Error de datos: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NuevaCita3.this, "Error de datos: No se pudo procesar la información de citas.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }


}