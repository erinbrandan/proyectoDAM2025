package com.example.wellnessgo;

import static org.junit.Assert.*;
import org.junit.Test;
/**
 * Nombre del paquete: com.example.wellnessgo
 * Nombre de la clase: ValidadorRegistroTest
 * * Descripción:
 * Clase de pruebas unitarias (Unit Tests) diseñada para verificar la robustez de la lógica
 * de validación en el registro de usuarios. Utiliza el framework JUnit 4 para ejecutar
 * diferentes casos de prueba, asegurando que el sistema responda correctamente ante
 * datos válidos e inválidos.
 * * Casos de Prueba Cubiertos:
 * - Registro exitoso con todos los campos válidos.
 * - Rechazo de campos con cadenas vacías ("").
 * - Gestión de valores nulos (null safety).
 * - Identificación de entradas compuestas únicamente por espacios en blanco.
 * * @author erinBrandan
 */
public class ValidadorRegistroTest {

    @Test
    public void testCamposCorrectos() {
        assertTrue(ValidadorRegistro.validarCampos("123", "Juan", "juan@test.com", "1234"));
    }

    @Test
    public void testCampoVacio() {
        assertFalse(ValidadorRegistro.validarCampos("", "Juan", "juan@test.com", "1234"));
    }

    @Test
    public void testCampoNulo() {
        assertFalse(ValidadorRegistro.validarCampos(null, "Juan", "juan@test.com", "1234"));
    }

    @Test
    public void testEspacios() {
        assertFalse(ValidadorRegistro.validarCampos("   ", "Juan", "juan@test.com", "1234"));
    }
}

