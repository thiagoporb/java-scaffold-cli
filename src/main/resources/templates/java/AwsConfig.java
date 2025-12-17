package %1$s.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;

/**
 * Configuração dos clientes AWS SDK v2.
 *
 * Em ambiente local (LocalStack), usamos endpoint customizado.
 * Em produção (AWS real), os clientes usam as configurações padrão.
 */
@Configuration
public class AwsConfig {

    @Value("${aws.endpoint:}")
    private String awsEndpoint;

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.credentials.access-key:}")
    private String accessKey;

    @Value("${aws.credentials.secret-key:}")
    private String secretKey;

    /**
     * Verifica se está usando LocalStack (endpoint customizado).
     */
    private boolean isLocalStack() {
        return awsEndpoint != null && !awsEndpoint.isEmpty();
    }

    /**
     * Cria provider de credenciais.
     * Em LocalStack, usa credenciais fixas (test/test).
     * Em AWS real, usa as credenciais do ambiente (IAM role, env vars, etc).
     */
    private StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        );
    }

    /**
     * Cliente Secrets Manager para gerenciar segredos.
     */
    @Bean
    public SecretsManagerClient secretsManagerClient() {
        var builder = SecretsManagerClient.builder()
            .region(Region.of(awsRegion));

        if (isLocalStack()) {
            builder
                .endpointOverride(URI.create(awsEndpoint))
                .credentialsProvider(credentialsProvider());
        }

        return builder.build();
    }
}

