#!/bin/bash

# GitFlow Helper Script
# Facilita a criação e gerenciamento de branches seguindo o GitFlow

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_usage() {
    echo "GitFlow Helper - Facilita o uso do GitFlow"
    echo ""
    echo "Uso:"
    echo "  $0 feature <nome-da-feature>     - Cria uma nova feature branch"
    echo "  $0 release <versao>               - Cria uma nova release branch"
    echo "  $0 hotfix <nome-do-hotfix>        - Cria uma nova hotfix branch"
    echo "  $0 finish-feature <nome-da-feature> - Finaliza uma feature branch"
    echo "  $0 finish-release <versao>        - Finaliza uma release branch"
    echo "  $0 finish-hotfix <nome-do-hotfix> - Finaliza uma hotfix branch"
    echo ""
    echo "Exemplos:"
    echo "  $0 feature adicionar-validacao-cpf"
    echo "  $0 release 1.0.0"
    echo "  $0 hotfix correcao-sql-injection"
}

check_branch_exists() {
    local branch=$1
    if git rev-parse --verify "$branch" >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

start_feature() {
    local feature_name=$1
    
    if [ -z "$feature_name" ]; then
        echo -e "${RED}Erro: Nome da feature é obrigatório${NC}"
        exit 1
    fi
    
    local branch="feature/$feature_name"
    
    if check_branch_exists "$branch"; then
        echo -e "${YELLOW}Branch $branch já existe${NC}"
        git checkout "$branch"
        return 0
    fi
    
    echo -e "${BLUE}Atualizando develop...${NC}"
    git checkout develop
    git pull origin develop
    
    echo -e "${GREEN}Criando feature branch: $branch${NC}"
    git checkout -b "$branch"
    
    echo -e "${GREEN}Feature branch criada: $branch${NC}"
    echo -e "${YELLOW}Você está agora na branch: $branch${NC}"
}

start_release() {
    local version=$1
    
    if [ -z "$version" ]; then
        echo -e "${RED}Erro: Versão é obrigatória (ex: 1.0.0)${NC}"
        exit 1
    fi
    
    local branch="release/$version"
    
    if check_branch_exists "$branch"; then
        echo -e "${YELLOW}Branch $branch já existe${NC}"
        git checkout "$branch"
        return 0
    fi
    
    echo -e "${BLUE}Atualizando develop...${NC}"
    git checkout develop
    git pull origin develop
    
    echo -e "${GREEN}Criando release branch: $branch${NC}"
    git checkout -b "$branch"
    git push -u origin "$branch"
    
    echo -e "${GREEN}Release branch criada: $branch${NC}"
    echo -e "${YELLOW}Você está agora na branch: $branch${NC}"
}

start_hotfix() {
    local hotfix_name=$1
    
    if [ -z "$hotfix_name" ]; then
        echo -e "${RED}Erro: Nome do hotfix é obrigatório${NC}"
        exit 1
    fi
    
    local branch="hotfix/$hotfix_name"
    
    if check_branch_exists "$branch"; then
        echo -e "${YELLOW}Branch $branch já existe${NC}"
        git checkout "$branch"
        return 0
    fi
    
    echo -e "${BLUE}Atualizando main...${NC}"
    git checkout main
    git pull origin main
    
    echo -e "${GREEN}Criando hotfix branch: $branch${NC}"
    git checkout -b "$branch"
    git push -u origin "$branch"
    
    echo -e "${GREEN}Hotfix branch criada: $branch${NC}"
    echo -e "${YELLOW}Você está agora na branch: $branch${NC}"
}

finish_feature() {
    local feature_name=$1
    
    if [ -z "$feature_name" ]; then
        echo -e "${RED}Erro: Nome da feature é obrigatório${NC}"
        exit 1
    fi
    
    local branch="feature/$feature_name"
    
    if ! check_branch_exists "$branch"; then
        echo -e "${RED}Erro: Branch $branch não existe${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}Finalizando feature: $branch${NC}"
    
    # Voltar para develop
    git checkout develop
    git pull origin develop
    
    # Merge no-ff para manter histórico
    echo -e "${BLUE}Fazendo merge de $branch em develop...${NC}"
    git merge --no-ff "$branch" -m "Merge branch '$branch' into develop"
    
    # Deletar branch local
    echo -e "${YELLOW}Deletando branch local: $branch${NC}"
    git branch -d "$branch"
    
    # Push
    echo -e "${GREEN}Fazendo push para develop...${NC}"
    git push origin develop
    
    echo -e "${GREEN}Feature finalizada com sucesso!${NC}"
}

finish_release() {
    local version=$1
    
    if [ -z "$version" ]; then
        echo -e "${RED}Erro: Versão é obrigatória (ex: 1.0.0)${NC}"
        exit 1
    fi
    
    local branch="release/$version"
    local tag="v$version"
    
    if ! check_branch_exists "$branch"; then
        echo -e "${RED}Erro: Branch $branch não existe${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}Finalizando release: $branch${NC}"
    
    # Merge para main
    echo -e "${BLUE}Fazendo merge de $branch em main...${NC}"
    git checkout main
    git pull origin main
    git merge --no-ff "$branch" -m "Merge branch '$branch' into main - Release $version"
    
    # Criar tag
    echo -e "${BLUE}Criando tag: $tag${NC}"
    git tag -a "$tag" -m "Release version $version"
    
    # Push main e tags
    echo -e "${GREEN}Fazendo push para main...${NC}"
    git push origin main
    git push origin --tags
    
    # Merge de volta para develop
    echo -e "${BLUE}Fazendo merge de $branch em develop...${NC}"
    git checkout develop
    git pull origin develop
    git merge --no-ff "$branch" -m "Merge branch '$branch' into develop - Release $version"
    git push origin develop
    
    # Deletar branch local e remota
    echo -e "${YELLOW}Deletando branch: $branch${NC}"
    git branch -d "$branch"
    git push origin --delete "$branch" || true
    
    echo -e "${GREEN}Release finalizada com sucesso!${NC}"
    echo -e "${GREEN}Tag criada: $tag${NC}"
}

finish_hotfix() {
    local hotfix_name=$1
    local version=$2
    
    if [ -z "$hotfix_name" ]; then
        echo -e "${RED}Erro: Nome do hotfix é obrigatório${NC}"
        exit 1
    fi
    
    if [ -z "$version" ]; then
        echo -e "${RED}Erro: Versão é obrigatória (ex: 1.0.1)${NC}"
        exit 1
    fi
    
    local branch="hotfix/$hotfix_name"
    local tag="v$version"
    
    if ! check_branch_exists "$branch"; then
        echo -e "${RED}Erro: Branch $branch não existe${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}Finalizando hotfix: $branch${NC}"
    
    # Merge para main
    echo -e "${BLUE}Fazendo merge de $branch em main...${NC}"
    git checkout main
    git pull origin main
    git merge --no-ff "$branch" -m "Merge branch '$branch' into main - Hotfix $version"
    
    # Criar tag
    echo -e "${BLUE}Criando tag: $tag${NC}"
    git tag -a "$tag" -m "Hotfix version $version"
    
    # Push main e tags
    echo -e "${GREEN}Fazendo push para main...${NC}"
    git push origin main
    git push origin --tags
    
    # Merge de volta para develop
    echo -e "${BLUE}Fazendo merge de $branch em develop...${NC}"
    git checkout develop
    git pull origin develop
    git merge --no-ff "$branch" -m "Merge branch '$branch' into develop - Hotfix $version"
    git push origin develop
    
    # Deletar branch local e remota
    echo -e "${YELLOW}Deletando branch: $branch${NC}"
    git branch -d "$branch"
    git push origin --delete "$branch" || true
    
    echo -e "${GREEN}Hotfix finalizado com sucesso!${NC}"
    echo -e "${GREEN}Tag criada: $tag${NC}"
}

# Main
case "$1" in
    feature)
        start_feature "$2"
        ;;
    release)
        start_release "$2"
        ;;
    hotfix)
        start_hotfix "$2"
        ;;
    finish-feature)
        finish_feature "$2"
        ;;
    finish-release)
        finish_release "$2"
        ;;
    finish-hotfix)
        finish_hotfix "$2" "$3"
        ;;
    *)
        print_usage
        exit 1
        ;;
esac

