package %1$s.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Configura o DataSource usando credenciais armazenadas no AWS Secrets Manager.
 *
 * Ativado quando {@code aws.secrets.database.enabled=true}.
 * O secret deve conter JSON: username, password, host, port, database.
 */
@Configuration
@ConditionalOnProperty(name = "aws.secrets.database.enabled", havingValue = "true")
@Slf4j
public class DatabaseConfig {

    @Value("${aws.secrets.database.secret-name}")
    private String secretName;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minIdle;

    @Value("${spring.datasource.hikari.idle-timeout:30000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.pool-name:%2$s-service-pool}")
    private String poolName;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretsManagerClient secretsManagerClient;

    public DatabaseConfig(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Configurando DataSource com credenciais do Secrets Manager. Secret: {}", secretName);

        DatabaseCredentials credentials = fetchDatabaseCredentials();

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(buildJdbcUrl(credentials));
        dataSource.setUsername(credentials.username());
        dataSource.setPassword(credentials.password());
        dataSource.setDriverClassName(driverClassName);

        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setMinimumIdle(minIdle);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setMaxLifetime(maxLifetime);
        dataSource.setPoolName(poolName);

        log.info("DataSource configurado - host: {}, pool: {}/{}", credentials.host(), minIdle, maxPoolSize);
        return dataSource;
    }

    private DatabaseCredentials fetchDatabaseCredentials() {
        try {
            var request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            var response = secretsManagerClient.getSecretValue(request);
            String secretString = response.secretString();

            JsonNode json = objectMapper.readTree(secretString);

            return new DatabaseCredentials(
                    json.get("username").asText(),
                    json.get("password").asText(),
                    json.get("host").asText(),
                    json.get("port").asText("5432"),
                    json.get("database").asText());

        } catch (Exception e) {
            log.error("Erro ao buscar credenciais do Secrets Manager: {}", e.getMessage());
            throw new RuntimeException(
                    "Falha ao obter credenciais do banco de dados do Secrets Manager", e);
        }
    }

    private String buildJdbcUrl(DatabaseCredentials credentials) {
        return String.format(
                "jdbc:postgresql://%%s:%%s/%%s",
                credentials.host(), credentials.port(), credentials.database());
    }

    private record DatabaseCredentials(
            String username, String password, String host, String port, String database) {}
}
