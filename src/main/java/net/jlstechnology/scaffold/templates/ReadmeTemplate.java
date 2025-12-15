package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Constrói o conteúdo do README do projeto gerado.
 */
public final class ReadmeTemplate {

    private static final String README_TEMPLATE = TemplateLoader.load("readme/README.md");

    private ReadmeTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Renderiza o texto do README com instruções iniciais.
     *
     * @param config configuração calculada.
     * @return conteúdo do README.
     */
    public static String render(ScaffoldConfig config) {
        String profileCommands = buildProfileCommands();
        String openApiCommands = buildOpenApiCommands();
        return String.format(
                README_TEMPLATE,
                config.artifactId(),                 // 1: título
                config.artifactId(),                 // 2: estrutura
                config.basePackagePath().toString(),  // 3: src/main/java
                config.artifactLowerCase(),           // 4: dev.yaml
                config.artifactLowerCase(),           // 5: homol.yaml
                config.artifactLowerCase(),           // 6: prod.yaml
                config.artifactLowerCase(),           // 7: docker.yaml
                config.artifactLowerCase(),           // 8: docker-externo.yaml
                config.basePackagePath().toString(),  // 9: src/test/java
                config.artifactLowerCase(),           // 10: banco de dados
                config.artifactLowerCase(),           // 11: postgres container (não usado, mas mantido para compatibilidade)
                config.artifactLowerCase(),           // 12: sonarqube container (não usado, mas mantido para compatibilidade)
                config.basePackagePath().toString(),  // 13: gatling path descrição
                config.basePackagePath().toString(),  // 14: gatling path plugin
                profileCommands,                      // 15: comandos de profile
                openApiCommands);                     // 16: comandos OpenAPI
    }

    private static String buildProfileCommands() {
        return Arrays.stream(ExecutionProfile.values())
                .map(profile -> "```\nmvn spring-boot:run -P" + profile.profileName() + "\n```")
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String buildOpenApiCommands() {
        return Arrays.stream(ExecutionProfile.values())
                .map(profile -> "```\nmvn openapi-generator:generate -P" + profile.profileName() + "\n```")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}

