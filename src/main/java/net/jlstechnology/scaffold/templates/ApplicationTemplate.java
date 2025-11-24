package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;

/**
 * Responsável por gerar os arquivos de configuração YAML.
 */
public final class ApplicationTemplate {

    private ApplicationTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Produz o conteúdo do arquivo application.yml comum a todos os ambientes.
     *
     * @return texto configurado.
     */
    public static String renderBase() {
        return """
                spring:
                  application:
                    name: app
                  jackson:
                    serialization:
                      WRITE_DATES_AS_TIMESTAMPS: false
                  datasource:
                    hikari: # Configurações do HikariCP (pool de conexões de alto desempenho)
                      maximum-pool-size: 10 # Número máximo de conexões no pool
                      minimum-idle: 5 # Número mínimo de conexões ociosas mantidas no pool
                      idle-timeout: 300000 # Tempo em milissegundos antes de fechar conexões ociosas (5 minutos)
                      max-lifetime: 1800000 # Tempo máximo de vida de uma conexão em milissegundos (30 minutos)
                      connection-timeout: 30000 # Timeout em milissegundos para obter uma conexão do pool (30 segundos)
                      leak-detection-threshold: 60000 # Threshold em milissegundos para detectar vazamentos de conexão (1 minuto)

                management:
                  endpoints:
                    web:
                      exposure:
                        include: health,info
                """;
    }

    /**
     * Produz o conteúdo específico de um profile.
     *
     * @param profile profile considerado.
     * @param databaseName nome do banco de dados (geralmente o artifactId em minúsculas).
     * @return configuração YAML dedicada ao profile.
     */
    public static String renderProfile(ExecutionProfile profile, String databaseName) {
        String profileName = profile.profileName();
        
        // Configurações para ambientes locais (dev, docker, docker-externo)
        if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER 
                || profile == ExecutionProfile.DOCKER_EXTERNO) {
            return """
                    spring:
                      config:
                        activate:
                          on-profile: %1$s
                      datasource:
                        url: jdbc:postgresql://localhost:5432/%2$s
                        driver-class-name: org.postgresql.Driver
                        username: postgres
                        password: postgres
                      jpa:
                        hibernate:
                          ddl-auto: update
                        properties:
                          hibernate:
                            dialect: org.hibernate.dialect.PostgreSQLDialect

                    logging:
                      level:
                        root: INFO
                        net.jlstechnology: DEBUG
                    """.formatted(profileName, databaseName);
        }
        
        // Configurações para ambientes remotos (homol, prod)
        return """
                spring:
                  config:
                    activate:
                      on-profile: %1$s
                  datasource:
                    url: ${DB_URL:jdbc:postgresql://db-server:5432/%2$s}
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
                  jpa:
                    hibernate:
                      ddl-auto: none
                    properties:
                      hibernate:
                        dialect: org.hibernate.dialect.PostgreSQLDialect

                logging:
                  level:
                    root: INFO
                    net.jlstechnology: INFO
                """.formatted(profileName, databaseName);
    }

    /**
     * Produz o conteúdo do arquivo application-test.yml para testes.
     *
     * @return configuração YAML dedicada aos testes.
     */
    public static String renderTest() {
        return """
                spring:
                  config:
                    activate:
                      on-profile: test
                  datasource:
                    url: jdbc:h2:mem:testdb
                    driver-class-name: org.h2.Driver
                    username: sa
                    password:
                  jpa:
                    hibernate:
                      ddl-auto: create-drop
                    properties:
                      hibernate:
                        dialect: org.hibernate.dialect.H2Dialect
                        format_sql: true
                  h2:
                    console:
                      enabled: true

                logging:
                  level:
                    root: WARN
                    net.jlstechnology: DEBUG
                    org.hibernate.SQL: DEBUG
                    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
                """;
    }
}

