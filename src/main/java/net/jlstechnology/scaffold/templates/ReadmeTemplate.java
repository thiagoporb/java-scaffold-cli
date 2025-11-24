package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.ScaffoldConfig;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Constrói o conteúdo do README do projeto gerado.
 */
public final class ReadmeTemplate {

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
        return """
                # %s

                Projeto gerado automaticamente via scaffolding corporativo para serviços Java com Spring Boot.

                ## Arquitetura e convenções

                O scaffold produz um projeto em camadas claro e modular:
                - `model`: entidades JPA.
                - `repository`: portas de persistência (CRUD).
                - `service`: regras de negócio. Cada classe termina com `Service` e usa o mapper estático para conversão.
                - `web.rest`: implementação dos contratos OpenAPI. Todas as classes terminam com `Resource`.
                - `web.rest.errors`: centraliza `ProblemDetails`, enums de `ProblemType`, exceções específicas e o `ControllerAdvice` que expõe JSON Problem Details conforme a RFC 9457.
                - `service.mapper`: converte entre modelos e DTOs internos.
                - `web.api`: contém os contratos gerados pelo OpenAPI Generator (DTOs `Request*DTO` e `Response*DTO`).

                O pacote `errors` já traz:
                - `ProblemDetails`, que inclui o timestamp, `type`, `title`, `status`, `detail`, `instance` e uma lista de `violations`.
                - `ProblemType` (tipos compatíveis com RFC 9457).
                - Exceções concretas (`ResourceNotFoundProblem`, `ValidationProblem`, `BusinessRuleProblem`).
                - `ProblemDetailsControllerAdvice`, que interpreta essas exceções e qualquer `Exception` inesperada.

                Os testes gerados cobrem:
                - `ArquiteturaTest`: garante nomes e pacotes.
                - `ExemploMapperTest`: valida os mapeamentos entre `ExemploModel` e `ExemploDTO`.
                - `ExemploResourceTest`: valida o fluxo básico do controller.
                Mantenha essas classes atualizadas conforme novos pacotes e recursos forem adicionados.

                ## Estrutura

                ```
                %s/
                  ├── pom.xml
                  ├── README.md
                  ├── src/main/java/%s/
                  │   ├── model/
                  │   ├── repository/
                  │   ├── service/
                  │   │   └── mapper/
                  │   └── web/rest/
                  ├── src/main/resources/
                  │   ├── application.yml
                  │   ├── application-dev.yml
                  │   ├── application-homol.yml
                  │   ├── application-prod.yml
                  │   ├── application-docker.yml
                  │   ├── application-docker-externo.yml
                  │   └── openapi/
                  │       ├── %s-dev.yaml
                  │       ├── %s-homol.yaml
                  │       ├── %s-prod.yaml
                  │       ├── %s-docker.yaml
                  │       └── %s-docker-externo.yaml
                  └── src/test/java/%s/
                      ├── arquitetura/
                      ├── model/
                      ├── repository/
                      ├── service/
                      │   └── mapper/
                      └── web/rest/
                ```

                ## Pré-requisitos

                - Java 21 (ou versão LTS superior)
                - Maven 3.9+
                - Docker e Docker Compose (para executar o banco de dados PostgreSQL)

                ## Banco de Dados PostgreSQL

                O projeto inclui um arquivo `docker-compose.yml` para executar o PostgreSQL localmente.

                ### Iniciar o PostgreSQL

                A partir da raiz do projeto, execute:

                ```bash
                docker-compose -f src/main/docker/postgres-compose.yml up -d
                ```

                O container será iniciado com as seguintes configurações:
                - **Porta**: 5432 (exposta em todas as interfaces de rede: `0.0.0.0:5432`)
                - **Banco de dados**: %s
                - **Usuário**: postgres
                - **Senha**: postgres

                **Nota para conexão externa (DBeaver, etc.)**:
                - Se o Docker estiver rodando no WSL2, use `localhost` ou `127.0.0.1` para conectar do Windows.
                - A porta 5432 está exposta em todas as interfaces (`0.0.0.0:5432`), garantindo acesso externo.
                - Se ainda houver problemas de conexão, verifique se o firewall do Windows não está bloqueando a porta 5432.

                ### Parar o PostgreSQL

                ```bash
                docker-compose -f src/main/docker/postgres-compose.yml down
                ```

                ### Verificar status do container

                ```bash
                docker ps | grep postgres
                ```

                **Importante**: O container PostgreSQL usa o nome fixo `postgres` para permitir que o SonarQube se conecte ao mesmo banco.

                **Nota sobre o banco SonarQube**: O script SQL em `src/main/docker/init-scripts/01-init-sonar-db.sql` cria automaticamente o banco `sonar` na primeira inicialização do PostgreSQL. Se o volume do PostgreSQL já existir, o script não será executado. Neste caso, você precisará criar o banco manualmente ou recriar o volume:

                ```bash
                # Criar o banco manualmente
                docker exec -it postgres psql -U postgres -c "CREATE DATABASE sonar;"
                
                # OU recriar o volume (apaga todos os dados!)
                docker-compose -f src/main/docker/postgres-compose.yml down -v
                docker-compose -f src/main/docker/postgres-compose.yml up -d
                ```

                ## SonarQube

                O projeto inclui um arquivo `docker-compose.yml` para executar o SonarQube localmente.

                **Importante**: O SonarQube utiliza o mesmo container PostgreSQL (`postgres`) criado pelo `postgres-compose.yml`. Certifique-se de que o PostgreSQL está rodando antes de iniciar o SonarQube.

                ### Iniciar o SonarQube

                1. **Primeiro, inicie o PostgreSQL** (se ainda não estiver rodando):
                ```bash
                docker-compose -f src/main/docker/postgres-compose.yml up -d
                ```

                2. **Aguarde alguns segundos** para o PostgreSQL estar totalmente pronto.

                3. **Inicie o SonarQube**:
                ```bash
                docker-compose -f src/main/docker/sonarqube-compose.yml up -d
                ```

                O SonarQube será iniciado com as seguintes configurações:
                - **Porta**: 9000
                - **URL**: http://localhost:9000
                - **Usuário padrão**: admin
                - **Senha padrão**: admin
                - **Banco de dados**: PostgreSQL compartilhado (container `postgres`, banco `sonar` será criado automaticamente)

                ### Parar o SonarQube

                ```bash
                docker-compose -f src/main/docker/sonarqube-compose.yml down
                ```

                ### Verificar status dos containers

                ```bash
                docker ps | grep sonarqube
                ```

                ### Executar análise de código no SonarQube

                Após iniciar o SonarQube, execute a análise do projeto. **Importante**: O projeto deve estar compilado antes da análise. Use `mvn clean verify` antes de `sonar:sonar` ou execute tudo em um único comando.

                O SonarQube requer autenticação após o primeiro login.

                **Opção 1: Usando login e senha**

                ```bash
                mvn clean verify sonar:sonar \\
                    -Dsonar.host.url=http://localhost:9000 \\
                    -Dsonar.login=admin \\
                    -Dsonar.password=sua-senha \\
                    -Dsonar.projectKey=groupId:artifactId \\
                    -Dsonar.projectName=nome-do-projeto \\
                    -Dsonar.java.binaries=target/classes
                ```

                **Opção 2: Usando token (recomendado para produção)**

                Gere um token em: http://localhost:9000/account/security/

                ```bash
                mvn clean verify sonar:sonar \\
                    -Dsonar.host.url=http://localhost:9000 \\
                    -Dsonar.login=seu-token-aqui \\
                    -Dsonar.projectKey=groupId:artifactId \\
                    -Dsonar.projectName=nome-do-projeto \\
                    -Dsonar.java.binaries=target/classes
                ```

                **Parâmetros opcionais:**

                - `-Dsonar.java.binaries=target/classes` - Diretório das classes compiladas (necessário para análise Java)
                - `-Dsonar.java.test.binaries=target/test-classes` - Diretório das classes de teste compiladas
                - `-Dsonar.exclusions=**/web/api/**,**/Application.class` - Excluir código gerado da análise
                - `-Dsonar.sources=src/main/java` - Diretórios de código fonte (padrão)
                - `-Dsonar.tests=src/test/java` - Diretórios de testes (padrão)

                Os resultados estarão disponíveis em: http://localhost:9000

                **Nota**: O projeto será criado automaticamente no SonarQube na primeira análise.

                ## Gatling

                O scaffold já inclui uma simulação Gatling em Java (`src/test/java/%s/gatling/simulation/BasicSimulation.java`) que usa a DSL Java oficial.

                ### Executar Gatling localmente

                ```bash
                mvn gatling:test
                ```

                Ou, caso prefira sempre recompilar toda a árvore:

                ```bash
                mvn clean verify gatling:test
                ```

                O relatório HTML será gerado em `target/gatling/<simulation-name>-<timestamp>/index.html`.

                ## Execução por profile

                %s

                ## Geração de código OpenAPI

                %s

                ## Plugins Maven fundamentais

                O build já vem com os principais plugins corporativos e suas responsabilidades:

                - **`spring-boot-maven-plugin`**: empacota o jar executável e permite executar a aplicação com `mvn spring-boot:run`.
                - **`jacoco-maven-plugin`**: prepara o agente, gera relatórios de cobertura e executa `check`. Ajuste os mínimos no `pom.xml` conforme o progresso dos testes.
                - **`sonar-maven-plugin`**: conecta ao SonarQube. Use `mvn clean verify sonar:sonar` com `-Dsonar.login`/`-Dsonar.password` ou token.
                - **`spotless-maven-plugin`**: aplica formatação automática (`spotless:apply`) no `validate`.
                - **`modernizer-maven-plugin`**: detecta APIs Java obsoletas durante a fase `verify`, garantindo compatibilidade com o Java definido.
                - **`openapi-generator-maven-plugin`**: gera os contratos `web.api` a partir dos arquivos `src/main/resources/openapi/*.yaml`. Cada perfil aponta para seu próprio contrato.
                - **`openapi-generator`** gerado: `Request*DTO` e `Response*DTO` já estão importados no `ExemploResource`.
                - **`gatling-maven-plugin`**: executa os cenários localizados em `src/test/java/%s/gatling/simulation`. Há uma simulação de exemplo em `BasicSimulation.java`; execute `mvn gatling:test` (ou `mvn clean verify`) para compilar a simulação e abrir os relatórios em `target/gatling`.

                ```
                mvn clean verify
                ```

                ### Executando cada plugin separadamente

                - **`jacoco-maven-plugin`**: prepara o agente e valida cobertura mínima.

                  ```bash
                  mvn clean test jacoco:report jacoco:check
                  ```

                - **`spotless-maven-plugin`**: aplica o estilo corporativo (formatação e imports).

                  ```bash
                  mvn spotless:apply
                  ```

                - **`modernizer-maven-plugin`**: identifica o uso de APIs ilegais para a versão do Java.

                  ```bash
                  mvn modernizer:modernizer
                  ```

                - **`openapi-generator-maven-plugin`**: atualiza o contrato OpenAPI do perfil desejado.

                  ```bash
                  mvn openapi-generator:generate -Pdev
                  ```

                - **`gatling-maven-plugin`**: gera e executa as simulações de carga.

                  ```bash
                  mvn clean verify gatling:test
                  ```

                ## Próximos passos

                - Substitua os exemplos em `model`, `repository`, `service` e `web/rest`.
                - Atualize os contratos OpenAPI localizados em `src/main/resources/openapi`.
                - Ajuste as propriedades específicas de cada ambiente nos arquivos `application-*.yml`.
                """.formatted(
                config.artifactId(),                 // 1: título
                config.artifactId(),                 // 2: estrutura
                config.basePackagePath(),             // 3: src/main/java
                config.artifactLowerCase(),           // 4: dev.yaml
                config.artifactLowerCase(),           // 5: homol.yaml
                config.artifactLowerCase(),           // 6: prod.yaml
                config.artifactLowerCase(),           // 7: docker.yaml
                config.artifactLowerCase(),           // 8: docker-externo.yaml
                config.basePackagePath(),             // 9: src/test/java
                config.artifactLowerCase(),           // 10: banco de dados
                config.artifactLowerCase(),           // 11: postgres container
                config.artifactLowerCase(),           // 12: sonarqube container
                config.basePackagePath(),             // 13: gatling path descrição
                config.basePackagePath(),             // 14: gatling path plugin
                profileCommands,                      // 15: comandos de profile
                openApiCommands);                     // 16: comandos OpenAPI
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
}

