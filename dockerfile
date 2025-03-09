# Estágio de build: usa uma imagem com Maven e Java 21 para compilar o projeto
FROM maven:3.9.9-amazoncorretto-21-al2023 as build

# Define o diretório de trabalho dentro do container
WORKDIR /build

# Copia todos os arquivos do projeto para o diretório /build no container
COPY . .

# Executa o comando Maven para compilar o projeto e gerar o arquivo JAR
# A opção -DskipTests ignora a execução dos testes durante a build
RUN mvn clean package -DskipTests

# Estágio de execução: usa uma imagem leve com apenas o Java 21 para rodar a aplicação
FROM amazoncorretto:21

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado no estágio de build para o diretório /app no container
COPY --from=build ./build/target/*.jar ./servicos_auto.jar

# Baixa o agente do OpenTelemetry e salva no diretório /app
RUN curl -L -o /app/opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# Expõe a porta 8085 para que a aplicação possa ser acessada externamente
EXPOSE 8085

# Configura a variável de ambiente JAVA_TOOL_OPTIONS para carregar o agente do OpenTelemetry
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"
# Configura as variáveis de ambiente para o OpenTelemetry
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://sentinel-otel:4318
ENV OTEL_SERVICE_NAME=servicos_auto
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

# Define o comando padrão para rodar a aplicação quando o container for iniciado
ENTRYPOINT ["java", "-jar", "servicos_auto.jar"]