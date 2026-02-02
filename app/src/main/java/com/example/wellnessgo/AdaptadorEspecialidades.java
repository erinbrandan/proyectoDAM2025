package com.example.wellnessgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
/**
 * Clase: AdaptadorEspecialidades
 * Descripción: Adaptador personalizado para mostrar las categorías de servicios (Fisioterapia,
 * Nutrición, etc.). Utiliza arrays de Strings e Integers para mapear los nombres de las
 * especialidades con sus iconos correspondientes en la interfaz de usuario.
 *  @author erinBrandan
 */
public class AdaptadorEspecialidades extends ArrayAdapter<String> {
        private Context context;
        private String[] textos;
        private int[] imagenes;
        /**
         * Constructor del adaptador.
         * @param context  Contexto de la actividad.
         * @param textos   Array con los nombres de las especialidades.
         * @param imagenes Array con los IDs de los recursos (R.drawable) de las imágenes.
         */
        public AdaptadorEspecialidades(Context context, String[] textos, int[] imagenes) {
            super(context, R.layout.item_especialidad, textos);
            this.context = context;
            this.textos = textos;
            this.imagenes = imagenes;
        }
    /**
     * Método getView: Se encarga de construir la vista para cada elemento del array.
     * Vincula el texto y la imagen en la posición correspondiente.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_especialidad, parent, false);

        TextView textView = itemView.findViewById(R.id.item_text_especialista);
        ImageView imageView = itemView.findViewById(R.id.item_image_misCitas);

        textView.setText(textos[position]);
        imageView.setImageResource(imagenes[position]);

        return itemView;
    }
}



