#!/bin/bash
# ============================================================
# Script de Inicialização do LocalStack
# ============================================================
# Este script é executado automaticamente quando o LocalStack
# inicia. Ele cria os recursos AWS necessários para desenvolvimento.
#
# IMPORTANTE:
#   - Ajuste os nomes dos buckets, filas e tópicos conforme
#     os microserviços que você vai criar
#   - Este script usa awslocal (AWS CLI pré-configurado para LocalStack)
# ============================================================

set -e

echo "=============================================="
echo "Inicializando recursos AWS no LocalStack..."
echo "=============================================="

%1$s%2$s%3$s%4$s%5$s%6$s%7$s
echo ""
echo "=============================================="
echo "Recursos AWS criados com sucesso!"
echo "=============================================="
echo ""
echo "S3 Buckets:"
awslocal s3 ls 2>/dev/null || echo "    (nenhum bucket criado)"
echo ""
echo "SQS Queues:"
awslocal sqs list-queues --query 'QueueUrls' --output table 2>/dev/null || echo "    (nenhuma fila criada)"
echo ""
echo "SNS Topics:"
awslocal sns list-topics --query 'Topics[*].TopicArn' --output table 2>/dev/null || echo "    (nenhum tópico criado)"
echo ""
echo "Secrets:"
awslocal secretsmanager list-secrets --query 'SecretList[*].Name' --output table 2>/dev/null || echo "    (nenhum secret criado)"
echo ""
echo "=============================================="
echo "LocalStack pronto para uso!"
echo "=============================================="
echo ""
echo "ENDPOINTS:"
echo "  - LocalStack Health: http://localhost:4566/_localstack/health"
echo ""

