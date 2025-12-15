-- ============================================================
-- Script de inicialização do PostgreSQL
-- Cria o database para o SonarQube
--
-- Este script roda automaticamente quando o container PostgreSQL
-- inicia pela primeira vez (pasta docker-entrypoint-initdb.d)
-- ============================================================

-- Database para o SonarQube
CREATE DATABASE sonar;

-- Concede permissões ao usuário postgres no database
-- (em produção, criar usuário específico para cada serviço)
GRANT ALL PRIVILEGES ON DATABASE sonar TO postgres;

-- Mensagem de confirmação (aparece nos logs do container)
\echo '==========================================='
\echo 'Database criado com sucesso:'
\echo '  - sonar'
\echo '  - %s (criado pelo POSTGRES_DB)'
\echo '==========================================='

