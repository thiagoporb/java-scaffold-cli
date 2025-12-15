#!/bin/bash
# Script de inicialização para criar o banco de dados do SonarQube
# Este script é executado automaticamente quando o PostgreSQL inicia pela primeira vez

set -e

# Cria o banco de dados 'sonar' se não existir
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    SELECT 'CREATE DATABASE sonar'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sonar')\gexec
EOSQL

# O SonarQube criará as tabelas automaticamente ao iniciar

