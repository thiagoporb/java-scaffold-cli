package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;

/**
 * Template para geração dos arquivos docker-compose.yml (PostgreSQL e SonarQube).
 */
public final class DockerComposeTemplate {

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
        return String.format("""
                services:
                  postgres:
                    image: postgres:16-alpine
                    container_name: postgres
                    environment:
                      POSTGRES_DB: %s
                      POSTGRES_USER: postgres
                      POSTGRES_PASSWORD: postgres
                    ports:
                      - "0.0.0.0:5432:5432"
                    volumes:
                      - postgres_data:/var/lib/postgresql/data
                      - ${PWD}/src/main/docker/init-scripts:/docker-entrypoint-initdb.d
                    healthcheck:
                      test: ["CMD-SHELL", "pg_isready -U postgres"]
                      interval: 10s
                      timeout: 5s
                      retries: 5

                volumes:
                  postgres_data:
                """, artifactId);
    }

    /**
     * Gera o conteúdo do docker-compose.yml para SonarQube.
     *
     * @param config configuração do projeto.
     * @return conteúdo do arquivo docker-compose.yml.
     */
    public static String renderSonarQube(ScaffoldConfig config) {
        return """
                services:
                  sonarqube:
                    image: sonarqube:10.4-community
                    container_name: sonarqube
                    environment:
                      SONAR_JDBC_URL: jdbc:postgresql://postgres:5432/sonar
                      SONAR_JDBC_USERNAME: postgres
                      SONAR_JDBC_PASSWORD: postgres
                    volumes:
                      - sonarqube_data:/opt/sonarqube/data
                      - sonarqube_extensions:/opt/sonarqube/extensions
                      - sonarqube_logs:/opt/sonarqube/logs
                    ports:
                      - "9000:9000"
                    healthcheck:
                      test: ["CMD-SHELL", "curl -f http://localhost:9000/api/system/status || exit 1"]
                      interval: 30s
                      timeout: 10s
                      retries: 5
                    external_links:
                      - postgres:postgres

                volumes:
                  sonarqube_data:
                  sonarqube_extensions:
                  sonarqube_logs:
                """;
    }

    /**
     * Gera o script shell de inicialização para criar o banco de dados do SonarQube.
     * Usa script shell porque CREATE DATABASE não pode ser executado dentro de um bloco DO $$.
     *
     * @return conteúdo do script shell.
     */
    public static String renderInitSonarDbScript() {
        return """
                #!/bin/bash
                # Script de inicialização para criar o banco de dados do SonarQube
                # Este script é executado automaticamente quando o PostgreSQL inicia pela primeira vez
                
                set -e
                
                # Cria o banco de dados 'sonar' se não existir
                psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
                    SELECT 'CREATE DATABASE sonar'
                    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sonar')\\gexec
                EOSQL
                
                # O SonarQube criará as tabelas automaticamente ao iniciar
                """;
    }
}

