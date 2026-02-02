package com.example.wellnessgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
/**
 * Clase: AdaptadorEspecialistas
 * Descripción: Adaptador personalizado que extiende de ArrayAdapter.
 * Su función principal es gestionar la visualización de una lista de profesionales.
 * Infla un diseño XML específico para cada fila y mapea los atributos del objeto
 * Especialista a los componentes visuales correspondientes.
 * * @author erinBrandan
 */
public class AdaptadorEspecialistas extends ArrayAdapter<Especialista> {
    private Context context;
    private List<Especialista> especialistas;
    /**
     * Constructor del adaptador.
     * @param context       Contexto de la actividad (actúa como el entorno de ejecución).
     * @param especialistas Lista de datos que se quieren mostrar.
     */
    public AdaptadorEspecialistas(Context context, List<Especialista> especialistas) {
        super(context, R.layout.item_especialista, especialistas);
        this.context = context;
        this.especialistas = especialistas;
    }
    /**
     * Método encargado de "dibujar" cada fila de la lista.
     * Se ejecuta cada vez que un elemento de la lista debe aparecer en pantalla.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.item_especialista, parent, false);
        }

        Especialista especialista = especialistas.get(position);

        TextView nombreTextView = itemView.findViewById(R.id.item_text_especialista);
        TextView especialidadTextView = itemView.findViewById(R.id.item_text2_especiallista);
        ImageView imagenImageView = itemView.findViewById(R.id.item_image_misCitas);

        nombreTextView.setText(especialista.getNombre());
        especialidadTextView.setText(especialista.getEspecialidad());
        imagenImageView.setImageResource(especialista.getImagen());

        return itemView;
    }
}
