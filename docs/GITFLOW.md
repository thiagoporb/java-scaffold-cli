# GitFlow - Fluxo de Trabalho

Este projeto segue o modelo **GitFlow** para gerenciamento de branches e releases.

## Script Helper

Para facilitar o uso do GitFlow, este projeto inclui um script auxiliar: `scripts/gitflow-helper.sh`

### Uso do Script Helper

```bash
# Criar feature branch
./scripts/gitflow-helper.sh feature adicionar-validacao-cpf

# Criar release branch
./scripts/gitflow-helper.sh release 1.0.0

# Criar hotfix branch
./scripts/gitflow-helper.sh hotfix correcao-sql-injection

# Finalizar feature branch
./scripts/gitflow-helper.sh finish-feature adicionar-validacao-cpf

# Finalizar release branch
./scripts/gitflow-helper.sh finish-release 1.0.0

# Finalizar hotfix branch (requer versão)
./scripts/gitflow-helper.sh finish-hotfix correcao-sql-injection 1.0.1
```

O script automatiza a criação e finalização de branches seguindo as melhores práticas do GitFlow.

## Branches Principais

### `main` (Produção)
- Contém código estável e testado
- Sempre pronto para deploy em produção
- Protegida contra pushes diretos
- Cada commit deve ser feito via merge de `release/` ou `hotfix/`

### `develop` (Desenvolvimento)
- Branch principal de desenvolvimento
- Contém código que será incluído na próxima release
- Todos os desenvolvedores trabalham a partir desta branch
- Recebe merges de `feature/` e `hotfix/`

## Branches de Suporte

### `feature/*` (Funcionalidades)
- Criadas a partir de `develop`
- Nomenclatura: `feature/nome-da-funcionalidade`
- Usadas para desenvolver novas funcionalidades
- Após conclusão, fazem merge de volta para `develop`
- **Nunca fazem merge diretamente em `main`**

**Exemplo:**
```bash
# Criar feature branch
git checkout develop
git pull origin develop
git checkout -b feature/adicionar-validacao-cpf

# Trabalhar na feature
git add .
git commit -m "feat: adiciona validação de CPF"

# Finalizar e fazer merge
git checkout develop
git merge --no-ff feature/adicionar-validacao-cpf
git branch -d feature/adicionar-validacao-cpf
git push origin develop
```

### `release/*` (Releases)
- Criadas a partir de `develop` quando está pronta para release
- Nomenclatura: `release/1.0.0` (usando versionamento semântico)
- Usadas para preparar uma nova release de produção
- Apenas correções de bugs permitidas (sem novas features)
- Após conclusão:
  - Merge para `main` (com tag de versão)
  - Merge de volta para `develop`

**Exemplo:**
```bash
# Criar release branch
git checkout develop
git pull origin develop
git checkout -b release/1.0.0

# Preparar release (atualizar versão, CHANGELOG, etc.)
# Apenas correções de bugs se necessário
git commit -m "chore: atualiza versão para 1.0.0"

# Finalizar release
git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# Merge de volta para develop
git checkout develop
git merge --no-ff release/1.0.0
git push origin develop

# Remover branch de release
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

### `hotfix/*` (Correções Urgentes)
- Criadas a partir de `main` para correções críticas em produção
- Nomenclatura: `hotfix/correcao-urgente`
- Usadas para corrigir bugs críticos em produção
- Após conclusão:
  - Merge para `main` (com nova tag de versão)
  - Merge de volta para `develop`

**Exemplo:**
```bash
# Criar hotfix branch
git checkout main
git pull origin main
git checkout -b hotfix/correcao-sql-injection

# Corrigir o problema
git commit -m "fix: corrige vulnerabilidade SQL injection"

# Finalizar hotfix
git checkout main
git merge --no-ff hotfix/correcao-sql-injection
git tag -a v1.0.1 -m "Hotfix version 1.0.1"
git push origin main --tags

# Merge de volta para develop
git checkout develop
git merge --no-ff hotfix/correcao-sql-injection
git push origin develop

# Remover branch de hotfix
git branch -d hotfix/correcao-sql-injection
git push origin --delete hotfix/correcao-sql-injection
```

## Convenções de Commits

Seguimos **Conventional Commits** em português BR:

```
<tipo>: <descrição curta>

[corpo opcional]

[rodapé opcional]
```

### Tipos de Commit

- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação (sem mudança de código)
- `refactor`: Refatoração
- `test`: Adição ou correção de testes
- `chore`: Tarefas de build/configuração
- `perf`: Melhoria de performance
- `ci`: Mudanças em CI/CD

### Exemplos

```bash
# Feature
git commit -m "feat: adiciona suporte a validação de CPF"

# Bug fix
git commit -m "fix: corrige cálculo de desconto em valores negativos"

# Documentação
git commit -m "docs: atualiza instruções de instalação"

# Refatoração
git commit -m "refactor: extrai lógica de validação para classe separada"

# Teste
git commit -m "test: adiciona testes para validação de CPF"
```

## Fluxo de Trabalho Resumido

```mermaid
main (produção)
  ↑
  ├── release/* → merge + tag
  └── hotfix/* → merge + tag
        ↓
      develop (desenvolvimento)
        ↑
        ├── feature/* → merge
        └── hotfix/* → merge (de volta)
```

## Proteções de Branch

### `main`
- ⛔ Push direto desabilitado
- ✅ Requer Pull Request
- ✅ Requer revisão de código
- ✅ Requer testes passando
- ✅ Requer status checks

### `develop`
- ⚠️ Push direto permitido (desenvolvimento ativo)
- ✅ Recomenda-se Pull Request para mudanças significativas
- ✅ Requer testes passando

## Versionamento Semântico

Seguimos [Semantic Versioning](https://semver.org/lang/pt-BR/):

- **MAJOR** (1.0.0): Mudanças incompatíveis na API
- **MINOR** (0.1.0): Novas funcionalidades compatíveis
- **PATCH** (0.0.1): Correções de bugs compatíveis

## Dicas

1. **Sempre mantenha `develop` atualizado:**
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. **Use `--no-ff` nos merges para manter histórico:**
   ```bash
   git merge --no-ff feature/nova-funcionalidade
   ```

3. **Deletar branches locais após merge:**
   ```bash
   git branch -d feature/nova-funcionalidade
   ```

4. **Sincronizar branches remotas:**
   ```bash
   git fetch --prune
   ```

5. **Antes de criar uma feature, certifique-se de que `develop` está atualizada:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/minha-feature
   ```

