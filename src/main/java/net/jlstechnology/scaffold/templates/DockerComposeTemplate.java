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
        return String.format(MAIN_DOCKER_COMPOSE_TEMPLATE, artifactId, databaseName);
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
}

