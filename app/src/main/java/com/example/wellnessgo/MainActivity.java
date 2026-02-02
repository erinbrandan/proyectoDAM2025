package com.example.wellnessgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

    /**
     * Clase: MainActivity
     * Descripción: Actividad principal que actúa como "Splash Screen" (Pantalla de bienvenida).
     * Su función es mostrar el logo de la aplicación durante un tiempo determinado antes de
     * redirigir automáticamente al usuario a la pantalla de Inicio de Sesión (Login).
     * * @author erinBrandan
     * @version 1.0
     */
    public class MainActivity extends AppCompatActivity {
        protected ImageView ima1;
        protected Intent pasarPantalla;
        protected TimerTask tt;
        protected Timer t;



        /**
         * Método que se ejecuta al crear la actividad.
         * Configura la interfaz visual y establece el temporizador para el cambio de pantalla.
         * * @param savedInstanceState Estado guardado de la aplicación en caso de recreación.
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            // Inicialización de componentes de la vista
            ima1=(ImageView) findViewById(R.id.imagenSplash);
            /* * Configuración del TimerTask:
             * Define la acción a ejecutar (cambio de Activity) tras cumplirse el tiempo.
             */
            tt=new TimerTask() {
                @Override
                public void run() {
                    pasarPantalla= new Intent(com.example.wellnessgo.MainActivity.this, Login.class);
                    startActivity(pasarPantalla);
                    finish();
                }
            };
            // Inicia el contador para que se ejecute en 3000 milisegundos
            t=new Timer();
            t.schedule(tt,3000);

        }
    }
