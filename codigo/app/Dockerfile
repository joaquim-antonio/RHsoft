# syntax=docker/dockerfile:1
# ---------------------------------------------------------------------------
# RHSoft - Backend (Spring Boot 3.5.6 / Java 21)
#
# Build multi-stage: a imagem final contem apenas a JRE e o JAR, sem Maven,
# nem sources, nem o .m2 de build.
#
# Build:  docker build -t rhsoft-backend ./codigo/app
# ---------------------------------------------------------------------------

# ------------------------------- build ------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copia primeiro apenas o manifesto, aproveitando o cache de layer do Maven
# para as dependencias (so re-resolve quando o pom.xml muda).
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B dependency:go-offline

# Agora o codigo-fonte. Testes sao pulados: sao executados no pipeline, nao no build da imagem.
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B clean package -DskipTests

# ------------------------------- runtime ----------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Roda como usuario nao-root: imagem base nao traz usuario nao-root pronto.
RUN addgroup -S rhsoft && adduser -S rhsoft -G rhsoft

# O JAR final e o unico artefato necessario em runtime.
COPY --from=build /build/target/*.jar app.jar
RUN chown -R rhsoft:rhsoft /app

USER rhsoft

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"

# Healthcheck interno. O actuator nao esta no projeto, entao usamos um endpoint
# publico que exercita a cadeia inteira: Tomcat + Security (permitAll) + JPA.
# /api/v1/vagas/disponiveis responde 200 mesmo com a base CBO vazia, e falha
# se o banco ainda nao estiver acessivel.
#
# O start-period de 90s cobre a subida do container mais o seed do CBO.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD wget -q --spider http://127.0.0.1:8080/api/v1/vagas/disponiveis || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
