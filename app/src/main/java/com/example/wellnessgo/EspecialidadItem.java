package com.example.wellnessgo;
/**
 * Clase: EspecialidadItem
 * Descripción: Representa una categoría médica o servicio dentro de la aplicación.
 * Se utiliza principalmente para poblar componentes de selección (como Spinners)
 * permitiendo que el usuario vea el nombre de la especialidad mientras la aplicación
 * gestiona internamente su ID numérico para las consultas a la API.
 * @author erinBrandan
 */
public class EspecialidadItem {
    private int id;
    private String nombre;

    public EspecialidadItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
