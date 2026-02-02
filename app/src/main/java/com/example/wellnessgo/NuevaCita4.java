package com.example.wellnessgo;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
 * Clase: NuevaCita4
 * Descripción: Cuarta fase del proceso de reserva de citas.
 * En esta pantalla se solicita al usuario un breve resumen o motivo de la consulta
 * a través de un campo de texto. Recopila toda la información acumulada de las
 * fases anteriores (especialidad, especialista, fecha y hora) y la traslada a la
 * última actividad de confirmación.
 * * @author erinBrandan
 */
public class NuevaCita4 extends AppCompatActivity {
    protected TextView texto1;
    protected TextView texto2;
    protected ImageButton imaBoton1;
    protected EditText caja1;
    protected Button boton1;
    protected Bundle extras;
    protected String paquete1;
    protected String paquete2;
    protected String paquete3;
    protected String paquete4;
    protected String paquete5;
    protected String paquete6;
    protected String paquete7;
    protected String paquete8;
    protected String paquete9;
    protected String paquete10;
    protected String paquete11;
    protected String paquete12;
    protected String contenidoCaja1="";
    protected Intent pasarPantalla;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_cita4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imaBoton1=(ImageButton) findViewById(R.id.imaBoton1_nuevaCita4);
        texto1=(TextView) findViewById(R.id.texto1_nuevaCita4);
        texto2=(TextView)  findViewById(R.id.texto2_nuevaCita4);
        caja1=(EditText) findViewById(R.id.caja1_nuevaCita4);
        boton1=(Button)findViewById(R.id.boton1_nuevaCita4);



        extras = getIntent().getExtras();
        if(extras!=null){
            paquete1=extras.getString("ESPECIALIDAD");
            paquete2=extras.getString("PROFESIONAL_ID");
            paquete3=extras.getString("PROFESIONAL");
            paquete4=extras.getString("FECHA");
            paquete5=extras.getString("HORA");
            paquete6=extras.getString("DIRECCION");
            paquete7=extras.getString("LOCALIDAD");
            paquete8=extras.getString("CP");
            paquete9=extras.getString("ESPECIALIDAD_ID");
            paquete10=extras.getString("ESTADO");
            paquete11=extras.getString("PROVINCIA");
            paquete12=extras.getString("ID_CITA");
        }
        else{
            Toast.makeText(NuevaCita4.this,  "No se ha recibido ningún paquete", Toast.LENGTH_SHORT).show();
        }
        /*
         * Listener para el botón de continuar.
         * Captura el motivo de la consulta y realiza el paso de todos los parámetros a NuevaCita5.
         */
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoCaja1=caja1.getText().toString();
                //Toast.makeText(NuevaCita4.this, contenidoCaja1, Toast.LENGTH_SHORT).show();

                pasarPantalla= new Intent(NuevaCita4.this,NuevaCita5.class);
                pasarPantalla.putExtra("ESPECIALIDAD",paquete1);
                pasarPantalla.putExtra("PROFESIONAL_ID", paquete2);
                pasarPantalla.putExtra("PROFESIONAL",paquete3);
                pasarPantalla.putExtra("FECHA",paquete4);
                pasarPantalla.putExtra("HORA",paquete5);
                pasarPantalla.putExtra("DIRECCION",paquete6);
                pasarPantalla.putExtra("LOCALIDAD",paquete7);
                pasarPantalla.putExtra("CP",paquete8);
                pasarPantalla.putExtra("ESPECIALIDAD_ID",paquete9);
                pasarPantalla.putExtra("ESTADO",paquete10);
                pasarPantalla.putExtra("PROVINCIA",paquete11);
                pasarPantalla.putExtra("RESUMEN",contenidoCaja1);
                pasarPantalla.putExtra("ID_CITA",paquete12);
                startActivity(pasarPantalla);
                finish();

            }
        });
    }
}