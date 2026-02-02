package com.example.wellnessgo;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Nombre del paquete: com.example.wellnessgo
 * Nombre de la clase: ValidadorNuevaCita5Test
 * * Descripción:
 * Suite de pruebas unitarias (Unit Tests) para la validación del proceso final de reserva de citas.
 * Esta clase utiliza JUnit 4 para verificar la robustez de ValidadorNuevaCita5, asegurando
 * que el sistema gestione correctamente el formato de fecha ISO (YYYY-MM-DD) y la
 * obligatoriedad de los campos antes de la persistencia en la API.
 * * Escenarios de Prueba Cubiertos:
 * - Validación sintáctica de fechas (formatos correctos vs incorrectos).
 * - Integridad de los parámetros obligatorios (not null / not empty).
 * - Control de entradas de texto malformadas (espacios en blanco).
 * - Simulación de fallos en la recepción de datos provenientes de Intents/Extras.
 * * @author erinBrandan
 */
public class ValidadorNuevaCita5Test {

    // Datos válidos de ejemplo para una cita
    private static final String VALOR_VALIDO_ESPECIALIDAD = "Medicina General";
    private static final String VALOR_VALIDO_ESPECIALISTA = "Dr. Perez";
    private static final String VALOR_VALIDO_FECHA = "2025-12-01";
    private static final String VALOR_VALIDO_FECHA_FORMATO = "2025-12-01"; // Formato YYYY-MM-DD
    private static final String VALOR_VALIDO_HORA = "10:00";
    private static final String VALOR_VALIDO_RESUMEN = "Chequeo anual";
    private static final String VALOR_VALIDO_ID = "usuario@test.com";
    private static final String VALOR_VALIDO_ID_ESPECIALISTA = "ESP-123";

    @Test
    public void testFormatoFechaCorrecto() {
        assertTrue(ValidadorNuevaCita5.validarFormatoFecha(VALOR_VALIDO_FECHA_FORMATO));
    }

    @Test
    public void testFormatoFechaIncorrecto_DDMMYYYY() {
        // Formato común, pero incorrecto para YYYY-MM-DD
        assertFalse(ValidadorNuevaCita5.validarFormatoFecha("01/12/2025"));
    }
    // Prueba 1: Todos los campos correctos (Éxito)
    @Test
    public void testCamposCorrectos() {
        assertTrue(ValidadorNuevaCita5.validarCamposCita(
                VALOR_VALIDO_ESPECIALIDAD,
                VALOR_VALIDO_ESPECIALISTA,
                VALOR_VALIDO_FECHA,
                VALOR_VALIDO_HORA,
                VALOR_VALIDO_RESUMEN,
                VALOR_VALIDO_ID,
                VALOR_VALIDO_ID_ESPECIALISTA
        ));
    }

    // Prueba 2: Fallo con un campo nulo (ID Cliente)
    @Test
    public void testCampoNulo_idCliente() {
        assertFalse(ValidadorNuevaCita5.validarCamposCita(
                VALOR_VALIDO_ESPECIALIDAD,
                VALOR_VALIDO_ESPECIALISTA,
                VALOR_VALIDO_FECHA,
                VALOR_VALIDO_HORA,
                VALOR_VALIDO_RESUMEN,
                null, // ID NULO
                VALOR_VALIDO_ID_ESPECIALISTA
        ));
    }

    // Prueba 3: Fallo con una cadena vacía (Especialidad)
    @Test
    public void testCampoVacio_especialidad() {
        assertFalse(ValidadorNuevaCita5.validarCamposCita(
                "", // ESPECIALIDAD VACÍA
                VALOR_VALIDO_ESPECIALISTA,
                VALOR_VALIDO_FECHA,
                VALOR_VALIDO_HORA,
                VALOR_VALIDO_RESUMEN,
                VALOR_VALIDO_ID,
                VALOR_VALIDO_ID_ESPECIALISTA
        ));
    }

    // Prueba 4: Fallo con un campo de solo espacios (Hora)
    @Test
    public void testSoloEspacios_hora() {
        assertFalse(ValidadorNuevaCita5.validarCamposCita(
                VALOR_VALIDO_ESPECIALIDAD,
                VALOR_VALIDO_ESPECIALISTA,
                VALOR_VALIDO_FECHA,
                "   ", // HORA SOLO ESPACIOS
                VALOR_VALIDO_RESUMEN,
                VALOR_VALIDO_ID,
                VALOR_VALIDO_ID_ESPECIALISTA
        ));
    }

    // Prueba 5: Fallo con un campo nulo que viene de los extras (Fecha)
    @Test
    public void testCampoNulo_fecha() {
        assertFalse(ValidadorNuevaCita5.validarCamposCita(
                VALOR_VALIDO_ESPECIALIDAD,
                VALOR_VALIDO_ESPECIALISTA,
                null, // FECHA NULA
                VALOR_VALIDO_HORA,
                VALOR_VALIDO_RESUMEN,
                VALOR_VALIDO_ID,
                VALOR_VALIDO_ID_ESPECIALISTA
        ));
    }
}