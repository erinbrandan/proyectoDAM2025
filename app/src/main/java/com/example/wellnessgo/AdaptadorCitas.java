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
 * Clase: AdaptadorCitas
 * Descripción: Adaptador personalizado encargado de gestionar la visualización del
 * historial de citas del usuario. Mapea la información detallada de cada reserva
 * (especialidad, profesional, fecha y hora) en un diseño de tarjeta personalizado.
 * Implementa el reciclaje de vistas para asegurar un rendimiento fluido durante el scroll.
 * @author erinBrandan
 */
public class AdaptadorCitas extends ArrayAdapter<Cita> {
    private Context context;
    private List<Cita> citas;
    /**
     * Constructor del adaptador.
     * @param context Contexto de la actividad (necesario para el inflador).
     * @param citas   Lista de objetos tipo Cita obtenidos generalmente de la API.
     */
    public AdaptadorCitas(Context context, List<Cita> citas) {
        super(context, R.layout.item_citas, citas);
        this.context = context;
        this.citas = citas;
    }
    /**
     * Método getView: Construye y rellena la vista de cada fila de la lista.
     * Es invocado automáticamente por el ListView para cada elemento visible.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            // Infla el nuevo layout para cada ítem de la lista si es la primera vez
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.item_citas, parent, false);
        }

        // Obtiene el objeto Cita para esta posición
        Cita cita = citas.get(position);

        // 1. Vincula los TextViews y ImageView con sus IDs en item_cita.xml
        TextView especialidadTextView = itemView.findViewById(R.id.item_text1_misCitas);
        TextView nombreTextView = itemView.findViewById(R.id.item_text2_misCitas);
        TextView fechaTextView = itemView.findViewById(R.id.item_text3_misCitas);
        TextView horaTextView = itemView.findViewById(R.id.item_text4_misCitas);
        ImageView imagenImageView = itemView.findViewById(R.id.item_image_misCitas);

        // 2. Asigna los datos del objeto Cita a los elementos de la interfaz
        especialidadTextView.setText(cita.getEspecialidad());
        nombreTextView.setText(cita.getEspecialista());
        fechaTextView.setText(cita.getFecha());
        horaTextView.setText(cita.getHora());
        imagenImageView.setImageResource(cita.getImagen());

        return itemView;
    }
}
