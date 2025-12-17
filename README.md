# Ferramenta de Scaffolding Maven + Spring Boot

Este repositório contém o utilitário `scaffold-cli`, implementado em Java 21 e distribuído como JAR executável. A ferramenta gera projetos base Spring Boot seguindo padrões corporativos estabelecidos.

## Pré-requisitos

- Java 21 LTS (ou superior compatível)
- Maven 3.9+

## Build

Para empacotar o CLI execute:

```bash
mvn clean package
```

O artefato gerado estará em `target/scaffold-cli.jar`.

## Uso

```bash
java -jar target/scaffold-cli.jar --groupId net.jlstechnology --artifactId synccta
```

Exemplo com suporte a AWS:
```bash
java -jar target/scaffold-cli.jar --groupId net.jlstechnology --artifactId meuprojeto --cloud aws --outputDir /home/user/projetos --force
```

Se algum parâmetro obrigatório não for informado, o CLI solicitará o valor interativamente.

### Argumentos disponíveis

- `--groupId` / `--group-id`: `groupId` Maven do projeto a ser gerado.
- `--artifactId` / `--artifact-id`: `artifactId` Maven do projeto.
- `--outputDir` / `--output-dir`: diretório destino (padrão: diretório atual).
- `--cloud`: provedor de cloud (atualmente suportado: `aws`). Quando `--cloud aws` é informado, o scaffold gera automaticamente configuração do LocalStack no `docker-compose.yml` e adiciona dependências AWS SDK e Testcontainers LocalStack.
- `--force`: sobrescreve o diretório de saída se já existir.

## Estrutura gerada

O projeto final conterá:

- `pom.xml` com Java 21, Spring Boot 4.0.0 e os plugins/dependências obrigatórios (Jacoco, Sonar, Spotless, Modernizer, OpenAPI Generator e Gatling).
- Perfis Maven `dev`, `homol`, `prod`, `docker` e `docker-externo`, cada um com seu respectivo `application-<profile>.yml` e contrato OpenAPI.
- Estrutura de pacotes Java (`model`, `repository`, `service`, `service.mapper`, `web.rest`) e testes espelhados.
- Testes de arquitetura com ArchUnit e testes unitários de exemplo.
- Testes de carga com Gatling (simulação de exemplo incluída).
- README gerado automaticamente com instruções de uso do projeto destino.

## Configuração do Gatling

O scaffold inclui suporte a testes de carga com Gatling, configurado seguindo o padrão do [JHipster](https://github.com/jhipster/jhipster-sample-app).

### Versões utilizadas

- **Gatling**: `3.13.5`
- **Gatling Maven Plugin**: `4.16.3`

### Executando testes de carga

**Importante**: Para executar os testes do Gatling com sucesso, a aplicação Spring Boot deve estar rodando.

1. **Inicie a aplicação** (em um terminal):
```bash
mvn spring-boot:run -Pdev
```

2. **Execute os testes do Gatling** (em outro terminal):
```bash
mvn gatling:test
```

O relatório HTML será gerado em `target/gatling/<simulation-name>-<timestamp>/index.html`.

### Sobre erros de conexão

Se você receber erros como `Connection refused` ao executar `mvn gatling:test`, isso indica que a aplicação Spring Boot não está rodando. O Gatling está tentando se conectar a `http://localhost:8080`, mas não encontra um servidor ativo.

**Solução**: Certifique-se de que a aplicação está rodando antes de executar os testes do Gatling.

### Configuração

O plugin Gatling está configurado no `pom.xml` gerado com:
- `runMultipleSimulations: true` - permite executar múltiplas simulações
- Inclusão automática da simulação de exemplo: `BasicSimulation`

A configuração não requer dependências explícitas do Netty, pois o Gatling já inclui todas as dependências necessárias, evitando conflitos de versão.

## Fluxo de Trabalho (GitFlow)

Este projeto segue o modelo **GitFlow** para gerenciamento de branches e releases.

- **`main`**: Código estável e testado, pronto para produção
- **`develop`**: Branch principal de desenvolvimento
- **`feature/*`**: Novas funcionalidades
- **`release/*`**: Preparação de releases
- **`hotfix/*`**: Correções urgentes em produção

Para mais detalhes sobre o GitFlow, consulte o arquivo [docs/GITFLOW.md](docs/GITFLOW.md).

## CI/CD com GitHub Actions

Este projeto inclui workflows do GitHub Actions configurados para pipeline CI/CD:

### Workflows Disponíveis

1. **CI** (`.github/workflows/ci.yml`)
   - Executa em push/PR para `main` e `develop`
   - Build e testes em Java 21
   - Upload de artefatos (JAR e relatórios)

2. **Release** (`.github/workflows/release.yml`)
   - Executa quando uma tag `v*` é criada
   - Valida versão no `pom.xml`
   - Gera release no GitHub com JAR anexado

3. **Code Quality** (`.github/workflows/code-quality.yml`)
   - Validações de qualidade de código
   - Upload de cobertura (Codecov)

4. **PR Validation** (`.github/workflows/pr-validation.yml`)
   - Valida Conventional Commits em Pull Requests
   - Executa testes e build
   - Comenta status no PR

### Requisitos

Para os workflows funcionarem, o token do GitHub precisa ter escopo `workflow` habilitado:
1. Acesse: https://github.com/settings/tokens
2. Edite ou crie um token com permissão `workflow`
3. Configure no Cursor/IDE ou use SSH para pushes

## Próximos passos sugeridos

- Rodar o CLI para criar um novo serviço base.
- Ajustar os contratos OpenAPI gerados e regenerar os clientes.
- Customizar as entidades, serviços, repositórios e recursos conforme o domínio desejado.
- Após gerar um projeto, execute `mvn clean package` no diretório criado para compilar, gerar as classes OpenAPI e produzir a pasta `target/`.

