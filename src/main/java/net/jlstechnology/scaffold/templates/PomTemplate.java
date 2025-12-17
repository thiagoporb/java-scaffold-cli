package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Responsável por montar o conteúdo do arquivo pom.xml do projeto gerado.
 */
public final class PomTemplate {

    private static final String POM_TEMPLATE = TemplateLoader.load("pom/pom.xml");

    private PomTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Constrói o conteúdo completo do pom.xml.
     *
     * @param config configuração do scaffold.
     * @return texto final para gravação.
     */
    public static String render(ScaffoldConfig config) {
        String profilesSection = buildProfilesSection(config);
        String testcontainersProperties = renderTestcontainersProperties();
        String awsProperties = renderAwsProperties(config);
        String testcontainersDependencyManagement = renderTestcontainersDependencyManagement();
        String awsDependencyManagement = renderAwsDependencyManagement(config);
        String awsDependencies = renderAwsDependencies(config);
        String testcontainersDependencies = renderTestcontainersDependencies();
        String testcontainersLocalStackDependency = renderTestcontainersLocalStackDependency(config);
        
        return String.format(
                java.util.Locale.ROOT,
                POM_TEMPLATE,
                config.groupId(),                    // 1: groupId
                config.artifactId(),                 // 2: artifactId
                config.artifactId(),                 // 3: name
                config.artifactLowerCase(),           // 4: openapi.generator.input
                config.basePackage(),                 // 5: openapi.generator.package
                config.basePackage(),                 // 6: mainClass (package)
                config.artifactPascalCase(),          // 7: mainClass (class name)
                config.basePackage(),                 // 8: modelPackage
                config.basePackage(),                 // 9: configPackage
                config.basePackage(),                 // 10: gatling include
                profilesSection,                      // 11: profiles
                testcontainersProperties,            // 12: testcontainers properties
                awsProperties,                       // 13: aws properties
                testcontainersDependencyManagement,  // 14: testcontainers dependencyManagement
                awsDependencyManagement,             // 15: aws dependencyManagement
                awsDependencies,                      // 16: aws dependencies
                testcontainersDependencies,          // 17: testcontainers dependencies
                testcontainersLocalStackDependency   // 18: testcontainers localstack dependency
        );
    }

    private static String buildProfilesSection(ScaffoldConfig config) {
        return Arrays.stream(ExecutionProfile.values())
                .map(profile -> buildProfileEntry(profile, config.artifactLowerCase()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String buildProfileEntry(ExecutionProfile profile, String artifactLower) {
        String activationBlock = profile == ExecutionProfile.DEV
                ? """
                            <activation>
                                <activeByDefault>true</activeByDefault>
                            </activation>
                """
                : "";
        String profileComment = switch (profile) {
            case DEV -> "<!-- Profile de desenvolvimento (ativo por padrão) -->";
            case HOMOL -> "<!-- Profile de homologação -->";
            case PROD -> "<!-- Profile de produção -->";
            case DOCKER -> "<!-- Profile para ambiente Docker -->";
            case DOCKER_EXTERNO -> "<!-- Profile para ambiente Docker externo -->";
        };
        return ("""
                        %4$s
                        <profile>
                            <id>%1$s</id>
                %3$s            <properties>
                                <spring.profiles.active>%1$s</spring.profiles.active>
                                <openapi.generator.input>src/main/resources/openapi/%2$s-%1$s.yaml</openapi.generator.input>
                            </properties>
                        </profile>""").formatted(profile.profileName(), artifactLower, activationBlock, profileComment);
    }

    private static String renderTestcontainersProperties() {
        return """
        <testcontainers.version>1.20.4</testcontainers.version>
        """;
    }

    private static String renderAwsProperties(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
        <awssdk.version>2.29.0</awssdk.version>
        """;
    }

    private static String renderTestcontainersDependencyManagement() {
        return """
            <!-- Testcontainers BOM (gerenciamento de versões) -->
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        """;
    }

    private static String renderAwsDependencyManagement(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
            <!-- AWS SDK BOM (gerenciamento de versões) -->
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>${awssdk.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        """;
    }

    private static String renderAwsDependencies(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
        <!-- AWS SDK Secrets Manager -->
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>secretsmanager</artifactId>
        </dependency>
        """;
    }

    private static String renderTestcontainersDependencies() {
        return """
        <!-- Spring Boot Testcontainers (testes de integração) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- Testcontainers PostgreSQL (testes de integração) -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        """;
    }

    private static String renderTestcontainersLocalStackDependency(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
        <!-- Testcontainers LocalStack (testes de integração AWS) -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>localstack</artifactId>
            <scope>test</scope>
        </dependency>
        """;
    }

}

