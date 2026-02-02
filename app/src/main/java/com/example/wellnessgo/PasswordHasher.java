package com.example.wellnessgo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    /**
     * Genera un hash SHA-256 de una cadena de entrada.
     *
     * @param password La contraseña en texto plano.
     * @return El hash SHA-256 de la contraseña o null si falla el algoritmo.
     */
    public static String hashPassword(String password) {
        try {
            // Obtener una instancia del algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Aplicar el hash
            byte[] hash = digest.digest(password.getBytes("UTF-8"));

            // Convertir el array de bytes a una representación hexadecimal (String)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                // Añadir un 0 delante si es necesario para asegurar dos dígitos
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
             return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}