# %1$s

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
%2$s/
  ├── pom.xml
  ├── README.md
  ├── Dockerfile
  ├── src/main/
  │   ├── docker/
  │   │   ├── docker-compose.yml
  │   │   ├── prometheus/
  │   │   │   └── prometheus.yml
  │   │   ├── grafana/
  │   │   │   └── provisioning/
  │   │   │       ├── datasources/
  │   │   │       │   └── datasources.yml
  │   │   │       └── dashboards/
  │   │   │           └── dashboards.yml
  │   │   └── postgres/
  │   │       └── 01-create-databases.sql%20$s
  │   └── java/%3$s/
  │   ├── model/
  │   ├── repository/
  │   ├── service/
  │   │   └── mapper/
  │   ├── web/rest/%21$s
  ├── src/main/resources/
  │   ├── application.yml
  │   ├── application-dev.yml
  │   ├── application-homol.yml
  │   ├── application-prod.yml
  │   ├── application-docker.yml
  │   ├── application-docker-externo.yml
  │   └── openapi/
  │       ├── %4$s-dev.yaml
  │       ├── %5$s-homol.yaml
  │       ├── %6$s-prod.yaml
  │       ├── %7$s-docker.yaml
  │       └── %8$s-docker-externo.yaml
  └── src/test/java/%9$s/
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

## Docker Compose - Ambiente Completo

O projeto inclui um arquivo `docker-compose.yml` consolidado que configura todos os serviços necessários para desenvolvimento local.

### Iniciar todos os serviços

A partir da raiz do projeto, execute:

```bash
cd src/main/docker
docker compose up -d
```

Ou a partir da raiz:

```bash
docker compose -f src/main/docker/docker-compose.yml up -d
```

Isso iniciará todos os serviços:
- **PostgreSQL** (porta 5432) - Banco de dados principal
- **SonarQube** (porta 9000) - Análise estática de código
- **Prometheus** (porta 9090) - Coleta de métricas
- **Grafana** (porta 3000) - Visualização de métricas
- **%1$s-service** (porta 8081) - Serviço gerado%17$s

### Parar todos os serviços

```bash
cd src/main/docker
docker compose down
```

Ou:

```bash
docker compose -f src/main/docker/docker-compose.yml down
```

### Verificar status dos containers

```bash
docker ps
```

### Parar e limpar volumes (APAGA DADOS!)

```bash
cd src/main/docker
docker compose down -v
```

## Banco de Dados PostgreSQL

O PostgreSQL é iniciado automaticamente pelo `docker-compose.yml` principal.

**Configurações:**
- **Porta**: 5432 (exposta em todas as interfaces: `0.0.0.0:5432`)
- **Banco de dados principal**: %10$s
- **Banco SonarQube**: sonar (criado automaticamente)
- **Usuário**: postgres
- **Senha**: postgres (padrão, configure via variáveis de ambiente)

**Nota para conexão externa (DBeaver, etc.)**:
- Se o Docker estiver rodando no WSL2, use `localhost` ou `127.0.0.1` para conectar do Windows.
- A porta 5432 está exposta em todas as interfaces (`0.0.0.0:5432`), garantindo acesso externo.
- Se ainda houver problemas de conexão, verifique se o firewall do Windows não está bloqueando a porta 5432.

**Nota sobre o banco SonarQube**: O script SQL em `src/main/docker/postgres/01-create-databases.sql` cria automaticamente o banco `sonar` na primeira inicialização do PostgreSQL. Se o volume do PostgreSQL já existir, o script não será executado. Neste caso, você precisará criar o banco manualmente ou recriar o volume:

```bash
# Criar o banco manualmente
docker exec -it postgres psql -U postgres -c "CREATE DATABASE sonar;"

# OU recriar o volume (apaga todos os dados!)
docker compose -f src/main/docker/docker-compose.yml down -v
docker compose -f src/main/docker/docker-compose.yml up -d
```

## SonarQube

O SonarQube é iniciado automaticamente pelo `docker-compose.yml` principal.

**Configurações:**
- **Porta**: 9000
- **URL**: http://localhost:9000
- **Usuário padrão**: admin
- **Senha padrão**: admin
- **Banco de dados**: PostgreSQL compartilhado (container `postgres`, banco `sonar` criado automaticamente)

**Importante**: O SonarQube utiliza o mesmo container PostgreSQL (`postgres`) e depende que o PostgreSQL esteja saudável antes de iniciar.

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

## Prometheus

O Prometheus é iniciado automaticamente pelo `docker-compose.yml` principal e coleta métricas dos serviços.

**Configurações:**
- **Porta**: 9090
- **URL**: http://localhost:9090
- **Scrape interval**: 15 segundos

**Métricas coletadas:**
- Métricas do próprio Prometheus (auto-monitoramento)
- Métricas do %1$s-service via endpoint `/api/actuator/prometheus`

**Acessar métricas do serviço:**
- Diretamente: http://localhost:8081/api/actuator/prometheus
- Via Prometheus: http://localhost:9090 (consultar métricas usando PromQL)

## Grafana

O Grafana é iniciado automaticamente pelo `docker-compose.yml` principal e permite visualizar métricas coletadas pelo Prometheus.

**Configurações:**
- **Porta**: 3000
- **URL**: http://localhost:3000
- **Usuário padrão**: admin
- **Senha padrão**: admin

**Datasources provisionadas automaticamente:**
- **Prometheus**: http://prometheus:9090 (default)
- **Jaeger**: http://jaeger:16686 (se Jaeger estiver configurado)

**Primeiro acesso:**
1. Acesse http://localhost:3000
2. Faça login com `admin` / `admin`
3. O Grafana solicitará alteração da senha (opcional)
4. Os datasources já estarão configurados automaticamente

**Criar dashboards:**
- Use o datasource Prometheus para criar dashboards personalizados
- Explore métricas do serviço usando PromQL

%18$s

## Observabilidade

O projeto está configurado para observabilidade completa:

**Métricas (Prometheus + Grafana):**
- CPU, memória, requisições HTTP
- Tempo de resposta, taxa de erro
- Métricas customizadas do Spring Boot Actuator

**Verificações de saúde:**
- Liveness: http://localhost:8081/api/actuator/health/liveness
- Readiness: http://localhost:8081/api/actuator/health/readiness
- Health geral: http://localhost:8081/api/actuator/health

**Endpoints do Actuator expostos:**
- `/api/actuator/health` - Status de saúde
- `/api/actuator/info` - Informações da aplicação
- `/api/actuator/metrics` - Lista de métricas disponíveis
- `/api/actuator/prometheus` - Métricas no formato Prometheus

## Swagger UI - Documentação da API

O projeto utiliza **SpringDoc OpenAPI** para gerar automaticamente a documentação interativa da API.

**Acesso:**
- **Swagger UI**: http://localhost:8081/api/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/api/v3/api-docs
- **OpenAPI YAML**: http://localhost:8081/api/v3/api-docs.yaml

**Funcionalidades:**
- Interface web interativa para testar os endpoints da API
- Documentação automática baseada nos contratos OpenAPI em `src/main/resources/openapi/`
- Teste de requisições diretamente pelo navegador
- Visualização de schemas, modelos e exemplos

**Nota**: A documentação é gerada automaticamente a partir dos arquivos OpenAPI YAML localizados em `src/main/resources/openapi/`. Cada perfil (`dev`, `homol`, `prod`, etc.) possui seu próprio contrato OpenAPI.

## Secrets Manager (DatabaseConfig)

Quando o projeto é gerado com `--cloud aws`, o `DatabaseConfig` busca as credenciais do banco no AWS Secrets Manager.

**Secret esperado (dev):**
- Nome: `dev/{artifactId}/db`
- Valor (LocalStack/dev):
```json
{
  "username": "postgres",
  "password": "postgres",
  "host": "localhost",
  "port": "5432",
  "database": "{artifactId}"
}
```

**Criar o secret no LocalStack (antes de subir em dev):**
- Opção 1: executar `src/main/docker/localstack/init-aws.sh`
- Opção 2 (manual):
```bash
awslocal secretsmanager create-secret \
  --name dev/{artifactId}/db \
  --secret-string '{"username":"postgres","password":"postgres","host":"localhost","port":"5432","database":"{artifactId}"}'
```

**Subir a aplicação em dev (após criar o secret):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Execução por profile

%15$s

## Geração de código OpenAPI

%16$s

## Plugins Maven fundamentais

O build já vem com os principais plugins corporativos e suas responsabilidades:

- **`spring-boot-maven-plugin`**: empacota o jar executável e permite executar a aplicação com `mvn spring-boot:run`.
- **`jacoco-maven-plugin`**: prepara o agente, gera relatórios de cobertura e executa `check`. Ajuste os mínimos no `pom.xml` conforme o progresso dos testes.
- **`sonar-maven-plugin`**: conecta ao SonarQube. Use `mvn clean verify sonar:sonar` com `-Dsonar.login`/`-Dsonar.password` ou token.
- **`spotless-maven-plugin`**: aplica formatação automática (`spotless:apply`) no `validate`.
- **`modernizer-maven-plugin`**: detecta APIs Java obsoletas durante a fase `verify`, garantindo compatibilidade com o Java definido.
- **`openapi-generator-maven-plugin`**: gera os contratos `web.api` a partir dos arquivos `src/main/resources/openapi/*.yaml`. Cada perfil aponta para seu próprio contrato.
- **`openapi-generator`** gerado: `Request*DTO` e `Response*DTO` já estão importados no `ExemploResource`.

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

  ```

%19$s

## Próximos passos

- Substitua os exemplos em `model`, `repository`, `service` e `web/rest`.
- Atualize os contratos OpenAPI localizados em `src/main/resources/openapi`.
- Ajuste as propriedades específicas de cada ambiente nos arquivos `application-*.yml`.

