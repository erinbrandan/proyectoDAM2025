package com.example.wellnessgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.BitmapFactory; // Necesario para BitmapFactory
import android.util.Base64; // Necesario para Base64.decode
import java.io.BufferedReader;
import java.io.InputStream;          // Necesario para InputStream
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

/**
 * Clase: MisDocumentos
 * Descripción: Permite al usuario visualizar y editar su perfil personal.
 * Gestiona la captura de fotos mediante cámara, selección desde galería,
 * almacenamiento interno de archivos, escalado de imágenes para optimización de memoria
 * y sincronización con la API REST mediante JSON y Base64.
 * * @author erinBrandan
 */
public class MisDocumentos extends AppCompatActivity {


    protected ImageButton btnCambiarImagen;
    protected ImageView imagenPerfil;
    protected ImageButton imaboton1;
    protected Button boton1;
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;
    protected TextView texto6;
    protected TextView texto7;
    protected EditText caja1;
    protected EditText caja2;
    protected EditText caja3;
    protected EditText caja4;
    protected EditText caja5;
    protected EditText caja6;
    protected Intent pasarPantalla;
    protected Uri currentImageUri = null; // Variable para almacenar la URI actual y persistente

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final String URL_BASE = "http://wellnessgo.ddns.net:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_documentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_scroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de TODAS las Vistas
        imaboton1=(ImageButton) findViewById(R.id.imaBoton1_misDocumentos);
        boton1=(Button) findViewById(R.id.boton1_misDocumentos);
        texto1=(TextView) findViewById(R.id.texto1_misDocumentos);
        texto2=(TextView) findViewById(R.id.texto2_misDocumentos);
        texto3=(TextView) findViewById(R.id.texto3_misDocumentos);
        texto4=(TextView) findViewById(R.id.texto4_misDocumentos);
        texto5=(TextView) findViewById(R.id.texto5_misDocumentos);
        texto6=(TextView) findViewById(R.id.texto6_misDocumentos);
        texto7=(TextView) findViewById(R.id.texto7_misDocumentos);
        caja1=(EditText) findViewById(R.id.caja1_misDocumentos);
        caja2=(EditText) findViewById(R.id.caja2_misDocumentos);
        caja3=(EditText) findViewById(R.id.caja3_misDocumentos);
        caja4=(EditText) findViewById(R.id.caja4_misDocumentos);
        caja5=(EditText) findViewById(R.id.caja5_misDocumentos);
        caja6=(EditText) findViewById(R.id.caja6_misDocumentos);
        imagenPerfil = (ImageView) findViewById(R.id.imagen1_misDocumentos);
        btnCambiarImagen = (ImageButton) findViewById(R.id.btn_cambiar_imagen);

        // Cargar datos guardados (texto e imagen)
        solicitarDatosUsuario();

        // Listener del botón de regreso (imaboton1)
        imaboton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegación a Principal
                pasarPantalla = new Intent(MisDocumentos.this, Principal.class);
                startActivity(pasarPantalla);
                finish();
            }
        });
        // Listener del botón para guardad en BBDD
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatosMisDocumentos();
                pasarPantalla = new Intent(MisDocumentos.this, Principal.class);
                startActivity(pasarPantalla);
                finish();
            }
        });


        // Listener del botón de cambio de imagen
        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoParaSeleccionImagen();
            }
        });
    }
// --- SOLICITAR DATOS POR GET A LA API ---

    /**
     * Realiza la llamada GET a /perfil/{dni} para cargar los datos del usuario logueado.
     */
    private void solicitarDatosUsuario() {
        String dniUsuario = getDniUsuario();

        if (dniUsuario == null) {
            Toast.makeText(this, "Error: DNI no encontrado. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // Endpoint para obtener el perfil del cliente por DNI
                URL url = new URL(URL_BASE + "/perfil/" + dniUsuario);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Éxito: Leer la respuesta JSON
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    JSONObject perfilJson = new JSONObject(response.toString());

                    // Actualizar UI en el hilo principal
                    runOnUiThread(() -> actualizarUIConDatos(perfilJson));

                } else {
                    // Error de API
                    runOnUiThread(() -> Toast.makeText(MisDocumentos.this, "Error " + responseCode + " al cargar perfil.", Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MisDocumentos.this, "Error de conexión al cargar datos.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
    /**
     * Procesa el JSON del perfil y decodifica la imagen Base64 si existe.
     * Actualiza los EditText y el ImageView con los datos recibidos del JSON.
     */
    private void actualizarUIConDatos(JSONObject perfilJson) {
        try {
            // Asumimos que la API devuelve todas las claves, y usamos .optString para campos opcionales
            caja1.setText(perfilJson.optString("nombre", ""));
            caja2.setText(perfilJson.optString("apellido1", ""));
            caja3.setText(perfilJson.optString("apellido2", ""));
            caja4.setText(perfilJson.optString("direccion", ""));
            caja5.setText(perfilJson.optString("cod_postal", ""));
            caja6.setText(perfilJson.optString("telefono", ""));

            String base64Image = perfilJson.optString("foto_base64", null); // API usa "foto_base64"

            // Cargar Imagen
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] decodedString = Base64.decode(base64Image, Base64.NO_WRAP);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    imagenPerfil.setImageBitmap(decodedByte);


                } else {
                    imagenPerfil.setImageResource(R.drawable.perfil); // Default
                }
            } else {
                imagenPerfil.setImageResource(R.drawable.perfil); // Default
            }

            Toast.makeText(this, "Datos de perfil cargados.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar los datos de la API.", Toast.LENGTH_LONG).show();
        }
    }

    // ==========================================
    // SECCIÓN 2: GESTIÓN DE CÁMARA Y GALERÍA
    // ==========================================
    /**
     * Muestra un cuadro de diálogo al usuario para elegir entre tomar una foto con la cámara
     * o seleccionar una imagen de la galería.
     */
    private void dialogoParaSeleccionImagen() {
        String[] options = {"Tomar Foto", "Elegir de Galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                abrirCamera();
            } else if (which == 1) {
                abrirGaleria();
            }
        });
        builder.show();
    }
    /**
     * Comprueba los permisos de la cámara y los solicita si es necesario.
     * Si el permiso está concedido, llama a al metodo lanzarCamara().
     */
    private void abrirCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            lanzarCamara();
        }
    }

    /**
     * Configura la cámara utilizando FileProvider para generar una URI segura.
     * Esto evita errores de seguridad en Android 7.0+.
     */
    private void lanzarCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                // 1. Crear la URI segura con FileProvider
                currentImageUri = crearArchivoTemporalDeImagen();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear el archivo para la cámara.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentImageUri != null) {
                // 2. Indicar a la cámara dónde guardar y CONCEDER el permiso de acceso
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);

                // CRUCIAL: Necesita permiso de escritura (para guardar) y LECTURA (para acceder al FileProvider)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
            }
        }
    }
    /**
     * Comprueba los permisos de almacenamiento (Galería) y los solicita si es necesario.
     * Si el permiso está concedido, llama al metodo lanzarGaleria().
     */
    private void abrirGaleria() {
        String permission;
        // Determinar el permiso de almacenamiento según la versión de Android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // Comprobar si el permiso ya está concedido.
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    PERMISSION_REQUEST_CODE + 1);
        } else {
            lanzarGaleria();
        }
    }
    /**
     * Lanza la actividad para seleccionar una imagen de la galería.
     */
    private void lanzarGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // Permiso de lectura temporal
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // --- MANEJO DE RESULTADO DE PERMISOS ---
    /**
     * Maneja el resultado de la solicitud de permisos.
     * @param requestCode Código de solicitud.
     * @param permissions Array de permisos solicitados.
     * @param grantResults Resultado de los permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) { // Cámara
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE + 1) { // Galería
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarGaleria();
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- MANEJO DE RESULTADO DE IMAGEN ---
    /**
     * Maneja el resultado de las actividades lanzadas con {@code startActivityForResult},
     * como la cámara o la galería.
     * Incluye una copia al almacenamiento interno para persistencia propia de la app.
     * @param requestCode Código de solicitud (PICK_IMAGE_REQUEST o TAKE_PHOTO_REQUEST).
     * @param resultCode Resultado de la actividad (RESULT_OK, etc.).
     * @param data Intent que contiene los datos de resultado (ej. la URI de la imagen de galería).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verificar si el resultado fue OK
        if (resultCode == RESULT_OK) {

            // --- Manejo de la Galería (PICK_IMAGE_REQUEST) ---
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {

                    java.io.InputStream inputStream = null;
                    try {
                        // Copiar la imagen externa a una URI local
                        Uri localImageUri = copyUriToInternalStorage(selectedImageUri);

                        // Leer el Bitmap de la URI local
                        inputStream = getContentResolver().openInputStream(localImageUri);
                        Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);

                        if (bitmap != null) {
                            imagenPerfil.setImageBitmap(bitmap);
                            currentImageUri = localImageUri;

                        } else {
                            throw new java.io.IOException("Error al decodificar el Bitmap (Galería).");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar/guardar la imagen de Galería.", Toast.LENGTH_LONG).show();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (java.io.IOException e) {  }
                        }
                    }
                }

                // --- Manejo de la Cámara (TAKE_PHOTO_REQUEST) ---
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                if (currentImageUri != null) {
                    Uri localImageUri = null;
                    try {
                        //Copiar la imagen temporal (content://) a una URI local permanente (file://)
                        localImageUri = copyUriToInternalStorage(currentImageUri);

                        // Cargar la imagen escalada para la UI
                        // Usamos 200x200 para la vista previa
                        Bitmap bitmap = decodificarBitmapEscalado(localImageUri, 200, 200);

                        if (bitmap != null) {
                            imagenPerfil.setImageBitmap(bitmap);
                            currentImageUri = localImageUri; // Almacena la URI local para la BBDD
                            Toast.makeText(this, "Foto cargada correctamente.", Toast.LENGTH_SHORT).show();
                        } else {
                            throw new java.io.IOException("No se pudo decodificar el Bitmap de la cámara (después de copiar).");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "ERROR: Fallo al procesar la foto de la cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        // Limpieza del temporal del FileProvider (currentImageUri es la URI temporal)
                        if (currentImageUri != null && currentImageUri.getScheme().equals("content")) {
                            getContentResolver().delete(currentImageUri, null, null);
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: URI de cámara no disponible.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    // --- MÉTODO PARA COPIAR LA IMAGEN A ALMACENAMIENTO LOCAL ---
    /**
     * Copia el contenido de una URI de origen
     * a un archivo en el almacenamiento interno de la aplicación.
     * @param externalUri La URI de origen (content:// o file://).
     * @return La URI del nuevo archivo local (file://).
     * @throws IOException Si ocurre un error de lectura/escritura.
     */
    private Uri copyUriToInternalStorage(Uri externalUri) throws IOException {
        // Generar un nombre de archivo único
        String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
        java.io.File localFile = new java.io.File(getFilesDir(), fileName);

        InputStream inputStream = null;
        java.io.FileOutputStream outputStream = null;

        try {
            inputStream = getContentResolver().openInputStream(externalUri);
            if (inputStream == null) {
                throw new IOException("No se pudo abrir el InputStream desde la URI externa.");
            }
            outputStream = new java.io.FileOutputStream(localFile);

            // Copiar bytes
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Devolver la URI del archivo local
            return Uri.fromFile(localFile);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) { /* ignorar */ }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) { /* ignorar */ }
            }
        }
    }
    /**
     * Crea un archivo JPEG temporal en el almacenamiento interno y devuelve la URI
     * segura mediante FileProvider para que la cámara pueda escribir en él.
     * @return La URI de contenido (content://) del archivo temporal.
     * @throws IOException Si falla la creación del archivo.
     */
    private Uri crearArchivoTemporalDeImagen() throws IOException {
        // Genera el archivo local (como antes)
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = getFilesDir();

        java.io.File imageFile = java.io.File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        // Devuelve la URI del FileProvider (Content URI)
        return androidx.core.content.FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".fileprovider",
                imageFile
        );
    }
    // --- MÉTODO DE CODIFICACIÓN BASE64 ---
    /**
     * Codifica la imagen de la URI proporcionada en una cadena Base64,
     * aplicando muestreo (sampling) para reducir el tamaño del Bitmap y evitar
     * errores durante el proceso de codificación para el envío a la API.
     * @param imageUri La URI de la imagen local (file://) a codificar.
     * @return La cadena Base64 de la imagen reducida, o null si falla.
     */
    private String codificarImagenABase64(Uri imageUri) {
        if (imageUri == null) {
            return null;
        }

        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            // Decodificar límites para muestreo
            // Necesitamos reabrir el stream cada vez que decodificamos
            inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Cerrar el stream de límites
            try { inputStream.close(); } catch (IOException e) { /* ignorar */ }

            // Calcular inSampleSize para reducir el tamaño al enviar (Ej: 1/8 del tamaño)
            // Para codificación usamos un tamaño de 512x512px .
            options.inSampleSize = calcularInSampleSize(options, 512, 512);
            options.inJustDecodeBounds = false;

            // Decodificar el Bitmap reducido
            inputStream = getContentResolver().openInputStream(imageUri); // Reabrir el stream
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            }

            if (bitmap != null) {
                java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

                // Comprimir el Bitmap. Usamos 70% de calidad.
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

                byte[] imageBytes = outputStream.toByteArray();

                // 5. Devolver la cadena Base64
                return android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error de codificación Base64: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) { }
            }
        }
        return null;
    }
    // --- MÉTODO PARA ENVIAR TODOS LOS DATOS A LA API ---
    /**
     * Envía todos los datos del formulario (incluida la imagen Base64) a la API
     * mediante una solicitud HTTP PUT.
     */
    private void enviarDatosMisDocumentos() {

        // Obtener y codificar la imagen
        String imagenBase64String = codificarImagenABase64(currentImageUri);

        // Recopilar todos los datos
        String nombre = caja1.getText().toString().trim();
        String apellido1 = caja2.getText().toString().trim();
        String apellido2 = caja3.getText().toString().trim();
        String direccion = caja4.getText().toString().trim();
        String codigoPostal = caja5.getText().toString().trim();
        String telefono = caja6.getText().toString().trim();
        String dniUsuario = getDniUsuario();

        if (dniUsuario == null) {
            runOnUiThread(() -> Toast.makeText(MisDocumentos.this, "Error: No se encontró el DNI del usuario. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show());
            return;
        }



        // Iniciar el hilo de red
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // Crear el objeto JSON con los datos
                JSONObject datosDocumentos = new JSONObject();
                datosDocumentos.put("dni", dniUsuario);
                datosDocumentos.put("nombre", nombre);
                datosDocumentos.put("apellido1", apellido1);
                datosDocumentos.put("apellido2", apellido2);
                datosDocumentos.put("direccion", direccion);
                datosDocumentos.put("codigoPostal", codigoPostal);
                datosDocumentos.put("telefono", telefono);
                // Si la imagen es nula, enviamos una cadena vacía o nula.
                datosDocumentos.put("foto_perfil_base64", imagenBase64String != null ? imagenBase64String : "");



                URL url = new URL(URL_BASE + "/perfil");

                // Configurar la Conexión HTTP (PUT para actualización)
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Enviar el JSON
                OutputStream os = conn.getOutputStream();
                os.write(datosDocumentos.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                // Leer la Respuesta
                int responseCode = conn.getResponseCode();
                String finalMsg;

                if (responseCode >= 200 && responseCode < 400) {
                    // Éxito
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    finalMsg = "Guardado exitoso (" + responseCode + "): " + response.toString();

                    runOnUiThread(() -> {
                        Toast.makeText(MisDocumentos.this, "Datos guardados correctamente.", Toast.LENGTH_LONG).show();
                    });
                } else {
                    // Error (4xx o 5xx)
                    InputStream is = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    String mensajeErrorServidor = response.toString();
                    try {
                        JSONObject errorJson = new JSONObject(response.toString());
                        if (errorJson.has("error")) mensajeErrorServidor = errorJson.getString("error");
                        else if (errorJson.has("mensaje")) mensajeErrorServidor = errorJson.getString("mensaje");
                    } catch (Exception jsonE) {  }

                    finalMsg = "Error en el servidor (" + responseCode + "): " + response.toString();
                    System.out.println(finalMsg);

                    final String toastText = "Error al guardar (" + responseCode + "): " + mensajeErrorServidor;

                    runOnUiThread(() -> {
                        Toast.makeText(MisDocumentos.this, toastText, Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Error de red
                runOnUiThread(() -> Toast.makeText(MisDocumentos.this, "Error de conexión: Verifica la red y la API.", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
    // --- MÉTODO PARA OBTENER EL DNI GUARDADO ---
    /**
     * Obtiene el DNI del usuario logueado almacenado en SharedPreferences.
     * @return El DNI del usuario o null si no se encuentra.
     */
    private String getDniUsuario() {

        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString("USER_ID", null);
    }
    /**
     * Decodifica un Bitmap desde una URI, aplicando muestreo para reducir su tamaño
     * y evitar errores, ajustándose a las dimensiones deseadas.
     * @param imageUri URI de la imagen de origen.
     * @param anchoDeseado Ancho objetivo (en píxeles) para la visualización.
     * @param altoDeseado Alto objetivo (en píxeles) para la visualización.
     * @return El Bitmap reducido.
     * @throws IOException Si falla la lectura del InputStream.
     */
    private Bitmap decodificarBitmapEscalado(Uri imageUri, int anchoDeseado, int altoDeseado) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        if (inputStream == null) return null;

        // Primero decodificar solo los límites (inJustDecodeBounds=true)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Cerrar el stream después de la primera lectura
        try { inputStream.close(); } catch (IOException e) { /* ignorar */ }

        // Calcular inSampleSize
        options.inSampleSize = calcularInSampleSize(options, anchoDeseado, altoDeseado);

        // Decodificar el Bitmap final con inSampleSize
        options.inJustDecodeBounds = false;
        inputStream = getContentResolver().openInputStream(imageUri); // Reabrir el stream
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        try { inputStream.close(); } catch (IOException e) { /* ignorar */ }
        return bitmap;
    }

    /**
     * Calcula el valor de {@code inSampleSize} que es la potencia de 2 más grande
     * y que mantendrá el ancho y la altura del bitmap mayores o iguales a las dimensiones
     * solicitadas.
     * @param options Opciones de BitmapFactory que contienen las dimensiones originales (outHeight, outWidth).
     * @param reqWidth Ancho deseado.
     * @param reqHeight Altura deseada.
     * @return El factor de muestreo (siempre potencia de 2).
     */
    private int calcularInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Altura y ancho originales
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calcula el inSampleSize más grande que aún es una potencia de 2 y que mantendrá ambas dimensiones más grandes que el objetivo.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}