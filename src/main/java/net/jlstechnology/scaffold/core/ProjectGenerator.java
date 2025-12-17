package net.jlstechnology.scaffold.core;

import net.jlstechnology.scaffold.templates.ApplicationTemplate;
import net.jlstechnology.scaffold.templates.DockerComposeTemplate;
import net.jlstechnology.scaffold.templates.JavaSourceTemplate;
import net.jlstechnology.scaffold.templates.OpenApiTemplate;
import net.jlstechnology.scaffold.templates.PomTemplate;
import net.jlstechnology.scaffold.templates.ReadmeTemplate;
import net.jlstechnology.scaffold.templates.TestTemplate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Responsável por orquestrar a criação dos diretórios e arquivos do projeto alvo.
 */
public final class ProjectGenerator {

    /**
     * Executa a geração do projeto com base nas informações calculadas.
     *
     * @param config configuração completa.
     * @param force indica se diretórios existentes devem ser reutilizados.
     * @throws IOException caso ocorra falha de escrita.
     */
    public void generate(ScaffoldConfig config, boolean force) throws IOException {
        prepareTargetDirectory(config.projectRoot(), force);
        createCoreFiles(config);
        createResourceFiles(config);
        createDockerFiles(config);
        createSourceFiles(config);
        createTestFiles(config);
        createGatlingFiles(config);
    }

    private void prepareTargetDirectory(Path projectRoot, boolean force) throws IOException {
        if (Files.exists(projectRoot)) {
            if (!Files.isDirectory(projectRoot)) {
                throw new IllegalStateException("O caminho de destino existe mas não é um diretório válido.");
            }
            if (!force) {
                throw new IllegalStateException(
                        "O diretório de destino já existe. Execute novamente com --force para sobrescrever.");
            }
            // Com --force, deleta o diretório existente para garantir limpeza completa
            deleteDirectoryRecursively(projectRoot);
            System.out.printf("🗑️  Diretório existente removido: %s%n", projectRoot);
        }
        Files.createDirectories(projectRoot);
    }

    private void deleteDirectoryRecursively(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Ordena reverso para deletar arquivos antes de diretórios
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao deletar: " + path, e);
                        }
                    });
        }
    }

    private void createCoreFiles(ScaffoldConfig config) throws IOException {
        writeFile(config.projectRoot().resolve("pom.xml"), PomTemplate.render(config));
        writeFile(config.projectRoot().resolve("README.md"), ReadmeTemplate.render(config));
    }

    private void createResourceFiles(ScaffoldConfig config) throws IOException {
        Path resourcesDir = config.projectRoot().resolve("src/main/resources");
        writeFile(resourcesDir.resolve("application.yml"), ApplicationTemplate.renderBase());

        for (ExecutionProfile profile : ExecutionProfile.values()) {
            String profileName = profile.profileName();
            writeFile(
                    resourcesDir.resolve("application-" + profileName + ".yml"),
                    ApplicationTemplate.renderProfile(profile, config.artifactLowerCase(), config));
        }

        Path openApiDir = resourcesDir.resolve("openapi");
        for (ExecutionProfile profile : ExecutionProfile.values()) {
            writeFile(
                    openApiDir.resolve(config.artifactLowerCase() + "-" + profile.profileName() + ".yaml"),
                    OpenApiTemplate.render(profile));
        }

        // Arquivo de configuração para testes
        Path testResourcesDir = config.projectRoot().resolve("src/test/resources");
        writeFile(testResourcesDir.resolve("application-test.yml"), ApplicationTemplate.renderTest());
    }

    private void createDockerFiles(ScaffoldConfig config) throws IOException {
        // Criar estrutura docker/ em src/main/docker
        Path dockerDir = config.projectRoot().resolve("src/main/docker");
        
        // Docker Compose principal consolidado
        writeFile(dockerDir.resolve("docker-compose.yml"), DockerComposeTemplate.renderMainDockerCompose(config));
        
        // Configuração do Prometheus
        Path prometheusDir = dockerDir.resolve("prometheus");
        writeFile(prometheusDir.resolve("prometheus.yml"), DockerComposeTemplate.renderPrometheusConfig(config));
        
        // Configurações do Grafana
        Path grafanaDatasourcesDir = dockerDir.resolve("grafana/provisioning/datasources");
        writeFile(grafanaDatasourcesDir.resolve("datasources.yml"), DockerComposeTemplate.renderGrafanaDatasources(config));
        
        Path grafanaDashboardsDir = dockerDir.resolve("grafana/provisioning/dashboards");
        writeFile(grafanaDashboardsDir.resolve("dashboards.yml"), DockerComposeTemplate.renderGrafanaDashboards());
        
        // Script SQL de inicialização do PostgreSQL
        Path postgresDir = dockerDir.resolve("postgres");
        writeFile(postgresDir.resolve("01-create-databases.sql"), DockerComposeTemplate.renderPostgresInitSql(config));
        
        // LocalStack (apenas se cloud=aws)
        if ("aws".equals(config.cloud())) {
            Path localstackDir = dockerDir.resolve("localstack");
            String initScript = DockerComposeTemplate.renderLocalStackInitScript(config);
            if (!initScript.isEmpty()) {
                Path initScriptPath = localstackDir.resolve("init-aws.sh");
                writeFile(initScriptPath, initScript);
                // Tornar o script executável
                try {
                    java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions = 
                        java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x");
                    Files.setPosixFilePermissions(initScriptPath, permissions);
                } catch (UnsupportedOperationException e) {
                    // Windows não suporta PosixFilePermissions, ignorar
                }
            }
        }
        
        // Dockerfile na raiz do projeto
        writeFile(config.projectRoot().resolve("Dockerfile"), DockerComposeTemplate.renderDockerfile(config));
    }

    private void createSourceFiles(ScaffoldConfig config) throws IOException {
        Path baseJavaDir = config.projectRoot()
                .resolve("src/main/java")
                .resolve(config.basePackagePath());

        writeFile(
                baseJavaDir.resolve(config.artifactPascalCase() + "Application.java"),
                JavaSourceTemplate.applicationClass(config));
        writeFile(baseJavaDir.resolve("model/ExemploModel.java"), JavaSourceTemplate.exemploModel(config));
        writeFile(baseJavaDir.resolve("repository/ExemploRepository.java"), JavaSourceTemplate.exemploRepository(config));
        Path mapperDir = baseJavaDir.resolve("service/mapper");
        writeFile(mapperDir.resolve("ExemploMapper.java"), JavaSourceTemplate.exemploMapperImpl(config));
        writeFile(baseJavaDir.resolve("service/ExemploService.java"), JavaSourceTemplate.exemploService(config));
        writeFile(
                baseJavaDir.resolve("service/dto/ExemploDTO.java"), JavaSourceTemplate.exemploDto(config));
        Path errorsDir = baseJavaDir.resolve("web/rest/errors");
        writeFile(errorsDir.resolve("ProblemType.java"), JavaSourceTemplate.problemType(config));
        writeFile(errorsDir.resolve("ProblemDetails.java"), JavaSourceTemplate.problemDetails(config));
        writeFile(errorsDir.resolve("ProblemDetailsException.java"), JavaSourceTemplate.problemDetailsException(config));
        writeFile(errorsDir.resolve("ResourceNotFoundProblem.java"), JavaSourceTemplate.resourceNotFoundProblem(config));
        writeFile(errorsDir.resolve("ValidationProblem.java"), JavaSourceTemplate.validationProblem(config));
        writeFile(errorsDir.resolve("BusinessRuleProblem.java"), JavaSourceTemplate.businessRuleProblem(config));
        writeFile(errorsDir.resolve("ProblemDetailsControllerAdvice.java"), JavaSourceTemplate.problemDetailsControllerAdvice(config));
        writeFile(
                baseJavaDir.resolve("web/rest/ExemploResource.java"), JavaSourceTemplate.exemploResource(config));
        
        // AwsConfig (apenas se cloud=aws)
        if ("aws".equals(config.cloud())) {
            Path configDir = baseJavaDir.resolve("config");
            writeFile(configDir.resolve("AwsConfig.java"), JavaSourceTemplate.awsConfig(config));
            writeFile(configDir.resolve("DatabaseConfig.java"), JavaSourceTemplate.databaseConfig(config));
        }
    }

    private void createTestFiles(ScaffoldConfig config) throws IOException {
        Path baseTestDir = config.projectRoot()
                .resolve("src/test/java")
                .resolve(config.basePackagePath());

        writeFile(
                baseTestDir.resolve(config.artifactPascalCase() + "ApplicationTests.java"),
                TestTemplate.applicationTest(config));
        writeFile(baseTestDir.resolve("arquitetura/ArquiteturaTest.java"), TestTemplate.arquiteturaTest(config));
        writeFile(
                baseTestDir.resolve("service/mapper/ExemploMapperTest.java"),
                TestTemplate.mapperTest(config));
        writeFile(
                baseTestDir.resolve("service/ExemploServiceTest.java"),
                TestTemplate.serviceTest(config));
        writeFile(
                baseTestDir.resolve("web/rest/ExemploResourceTest.java"),
                TestTemplate.resourceTest(config));
        writeFile(
                baseTestDir.resolve("web/rest/errors/ErrorsTest.java"),
                TestTemplate.errorsTest(config));
        writeFile(
                baseTestDir.resolve("web/rest/errors/ProblemDetailsControllerAdviceTest.java"),
                TestTemplate.controllerAdviceTest(config));
    }

    private void createGatlingFiles(ScaffoldConfig config) throws IOException {
        Path gatlingDir = config.projectRoot()
                .resolve("src/test/java")
                .resolve(config.basePackagePath())
                .resolve("gatling")
                .resolve("simulation");
        writeFile(
                gatlingDir.resolve("BasicSimulation.java"),
                JavaSourceTemplate.gatlingSimulation(config));
    }

    private void writeFile(Path target, String content) throws IOException {
        Files.createDirectories(target.getParent());
        Files.writeString(target, content, StandardCharsets.UTF_8);
        System.out.printf("✔ Arquivo criado: %s%n", target);
    }
}

