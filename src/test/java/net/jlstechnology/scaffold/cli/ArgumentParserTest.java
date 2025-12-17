package net.jlstechnology.scaffold.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifica a interpretação dos argumentos informados na linha de comando.
 */
class ArgumentParserTest {

    private final ArgumentParser parser = new ArgumentParser();

    @Test
    @DisplayName("Deve interpretar argumentos completos")
    void shouldParseArguments() {
        String[] args = {
            "--groupId", "net.jlstechnology",
            "--artifactId", "sync-cta",
            "--output-dir", "/tmp",
            "--force"
        };

        CliArguments result = parser.parse(args);

        assertEquals("net.jlstechnology", result.groupId());
        assertEquals("sync-cta", result.artifactId());
        assertEquals(Path.of("/tmp"), result.outputDir());
        assertTrue(result.force());
        assertNull(result.cloud());
    }

    @Test
    @DisplayName("Deve interpretar argumento --cloud aws")
    void shouldParseCloudAws() {
        String[] args = {
            "--groupId", "net.jlstechnology",
            "--artifactId", "sync-cta",
            "--cloud", "aws"
        };

        CliArguments result = parser.parse(args);

        assertEquals("net.jlstechnology", result.groupId());
        assertEquals("sync-cta", result.artifactId());
        assertEquals("aws", result.cloud());
    }

    @Test
    @DisplayName("Deve lançar exceção para --cloud com valor inválido")
    void shouldThrowExceptionForInvalidCloud() {
        String[] args = {
            "--groupId", "net.jlstechnology",
            "--artifactId", "sync-cta",
            "--cloud", "azure"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> parser.parse(args)
        );

        assertTrue(exception.getMessage().contains("aws"));
        assertTrue(exception.getMessage().contains("azure"));
    }

    @Test
    @DisplayName("Deve lançar exceção para --cloud sem valor")
    void shouldThrowExceptionForCloudWithoutValue() {
        String[] args = {
            "--groupId", "net.jlstechnology",
            "--artifactId", "sync-cta",
            "--cloud"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> parser.parse(args)
        );

        assertTrue(exception.getMessage().contains("--cloud"));
        assertTrue(exception.getMessage().contains("valor"));
    }
}

