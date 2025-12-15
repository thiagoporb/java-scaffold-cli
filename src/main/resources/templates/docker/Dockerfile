# ============================================================
# Dockerfile Multi-stage para %1$s
# ============================================================
# Estágio 1: Build com Maven
# Estágio 2: Runtime com JRE mínimo
# ============================================================

# ----- BUILD STAGE -----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia arquivos do Maven
COPY pom.xml .
COPY src ./src

# Instala Maven e faz o build
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# ----- RUNTIME STAGE -----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia o JAR do estágio de build
COPY --from=builder /app/target/*.jar app.jar

# Muda para usuário não-root
USER appuser

# Expõe a porta da aplicação
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/actuator/health || exit 1

# Variáveis de ambiente padrão
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=local

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

