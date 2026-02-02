package com.example.wellnessgo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
/**
 * Clase: ValidadorNuevaCita5
 * Descripción: Clase de utilidad encargada de centralizar la lógica de validación
 * de datos antes de su envío a la API REST. Verifica la integridad de los campos
 * obligatorios y asegura que la fecha cumpla con el estándar ISO (YYYY-MM-DD).
 * * Nota: Requiere API 26 (Android Oreo) o superior por el uso de java.time.
 * * @author erinBrandan
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ValidadorNuevaCita5 {

    // Define el formato esperado para la fecha, por ejemplo: AAAA-MM-DD
    private static final String FORMATO_FECHA_ESPERADO = "yyyy-MM-dd";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATO_FECHA_ESPERADO)
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Valida que una cadena de texto corresponda al formato de fecha esperado (YYYY-MM-DD).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean validarFormatoFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return false; // Ya cubierta por validarCamposCita, pero se añade por seguridad.
        }
        try {
            // Intentamos parsear la fecha usando el formato definido

                FORMATTER.parse(fecha.trim());

            return true;
        } catch (DateTimeParseException e) {
            // Si hay una excepción al parsear, el formato es incorrecto
            return false;
        }
    }
    /**
     * Valida que todos los campos requeridos para la cita
     * (Especialidad, Especialista, Fecha, Hora, Resumen y ID del Cliente)
     * sean válidos (no nulos, vacíos o solo espacios).
     *
     * @return true si todos los campos son válidos; false en caso contrario.
     */
    public static boolean validarCamposCita(String especialidad, String especialista, String fecha,
                                            String hora, String resumen, String idCliente,
                                            String idEspecialista) {

        // Se verifica que ninguna de las cadenas sea null o esté vacía/solo espacios después de trim()
        return !(especialidad == null || especialidad.trim().isEmpty() ||
                especialista == null || especialista.trim().isEmpty() ||
                fecha == null || fecha.trim().isEmpty() ||
                hora == null || hora.trim().isEmpty() ||
                resumen == null || resumen.trim().isEmpty() ||
                idCliente == null || idCliente.trim().isEmpty() ||
                idEspecialista == null || idEspecialista.trim().isEmpty());
    }
}