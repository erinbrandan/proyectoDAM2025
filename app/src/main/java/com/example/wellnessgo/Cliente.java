package com.example.wellnessgo;
/**
 * Clase: Cliente
 * Descripción: Modelo de datos que representa al usuario de la aplicación WellnessGo.
 * Contiene la información básica de identificación y credenciales necesaria para
 * los procesos de autenticación, registro y gestión de sesiones.
 * Aunque no se usa actualmente se planea usarla en la versión 2.0 de la aplicación.
 *  @author erinbrandan
 */
public class Cliente {


    private String dni;
    private String nombre;
    private String email;
    private String contrasena;

    public Cliente(String dni, String nombre, String email, String contrasena) {
        this.dni=dni;
        this.nombre=nombre;
        this.email=email;
        this.contrasena=contrasena;
        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
