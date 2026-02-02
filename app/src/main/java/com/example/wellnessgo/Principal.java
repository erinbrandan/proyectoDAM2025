package com.example.wellnessgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
/**
 * Clase: Principal
 * Descripción: Actúa como el panel de control (Dashboard) de la aplicación.
 * Proporciona acceso directo a las funcionalidades clave como la reserva de citas,
 * consulta de historial médico y documentos. Gestiona la navegación principal
 * mediante un BottomNavigationView y un menú de opciones superior.
 * * @author erinBrandan
 * @version 1.0
 */
public class Principal extends AppCompatActivity {
    protected TextView texto1;
    protected TextView texto2;
    protected TextView texto3;
    protected TextView texto4;
    protected TextView texto5;
    protected TextView texto6;
    protected TextView texto7;
    protected ImageView ima1;
    protected ImageView ima2;
    protected ImageView ima3;
    protected ImageButton imaBoton1;
    protected LinearLayout layout1;
    protected LinearLayout layout2;
    protected LinearLayout layout3;
    protected Intent pasarPantalla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        texto1=(TextView) findViewById(R.id.texto1_principal);
        texto2=(TextView) findViewById(R.id.texto2_principal);
        texto3=(TextView) findViewById(R.id.texto3_principal);
        texto4=(TextView) findViewById(R.id.texto4_principal);
        texto5=(TextView) findViewById(R.id.texto5_principal);
        texto6=(TextView) findViewById(R.id.texto6_principal);
        texto7=(TextView) findViewById(R.id.texto7_principal);
        ima1=(ImageView)   findViewById(R.id.ima1_principal);
        ima2=(ImageView)   findViewById(R.id.ima2_principal);
        ima3=(ImageView)   findViewById(R.id.ima3_principal);
        layout1=(LinearLayout) findViewById(R.id.layoutBusquedaDoctor_principal);
        layout2=(LinearLayout) findViewById(R.id.layoutMisCitas_principal);
        layout3=(LinearLayout) findViewById(R.id.layoutMisDocumentos_principal);
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_principal);

        /*
         * Listener para la sección de Nueva Cita.
         */
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla= new Intent(com.example.wellnessgo.Principal.this, NuevaCita.class);
                startActivity(pasarPantalla);
            }
        });
        /*
         * Listener para la sección de Mis Citas.
         */
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla= new Intent(com.example.wellnessgo.Principal.this, MisCitas.class);
                startActivity(pasarPantalla);
            }
        });
        /*
         * Listener para la sección de Mis Documentos.
         */
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla= new Intent(com.example.wellnessgo.Principal.this, MisDocumentos.class);
                startActivity(pasarPantalla);
            }
        });
        /*
         * Listener para el botón de Soporte.
         */
        imaBoton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantalla = new Intent(Principal.this, Soporte.class);
                startActivity(pasarPantalla);
                finish();
            }
        });
        /*
         * Configuración del menú de navegación inferior (BottomNavigationView).
         */
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                // mostrar pantalla inicio
                return true;
            } else if (id == R.id.nav_citas) {
                pasarPantalla= new Intent(com.example.wellnessgo.Principal.this, NuevaCita.class);
                startActivity(pasarPantalla);
                return true;
            } else if (id == R.id.nav_perfil) {
                // mostrar perfil
                return true;
            }

            return false;
        });




    }
    /**
     * Crea el menú de opciones en la parte superior de la actividad.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }
    /**
     * Gestiona los clics en los elementos del menú superior (Action Bar).
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id= item.getItemId();
        if (id==R.id.item_ajustes_menu_principal)
        {
           return true;
        }

        else
        {
            return super.onOptionsItemSelected(item);
        }

    }

}