package net.jlstechnology.scaffold.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jlstechnology.scaffold.cli.CliArguments;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Garante que a configuração derivada seja calculada corretamente.
 */
class ScaffoldConfigTest {

    @Test
    @DisplayName("Deve derivar corretamente os valores a partir dos argumentos")
    void shouldDeriveValues() {
        CliArguments arguments = CliArguments.of("net.jlstechnology", "sync-cta", Path.of("/tmp"), false, null);
        ScaffoldConfig config = ScaffoldConfig.from(arguments);

        assertEquals("net.jlstechnology", config.groupId());
        assertEquals("sync-cta", config.artifactId());
        assertEquals("synccta", config.artifactPackageSegment());
        assertEquals("net.jlstechnology.synccta", config.basePackage());
        assertEquals("SyncCta", config.artifactPascalCase());
        assertEquals("sync-cta", config.artifactLowerCase());
        assertTrue(config.projectRoot().toString().endsWith("sync-cta"));
        assertEquals(Path.of("net/jlstechnology/synccta"), config.basePackagePath());
        assertEquals(null, config.cloud());
    }

    @Test
    @DisplayName("Deve derivar corretamente os valores com cloud aws")
    void shouldDeriveValuesWithCloudAws() {
        CliArguments arguments = CliArguments.of("net.jlstechnology", "sync-cta", Path.of("/tmp"), false, "aws");
        ScaffoldConfig config = ScaffoldConfig.from(arguments);

        assertEquals("net.jlstechnology", config.groupId());
        assertEquals("sync-cta", config.artifactId());
        assertEquals("aws", config.cloud());
    }
}

