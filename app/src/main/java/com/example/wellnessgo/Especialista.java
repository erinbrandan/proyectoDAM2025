package com.example.wellnessgo;
/**
 * Clase: Especialista
 * Descripción: Modelo de datos (POJO) que representa a un profesional sanitario.
 * Esta clase se utiliza para mapear la información proveniente de la base de datos
 * (DNI, nombre, especialidad) y asociarla a un recurso visual para ser mostrada
 * en componentes de tipo AdapterView (como el ListView en NuevaCita2).
 * * @author erinBrandan
 */
public class Especialista {
    private String id;
    private String nombre;
    private String especialidad;
    private int imagen;
    /**
     * Constructor completo para inicializar un objeto Especialista.
     * * @param id Identificador único.
     * @param nombre Nombre del médico.
     * @param especialidad Nombre de la categoría médica.
     * @param imagen Referencia al recurso drawable (ej: R.drawable.doctor).
     */
    public Especialista(String id, String nombre, String especialidad, int imagen) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}


