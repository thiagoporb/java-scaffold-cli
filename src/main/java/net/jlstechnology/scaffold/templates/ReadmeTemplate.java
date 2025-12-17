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
        String localstackServiceList = buildLocalStackServiceList(config);
        String localstackSection = buildLocalStackSection(config);
        String testcontainersAndAwsConfigSection = buildTestcontainersAndAwsConfigSection(config);
        String localstackDirStructure = buildLocalStackDirStructure(config);
        String configPackageStructure = buildConfigPackageStructure(config);
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
                openApiCommands,                      // 16: comandos OpenAPI
                localstackServiceList,                // 17: menção ao LocalStack na lista de serviços
                localstackSection,                    // 18: seção completa do LocalStack
                testcontainersAndAwsConfigSection,    // 19: seção de Testcontainers e AwsConfig
                localstackDirStructure,                // 20: diretório localstack na estrutura
                configPackageStructure);              // 21: pacote config na estrutura
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

    private static String buildLocalStackServiceList(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return System.lineSeparator() + "- **LocalStack** (porta 4566) - Simulador de serviços AWS";
    }

    private static String buildLocalStackSection(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
## LocalStack - Simulador de Serviços AWS

O LocalStack é iniciado automaticamente pelo `docker-compose.yml` quando o projeto foi gerado com suporte a AWS (`--cloud aws`).

**Configurações:**
- **Porta principal**: 4566
- **Dashboard**: http://localhost:4566/_localstack/health
- **Endpoint AWS**: http://localhost:4566
- **Região padrão**: us-east-1

**Serviços disponíveis:**
- **Secrets Manager**: Gerenciamento de segredos (habilitado por padrão)

**Inicialização automática:**
O LocalStack executa automaticamente o script `src/main/docker/localstack/init-aws.sh` na inicialização, criando os recursos AWS necessários para desenvolvimento.

**Exemplo de uso com AWS CLI:**
```bash
# Listar secrets
aws --endpoint-url=http://localhost:4566 secretsmanager list-secrets

# Criar um secret
aws --endpoint-url=http://localhost:4566 secretsmanager create-secret \\
    --name meu-secret \\
    --secret-string '{"usuario":"admin","senha":"123456"}'

# Recuperar um secret
aws --endpoint-url=http://localhost:4566 secretsmanager get-secret-value \\
    --secret-id meu-secret
```

**Nota**: O LocalStack persiste dados entre restarts do container através do volume `localstack_data`. Para limpar todos os dados, execute:
```bash
docker compose -f src/main/docker/docker-compose.yml down -v
```

""";
    }

    private static String buildTestcontainersAndAwsConfigSection(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return """
## Testcontainers

O projeto inclui suporte a **Testcontainers** para testes de integração com PostgreSQL e LocalStack.

**Dependências incluídas:**
- `spring-boot-testcontainers`: Integração do Testcontainers com Spring Boot
- `testcontainers-postgresql`: Container PostgreSQL para testes
- `testcontainers-localstack`: Container LocalStack para testes de serviços AWS

**Versões:**
- **Testcontainers**: 1.20.4
- **AWS SDK**: 2.29.0

### Executar testes de integração

Os testes de integração utilizam containers Docker para simular o ambiente de produção:

```bash
mvn clean test
```

**Importante**: Certifique-se de que o Docker está rodando antes de executar os testes, pois o Testcontainers precisa criar containers temporários.

## Configuração AWS

O projeto inclui uma classe de configuração `AwsConfig` no pacote `config` que fornece beans prontos para uso dos serviços AWS.

### AwsConfig

A classe `AwsConfig` está localizada em:
```
src/main/java/%1$s/config/AwsConfig.java
```

Esta classe é uma configuração Spring (`@Configuration`) que cria e configura os clientes AWS SDK v2 necessários para interagir com os serviços AWS.

**Responsabilidades:**
- Configuração de credenciais AWS (LocalStack ou AWS real)
- Criação de beans para clientes AWS (Secrets Manager, S3, SQS, etc.)
- Gerenciamento de endpoints (LocalStack em desenvolvimento, AWS em produção)
- Configuração de região AWS

**Beans disponíveis:**
- `SecretsManagerClient`: Cliente para interagir com o AWS Secrets Manager

**Propriedades de configuração:**
As propriedades AWS são configuradas nos arquivos `application-*.yml`:
- `aws.endpoint`: Endpoint do LocalStack (desenvolvimento) ou AWS (produção)
  - Desenvolvimento: `http://localhost:4566` (LocalStack)
  - Produção: omitir para usar endpoint padrão da AWS
- `aws.region`: Região AWS (padrão: `us-east-1`)
- `aws.credentials.access-key`: Chave de acesso (opcional, necessário apenas para LocalStack)
- `aws.credentials.secret-key`: Chave secreta (opcional, necessário apenas para LocalStack)

**Exemplo de uso:**
```java
@Autowired
private SecretsManagerClient secretsManagerClient;

public void exemplo() {
    GetSecretValueRequest request = GetSecretValueRequest.builder()
        .secretId("meu-secret")
        .build();
    
    GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
    String secret = response.secretString();
}
```

**Comportamento:**
- **Desenvolvimento (LocalStack)**: Quando `aws.endpoint` está configurado, a classe detecta automaticamente que está usando LocalStack e configura credenciais fixas (`test/test`) e o endpoint customizado.
- **Produção (AWS real)**: Quando `aws.endpoint` não está configurado, a classe usa as credenciais padrão do ambiente (IAM role, variáveis de ambiente, etc.) e o endpoint padrão da AWS.

## DatabaseConfig (Secrets Manager)

A classe `DatabaseConfig` é gerada no pacote `config` quando o projeto é criado com `--cloud aws`. Ela lê as credenciais do banco a partir do AWS Secrets Manager e cria um `DataSource` com HikariCP.

**Arquivo gerado:**
```
src/main/java/%1$s/config/DatabaseConfig.java
```

**Propriedades:**
```
aws.secrets.database.enabled=true
aws.secrets.database.secret-name={profile}/{artifactId}/db
```

**Formato do secret (JSON):**
```json
{
  "username": "postgres",
  "password": "postgres",
  "host": "postgres",
  "port": "5432",
  "database": "meuprojeto"
}
```

**Secrets por profile (criados no init-aws.sh):**
- `dev/{artifactId}/db` (host: localhost)
- `docker/{artifactId}/db` (host: postgres)
- `docker-externo/{artifactId}/db` (host: postgres)
- `homol/{artifactId}/db` (host: postgres — ajustar conforme ambiente)
- `prod/{artifactId}/db` (host: postgres — ajustar conforme ambiente)

**Observação:** Quando `--cloud aws` está habilitado, os `application-*.yml` não definem `url/username/password`; o `DatabaseConfig` monta a URL JDBC a partir do secret.

**Nota**: Em desenvolvimento, o `aws.endpoint` aponta para o LocalStack (`http://localhost:4566`). Em produção, configure o endpoint da AWS real ou remova a propriedade para usar o endpoint padrão da AWS.

""".formatted(config.basePackagePath().toString());
    }

    private static String buildLocalStackDirStructure(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return System.lineSeparator() + "  │   │   ├── localstack/" + System.lineSeparator() + "  │   │   │   └── init-aws.sh";
    }

    private static String buildConfigPackageStructure(ScaffoldConfig config) {
        if (!"aws".equals(config.cloud())) {
            return "";
        }
        return System.lineSeparator() + "  │   │   └── config/";
    }
}

