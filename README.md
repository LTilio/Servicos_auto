# Serviços Auto

Este projeto é uma API para a busca de fornecedores de serviços automotivos, desenvolvida com Spring Boot e instrumentada com OpenTelemetry. A aplicação está containerizada e pode ser facilmente executada via Docker.

## 🎯 Observabilidade
Este projeto já está instrumentado com OpenTelemetry. Os traces, logs e métricas serão enviados para os serviços configurados. Você pode visualizar os dados no Grafana ou outra ferramenta conectada ao OpenTelemetry.

## 📌 Pré-requisitos
Antes de iniciar, certifique-se de ter os seguintes softwares instalados:
- Docker
- Git
- Postman (para obter as credenciais do Imgur)

## 🏗️ Passo a passo para rodar o projeto

### 1️⃣ Clonar o repositório
```sh
 git clone https://github.com/LTilio/Servicos_auto.git
```
```sh
cd servicos-auto
```

### 2️⃣ Criar o arquivo `application.properties`
Antes de rodar o projeto, crie o arquivo `application.properties` na pasta `src/main/resources/` usando o `application.properties.exemple` como base.

### 3️⃣ Criar a rede Docker (se ainda não existir)
```sh
docker network create sentinel
```

### 4️⃣ Construir e subir os containers
```sh
docker compose up --build -d
```
Isso irá:
- Criar o container do banco de dados PostgreSQL
- Criar o container da aplicação Spring Boot
- Configurar a instrumentação com OpenTelemetry

### 5️⃣ Acessar a API
A API estará rodando em `http://localhost:8085`

### 6️⃣ Documentação Interativa (Swagger UI)
A documentação interativa pode ser acessada em:
```sh
http://localhost:8085/docs
```

### 7️⃣ Para visualizar os logs
Caso precise visualizar os logs da aplicação, utilize:
```sh
docker logs -f servicos_auto
```

### 8️⃣ Para parar os containers
Se precisar parar a aplicação, execute:
```sh
docker compose down
```

## 📝 Configurando o Imgur para upload de imagens
A API utiliza o Imgur para armazenar imagens. Para configurar as credenciais, siga os passos abaixo:

### 1️⃣ Criar uma conta no Imgur
Acesse [Imgur](https://imgur.com/) e crie uma conta, caso ainda não tenha uma.

### 2️⃣ Criar uma aplicação no Imgur
1. Acesse [Imgur API](https://api.imgur.com/oauth2/addclient)
2. Escolha "OAuth 2 authorization without a callback URL"
3. Preencha os dados necessários e clique em "Submit"
4. Após a criação, você receberá um `Client ID` e `Client Secret`

### 3️⃣ Obter o Refresh Token
1. Acesse a documentação oficial do Imgur: [https://apidocs.imgur.com/#intro](https://apidocs.imgur.com/#intro)
2. No Postman, clique na aba **Authorization**
3. Clique em **Get New Access Token**
4. Preencha os seguintes campos:
   - **Auth URL**: `https://api.imgur.com/oauth2/authorize`
   - **Access Token URL**: `https://api.imgur.com/oauth2/token`
   - **Client ID**: Seu Client ID do Imgur
   - **Client Secret**: Seu Client Secret do Imgur
5. Clique em **Request Token**
6. Copie o `refresh_token` da resposta e adicione ao `application.properties`
7. Copie os valores gerados e adicione ao `application.properties`:
```
imgur.client-id=SEU_CLIENT_ID
imgur.client-secret=SEU_CLIENT_SECRET
imgur.refresh_token=SEU_REFRESH_TOKEN
```

### 🔥 Observações
- O banco de dados roda na porta `5434`.
- A API roda na porta `8085`.
- Os dados do PostgreSQL não são persistidos após `docker compose down`.
- OpenTelemetry já está configurado para capturar logs, métricas e traces.

