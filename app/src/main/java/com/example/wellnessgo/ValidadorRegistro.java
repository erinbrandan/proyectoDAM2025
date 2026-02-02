package com.example.wellnessgo;
/**
 * Nombre del paquete: com.example.wellnessgo
 * Nombre de la clase: ValidadorRegistro
 * * Descripción:
 * Esta clase de utilidad proporciona lógica de validación para el proceso de registro de usuarios.
 * Su objetivo principal es asegurar la integridad de los datos antes de que sean procesados
 * por el controlador o enviados a la API externa.
 * * Responsabilidades:
 * - Verificar que los campos obligatorios (DNI, Nombre, Email, Contraseña) no sean nulos.
 * - Comprobar que las cadenas de texto no estén vacías ni contengan únicamente espacios en blanco.
 * * Patrón de diseño:
 * Utiliza métodos estáticos (Utility Class) para permitir su uso sin necesidad de instanciar la clase.
 * * @author erinBrandan
 */
public class ValidadorRegistro {

    public static boolean validarCampos(String dni, String nombre, String email, String contrasena) {
        return !(dni == null || dni.trim().isEmpty() ||
                nombre == null || nombre.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                contrasena == null || contrasena.trim().isEmpty());
    }
}