package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Responsável por gerar os arquivos de configuração YAML.
 */
public final class ApplicationTemplate {

    private static final String BASE_TEMPLATE = TemplateLoader.load("application/application-base.yml");
    private static final String PROFILE_LOCAL_TEMPLATE = TemplateLoader.load("application/application-profile-local.yml");
    private static final String PROFILE_REMOTE_TEMPLATE = TemplateLoader.load("application/application-profile-remote.yml");
    private static final String TEST_TEMPLATE = TemplateLoader.load("application/application-test.yml");

    private ApplicationTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Produz o conteúdo do arquivo application.yml comum a todos os ambientes.
     *
     * @return texto configurado.
     */
    public static String renderBase() {
        return BASE_TEMPLATE;
    }

    /**
     * Produz o conteúdo específico de um profile.
     *
     * @param profile profile considerado.
     * @param databaseName nome do banco de dados (geralmente o artifactId em minúsculas).
     * @param config configuração do scaffold (para verificar se AWS está habilitado).
     * @return configuração YAML dedicada ao profile.
     */
    public static String renderProfile(ExecutionProfile profile, String databaseName, ScaffoldConfig config) {
        String profileName = profile.profileName();
        String datasourceSection = renderDatasourceSection(profile, databaseName, config);
        String awsSection = renderAwsSection(profile, config);
        
        // Configurações para ambientes locais (dev, docker, docker-externo)
        if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER 
                || profile == ExecutionProfile.DOCKER_EXTERNO) {
            return String.format(PROFILE_LOCAL_TEMPLATE, profileName, databaseName, datasourceSection, awsSection);
        }
        
        // Configurações para ambientes remotos (homol, prod)
        return String.format(PROFILE_REMOTE_TEMPLATE, profileName, databaseName, datasourceSection, awsSection);
    }

    /**
     * Renderiza a seção AWS para os profiles, incluindo secrets do banco.
     *
     * @param profile profile considerado.
     * @param config configuração do scaffold.
     * @return seção AWS ou string vazia se cloud não for aws.
     */
    private static String renderAwsSection(ExecutionProfile profile, ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        
        // Configurações para ambientes locais (dev, docker, docker-externo)
        if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER 
                || profile == ExecutionProfile.DOCKER_EXTERNO) {
            return renderAwsLocalSection(profile, config);
        }
        
        // Configurações para ambientes remotos (homol, prod)
        return renderAwsRemoteSection(profile, config);
    }

    /**
     * Renderiza a seção AWS para ambientes locais (LocalStack).
     *
     * @param profile profile considerado.
     * @param config configuração do scaffold.
     * @return seção AWS para LocalStack.
     */
    private static String renderAwsLocalSection(ExecutionProfile profile, ScaffoldConfig config) {
        String endpoint = (profile == ExecutionProfile.DOCKER || profile == ExecutionProfile.DOCKER_EXTERNO)
            ? "http://localstack:4566"
            : "http://localhost:4566";
        String secretName = profile.profileName() + "/" + config.artifactLowerCase() + "/db";
        return String.format("""

# ============================================================
# AWS - LocalStack
# ============================================================
aws:
  endpoint: %s
  region: us-east-1
  credentials:
    access-key: test
    secret-key: test
  secrets:
    database:
      enabled: true
      secret-name: %s
""", endpoint, secretName);
    }

    /**
     * Renderiza a seção AWS para ambientes remotos (AWS em produção).
     *
     * @return seção AWS para AWS real.
     */
    private static String renderAwsRemoteSection(ExecutionProfile profile, ScaffoldConfig config) {
        String secretName = profile.profileName() + "/" + config.artifactLowerCase() + "/db";
        return """
# ============================================================
# AWS - Ambiente AWS (produção)
# ============================================================
aws:
  endpoint:  # Vazio = usa AWS padrão
  region: ${AWS_REGION:us-east-1}
  credentials:
    access-key: ${AWS_ACCESS_KEY_ID:}
    secret-key: ${AWS_SECRET_ACCESS_KEY:}
  secrets:
    database:
      enabled: true
      secret-name: %s
""".formatted(secretName);
    }

    /**
     * Produz o conteúdo do arquivo application-test.yml para testes.
     *
     * @return configuração YAML dedicada aos testes.
     */
    public static String renderTest() {
        return TEST_TEMPLATE;
    }

    private static String renderDatasourceSection(ExecutionProfile profile, String databaseName, ScaffoldConfig config) {
        boolean isAws = "aws".equals(config.cloud());

        if (!isAws) {
            if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER || profile == ExecutionProfile.DOCKER_EXTERNO) {
                return """
  datasource:
    url: jdbc:postgresql://localhost:5432/%s
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: postgres
""".formatted(databaseName);
            }

            return """
  datasource:
    url: ${DB_URL:jdbc:postgresql://db-server:5432/%s}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME:db_user}
    password: ${DB_PASSWORD:db_password}
    hikari: # Configurações específicas para ambientes remotos
      maximum-pool-size: 20 # Pool maior para ambientes de produção
      minimum-idle: 10
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 30000
      leak-detection-threshold: 60000
""".formatted(databaseName);
        }

        // AWS habilitado: credenciais via Secrets Manager
        if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER || profile == ExecutionProfile.DOCKER_EXTERNO) {
            return """
  datasource:
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 30000
      connection-timeout: 20000
      max-lifetime: 1800000
      pool-name: %1$s-service-pool
""".formatted(config.artifactLowerCase());
        }

        return """
  datasource:
    driver-class-name: org.postgresql.Driver
    hikari: # Configurações específicas para ambientes remotos
      maximum-pool-size: ${HIKARI_MAX_POOL_SIZE:20} # Pool maior para ambientes de produção
      minimum-idle: ${HIKARI_MIN_IDLE:5}
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 30000
      leak-detection-threshold: 60000
      connection-test-query: SELECT 1
      validation-timeout: 5000
      pool-name: %1$s-service-pool
""".formatted(config.artifactLowerCase());
    }
}

