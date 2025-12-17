package net.jlstechnology.scaffold.integration;

import net.jlstechnology.scaffold.cli.CliArguments;
import net.jlstechnology.scaffold.core.ProjectGenerator;
import net.jlstechnology.scaffold.core.ScaffoldConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Teste de integração que valida se o scaffold gera projetos que compilam corretamente.
 * Este teste é executado durante o build do scaffold-cli para garantir qualidade.
 */
class ScaffoldValidationTest {

    @Test
    @DisplayName("Deve gerar projeto válido sem cloud")
    void shouldGenerateValidProjectWithoutCloud(@TempDir Path tempDir) throws IOException, InterruptedException {
        CliArguments arguments = CliArguments.of(
            "net.jlstechnology",
            "test-project",
            tempDir,
            true,
            null
        );
        
        ScaffoldConfig config = ScaffoldConfig.from(arguments);
        ProjectGenerator generator = new ProjectGenerator();
        generator.generate(config, true);
        
        validateMavenBuild(config.projectRoot());
    }

    @Test
    @DisplayName("Deve gerar projeto válido com cloud aws")
    void shouldGenerateValidProjectWithCloudAws(@TempDir Path tempDir) throws IOException, InterruptedException {
        CliArguments arguments = CliArguments.of(
            "net.jlstechnology",
            "test-project-aws",
            tempDir,
            true,
            "aws"
        );
        
        ScaffoldConfig config = ScaffoldConfig.from(arguments);
        ProjectGenerator generator = new ProjectGenerator();
        generator.generate(config, true);
        
        validateMavenBuild(config.projectRoot());
    }

    private void validateMavenBuild(Path projectRoot) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "mvn", "clean", "package", "-Pdev", "-q"
        );
        processBuilder.directory(projectRoot.toFile());
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        boolean finished = process.waitFor(10, TimeUnit.MINUTES);
        
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Build Maven excedeu o tempo limite de 10 minutos.");
        }
        
        if (process.exitValue() != 0) {
            // Se falhou, executar novamente sem -q para ver os erros
            ProcessBuilder verboseBuilder = new ProcessBuilder(
                "mvn", "clean", "package", "-Pdev"
            );
            verboseBuilder.directory(projectRoot.toFile());
            verboseBuilder.redirectErrorStream(true);
            Process verboseProcess = verboseBuilder.start();
            
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(verboseProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
            verboseProcess.waitFor();
            throw new IllegalStateException(
                "Build Maven falhou no projeto gerado. Verifique os logs acima."
            );
        }
    }
}

