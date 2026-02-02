package com.example.wellnessgo;
/**
 * Clase: Cita
 * Descripción: Modelo de datos que representa una reserva médica completa.
 * Esta clase agrupa la información del profesional, la especialidad y el bloque
 * temporal (fecha/hora), además de un identificador único para la gestión
 * con la API y una referencia visual para la interfaz de usuario.
 *  @author erinBrandan
 */
public class Cita {
    private String idCita;
    private String especialista;
    private String especialidad;
    private String fecha;
    private String hora;
    private int imagen;


    /**
     * Constructor para inicializar una instancia de Cita.
     * @param idCita       ID único generado por la base de datos.
     * @param especialista Nombre del médico asignado.
     * @param especialidad Rama médica de la consulta.
     * @param fecha        Día programado.
     * @param hora         Tramo horario reservado.
     * @param imagen       Imagen que se mostrará en el listado de citas.
     */

    public Cita(String idCita, String especialista, String especialidad, String fecha, String hora, int imagen) {
        this.idCita = idCita;
        this.especialista = especialista;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.imagen = imagen;
    }
    // ==========================================
    // MÉTODOS ACCESORES (GETTERS Y SETTERS)
    // ==========================================
    public String getEspecialista() {
        return especialista;
    }

    public void setEspecialista(String especialista) {
        this.especialista = especialista;
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}


