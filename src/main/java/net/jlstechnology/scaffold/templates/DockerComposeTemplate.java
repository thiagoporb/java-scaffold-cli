package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Template para geração dos arquivos docker-compose.yml e configurações Docker.
 */
public final class DockerComposeTemplate {

    private static final String POSTGRES_TEMPLATE = TemplateLoader.load("docker/postgres-compose.yml");
    private static final String SONARQUBE_TEMPLATE = TemplateLoader.load("docker/sonarqube-compose.yml");
    private static final String INIT_SONAR_DB_TEMPLATE = TemplateLoader.load("docker/init-sonar-db.sh");
    private static final String MAIN_DOCKER_COMPOSE_TEMPLATE = TemplateLoader.load("docker/docker-compose.yml");
    private static final String PROMETHEUS_CONFIG_TEMPLATE = TemplateLoader.load("docker/prometheus/prometheus.yml");
    private static final String GRAFANA_DATASOURCES_TEMPLATE = TemplateLoader.load("docker/grafana/datasources.yml");
    private static final String GRAFANA_DASHBOARDS_TEMPLATE = TemplateLoader.load("docker/grafana/dashboards.yml");
    private static final String POSTGRES_INIT_SQL_TEMPLATE = TemplateLoader.load("docker/postgres/init-databases.sql");
    private static final String DOCKERFILE_TEMPLATE = TemplateLoader.load("docker/Dockerfile");
    private static final String LOCALSTACK_INIT_SCRIPT_TEMPLATE = TemplateLoader.load("docker/localstack/init-aws.sh");

    private DockerComposeTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Gera o conteúdo do docker-compose.yml para PostgreSQL.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo docker-compose.yml.
     */
    public static String renderPostgres(ScaffoldConfig config) {
        String artifactId = config.artifactLowerCase();
        return String.format(POSTGRES_TEMPLATE, artifactId);
    }

    /**
     * Gera o conteúdo do docker-compose.yml para SonarQube.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo docker-compose.yml.
     */
    public static String renderSonarQube(ScaffoldConfig config) {
        return SONARQUBE_TEMPLATE;
    }

    /**
     * Gera o script shell de inicialização para criar o banco de dados do SonarQube.
     * Usa script shell porque CREATE DATABASE não pode ser executado dentro de um bloco DO $$.
     *
     * @return conteúdo do script shell.
     * @deprecated Use {@link #renderPostgresInitSql(ScaffoldConfig)} ao invés deste método.
     */
    @Deprecated
    public static String renderInitSonarDbScript() {
        return INIT_SONAR_DB_TEMPLATE;
    }

    /**
     * Gera o conteúdo do docker-compose.yml principal consolidado com todos os serviços.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo docker-compose.yml.
     */
    public static String renderMainDockerCompose(ScaffoldConfig config) {
        String artifactId = config.artifactLowerCase();
        String databaseName = config.artifactLowerCase();
        String localstackService = renderLocalStack(config);
        String localstackVolume = renderLocalStackVolume(config);
        return String.format(MAIN_DOCKER_COMPOSE_TEMPLATE, artifactId, databaseName, localstackService, localstackVolume);
    }

    /**
     * Gera a configuração do Prometheus para coletar métricas do serviço gerado.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo prometheus.yml.
     */
    public static String renderPrometheusConfig(ScaffoldConfig config) {
        String artifactId = config.artifactLowerCase();
        return String.format(PROMETHEUS_CONFIG_TEMPLATE, artifactId);
    }

    /**
     * Gera a configuração de datasources do Grafana.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo datasources.yml.
     */
    public static String renderGrafanaDatasources(ScaffoldConfig config) {
        return GRAFANA_DATASOURCES_TEMPLATE;
    }

    /**
     * Gera a configuração de dashboards do Grafana.
     *
     * @return conteúdo do arquivo dashboards.yml.
     */
    public static String renderGrafanaDashboards() {
        return GRAFANA_DASHBOARDS_TEMPLATE;
    }

    /**
     * Gera o script SQL de inicialização para criar o banco de dados do SonarQube.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo SQL.
     */
    public static String renderPostgresInitSql(ScaffoldConfig config) {
        String databaseName = config.artifactLowerCase();
        return String.format(POSTGRES_INIT_SQL_TEMPLATE, databaseName);
    }

    /**
     * Gera o Dockerfile multi-stage para o serviço gerado.
     *
     * @param config configuração do projeto.
     * @return conteúdo do Dockerfile.
     */
    public static String renderDockerfile(ScaffoldConfig config) {
        String artifactId = config.artifactLowerCase();
        return String.format(DOCKERFILE_TEMPLATE, artifactId + "-service");
    }

    /**
     * Gera o serviço LocalStack para o docker-compose.yml.
     *
     * @param config configuração do projeto.
     * @return conteúdo do serviço LocalStack ou string vazia se cloud não for aws.
     */
    public static String renderLocalStack(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
  # ============================================================
  # LOCALSTACK - Simulador de Serviços AWS
  # ============================================================
  # Simula serviços AWS localmente para desenvolvimento.
  #
  # SERVIÇOS DISPONÍVEIS (Community):
  #   - S3: Armazenamento de arquivos
  #   - SQS: Filas de mensagens
  #   - SNS: Pub/Sub (notificações)
  #   - DynamoDB: Banco NoSQL
  #   - Lambda: Funções serverless (básico)
  #   - Secrets Manager: Gerenciamento de segredos
  #
  # ACESSO:
  #   Dashboard: http://localhost:4566/_localstack/health
  #   Endpoint AWS: http://localhost:4566
  #
  # EXEMPLO DE USO (AWS CLI):
  #   aws --endpoint-url=http://localhost:4566 secretsmanager list-secrets
  # ============================================================
  localstack:
    image: localstack/localstack:latest
    container_name: localstack
    environment:
      # Serviços a serem iniciados (padrão: apenas Secrets Manager)
      SERVICES: secretsmanager
      # Região padrão
      DEFAULT_REGION: us-east-1
      # Modo debug (desabilitar em produção)
      DEBUG: 0
      # Persistência de dados entre restarts
      PERSISTENCE: 1
      # Nome do host Docker (para acesso interno)
      LOCALSTACK_HOST: localstack
    ports:
      # Porta principal - todos os serviços
      - "4566:4566"
      # Porta para serviços externos (opcional)
      - "4510-4559:4510-4559"
    volumes:
      # Persistência de dados
      - localstack_data:/var/lib/localstack
      # Scripts de inicialização
      - ./localstack:/etc/localstack/init/ready.d
      # Socket Docker (necessário para Lambda)
      - /var/run/docker.sock:/var/run/docker.sock
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:4566/_localstack/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - microservices-net

""";
    }

    /**
     * Gera o volume LocalStack para o docker-compose.yml.
     *
     * @param config configuração do projeto.
     * @return conteúdo do volume LocalStack ou string vazia se cloud não for aws.
     */
    public static String renderLocalStackVolume(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
  localstack_data:
    name: microservices_localstack_data
""";
    }

    /**
     * Gera o script de inicialização do LocalStack.
     *
     * @param config configuração do projeto.
     * @return conteúdo do script init-aws.sh.
     */
    public static String renderLocalStackInitScript(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        
        java.util.List<String> services = getDefaultAwsServices();
        
        String s3Section = services.contains("s3") ? generateS3Section() : "";
        String sqsSection = services.contains("sqs") ? generateSQSSection() : "";
        String snsSection = services.contains("sns") ? generateSNSSection() : "";
        String secretsSection = services.contains("secretsmanager") ? generateSecretsManagerSection(config) : "";
        String apigatewaySection = services.contains("apigateway") ? generateApiGatewaySection() : "";
        String dynamodbSection = services.contains("dynamodb") ? generateDynamoDBSection() : "";
        String lambdaSection = services.contains("lambda") ? generateLambdaSection() : "";
        
        return String.format(LOCALSTACK_INIT_SCRIPT_TEMPLATE,
            s3Section, sqsSection, snsSection, secretsSection,
            apigatewaySection, dynamodbSection, lambdaSection);
    }

    private static java.util.List<String> getDefaultAwsServices() {
        return java.util.List.of("secretsmanager");
    }

    private static String generateSecretsManagerSection(ScaffoldConfig config) {
        String artifact = config.artifactLowerCase();
        return """
        # ============================================================
        # SECRETS MANAGER
        # ============================================================
        echo ""
        echo ">>> Criando secrets..."
        
        # Secrets de credenciais de banco (gerados por profile)
        awslocal secretsmanager create-secret \\
            --name dev/%1$s/db \\
            --secret-string '{"username":"postgres","password":"postgres","host":"localhost","port":"5432","database":"%1$s"}'
        echo "    [OK] dev/%1$s/db"

        awslocal secretsmanager create-secret \\
            --name docker/%1$s/db \\
            --secret-string '{"username":"postgres","password":"postgres","host":"postgres","port":"5432","database":"%1$s"}'
        echo "    [OK] docker/%1$s/db"

        awslocal secretsmanager create-secret \\
            --name docker-externo/%1$s/db \\
            --secret-string '{"username":"postgres","password":"postgres","host":"postgres","port":"5432","database":"%1$s"}'
        echo "    [OK] docker-externo/%1$s/db"

        awslocal secretsmanager create-secret \\
            --name homol/%1$s/db \\
            --secret-string '{"username":"postgres","password":"postgres","host":"postgres","port":"5432","database":"%1$s"}'
        echo "    [OK] homol/%1$s/db"

        awslocal secretsmanager create-secret \\
            --name prod/%1$s/db \\
            --secret-string '{"username":"postgres","password":"postgres","host":"postgres","port":"5432","database":"%1$s"}'
        echo "    [OK] prod/%1$s/db"

        """.formatted(artifact);
    }

    private static String generateS3Section() {
        return """
        # ============================================================
        # S3 - BUCKETS
        # ============================================================
        echo ""
        echo ">>> Criando buckets S3..."
        
        awslocal s3 mb s3://uploads-dev
        echo "    [OK] s3://uploads-dev"
        
        awslocal s3 mb s3://documents-dev
        echo "    [OK] s3://documents-dev"
        
        """;
    }

    private static String generateSQSSection() {
        return """
        # ============================================================
        # SQS - FILAS DE MENSAGENS
        # ============================================================
        echo ""
        echo ">>> Criando filas SQS..."
        
        awslocal sqs create-queue --queue-name orders-queue
        echo "    [OK] orders-queue"
        
        awslocal sqs create-queue --queue-name notifications-queue
        echo "    [OK] notifications-queue"
        
        """;
    }

    private static String generateSNSSection() {
        return """
        # ============================================================
        # SNS - TÓPICOS (PUB/SUB)
        # ============================================================
        echo ""
        echo ">>> Criando tópicos SNS..."
        
        awslocal sns create-topic --name user-events
        echo "    [OK] user-events"
        
        awslocal sns create-topic --name order-events
        echo "    [OK] order-events"
        
        """;
    }

    private static String generateApiGatewaySection() {
        return """
        # ============================================================
        # API GATEWAY
        # ============================================================
        echo ""
        echo ">>> Criando API Gateway..."
        echo "    [INFO] API Gateway será configurado manualmente conforme necessário"
        
        """;
    }

    private static String generateDynamoDBSection() {
        return """
        # ============================================================
        # DYNAMODB - TABELAS
        # ============================================================
        echo ""
        echo ">>> Criando tabelas DynamoDB..."
        echo "    [INFO] Tabelas DynamoDB serão criadas conforme necessário"
        
        """;
    }

    private static String generateLambdaSection() {
        return """
        # ============================================================
        # LAMBDA - FUNÇÕES
        # ============================================================
        echo ""
        echo ">>> Criando funções Lambda..."
        echo "    [INFO] Funções Lambda serão criadas conforme necessário"
        
        """;
    }
}

