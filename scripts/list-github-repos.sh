#!/bin/bash

# Script para listar repositórios do GitHub
# Uso: ./scripts/list-github-repos.sh SEU_TOKEN_AQUI

if [ -z "$1" ]; then
    echo "Erro: Token do GitHub não fornecido"
    echo "Uso: $0 GITHUB_TOKEN"
    echo ""
    echo "Para criar um token:"
    echo "1. Acesse: https://github.com/settings/tokens"
    echo "2. Clique em 'Generate new token (classic)'"
    echo "3. Selecione o escopo 'repo' (para repositórios privados) ou 'public_repo' (apenas públicos)"
    exit 1
fi

TOKEN=$1

echo "Listando seus repositórios do GitHub..."
echo ""

curl -s -H "Authorization: token $TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/user/repos?per_page=100&sort=updated" | \
     jq -r '.[] | "\(.name) | \(.private // false | if . then "privado" else "público" end) | \(.html_url)"' | \
     column -t -s '|'

echo ""
echo "Total de repositórios listados."
