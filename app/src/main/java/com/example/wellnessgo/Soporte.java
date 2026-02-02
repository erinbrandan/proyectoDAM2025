package com.example.wellnessgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
 * Clase: Soporte
 * Descripción: Actividad dedicada a la asistencia al usuario.
 * Proporciona acceso a una plataforma externa de preguntas frecuentes (FAQs)
 * mediante el navegador del sistema y permite el contacto directo con el
 * equipo administrador a través de la aplicación de correo electrónico predeterminada.
 * @author erinBrandan
 */
public class Soporte extends AppCompatActivity {
    protected ImageButton imaBoton1;
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;
    protected TextView texto6;
    protected ImageView ima1;
    protected ImageView ima2;
    protected LinearLayout layoutFAQs;
    protected LinearLayout layoutContacto;
    private static final String URL_FAQS = "https://fastidious-boba-c8ddb1.netlify.app/";

    private static final String EMAIL_CONTACTO = "admin@wellnessgo.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_soporte);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_soporte);
        texto1=(TextView) findViewById(R.id.texto1_soporte);
        texto2=(TextView) findViewById(R.id.texto2_soporte);
        texto3=(TextView) findViewById(R.id.texto3_soporte);
        texto4=(TextView) findViewById(R.id.texto4_soporte);
        texto5=(TextView) findViewById(R.id.texto5_soporte);
        texto6=(TextView) findViewById(R.id.texto6_soporte);
        ima1=(ImageView) findViewById(R.id.ima1_soporte);
        ima2=(ImageView) findViewById(R.id.ima2_soporte);
        layoutFAQs=(LinearLayout) findViewById(R.id.linearLayout_FAQs);
        layoutContacto=(LinearLayout) findViewById(R.id.linearLayout_Contacto);
        /*
         * Listener de cierre de actividad.
         */
        imaBoton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegación de regreso
                finish();
            }
        });
        /*
         * Listener para abrir la sección de Preguntas Frecuentes.
         */
        layoutFAQs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirUrlExterna(URL_FAQS);
            }
        });
        /*
         * Listener para iniciar el proceso de envío de correo electrónico.
         */
        layoutContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEmailContacto(EMAIL_CONTACTO);
            }
        });
    }
    /**
     * Ejecuta un Intent implícito para abrir una URL en el navegador del dispositivo.
     * @param url Cadena con la dirección web de destino.
     */
    private void abrirUrlExterna(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Muestra un mensaje si no se encuentra un navegador o hay otro error
            Toast.makeText(this, "No se encontró una aplicación para abrir la URL.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    /**
     * Ejecuta un Intent implícito (ACTION_SENDTO) para abrir la aplicación de correo en el propio dispositivo movil.
     * Utiliza el esquema "mailto:" para filtrar aplicaciones de mensajería no compatibles.
     * @param email Dirección de correo electrónico de soporte.
     */
    private void abrirEmailContacto(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // Usamos setData(Uri.parse("mailto:")) para que solo las apps de correo manejen el Intent
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte App WellnessGo");

        try {
            startActivity(intent);
        } catch (Exception e) {
            // Muestra un mensaje si no se encuentra una aplicación de correo
            Toast.makeText(this, "No se encontró una aplicación de correo instalada.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}