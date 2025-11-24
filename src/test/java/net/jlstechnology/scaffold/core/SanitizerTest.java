package net.jlstechnology.scaffold.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Valida o comportamento das rotinas de sanitização de identificadores.
 */
class SanitizerTest {

    @Test
    @DisplayName("Deve remover caracteres inválidos e manter letras minúsculas")
    void sanitizePackageSegment() {
        String sanitized = Sanitizer.sanitizePackageSegment("sync-cta-app");
        assertEquals("syncctaapp", sanitized);
    }

    @Test
    @DisplayName("Deve converter artefato em PascalCase")
    void toPascalCase() {
        String pascal = Sanitizer.toPascalCase("sync-cta-app");
        assertEquals("SyncCtaApp", pascal);
    }
}

