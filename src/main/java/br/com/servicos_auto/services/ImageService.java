package br.com.servicos_auto.services;

import br.com.servicos_auto.models.Anuncio;
import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.repositories.AnuncioRepository;
import br.com.servicos_auto.repositories.ImageRepository;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;
import br.com.servicos_auto.repositories.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.client-secret}")
    private String clientSecret;

    @Value("${imgur.refresh_token}")
    private String refreshToken;

    private String accessToken;

    private final String BASE_URL = "https://api.imgur.com/3";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrestadorServicoRepository prestadorServicoRepository;

    @Autowired
    private AnuncioRepository anuncioRepository;

    private final WebClient webClient;

    public ImageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    public Image uploadUsuarioImage(MultipartFile file, Long usuarioId) {
        // Obtém o usuário pelo ID
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        // Garante que o access token esteja ativo
        ensureValidAccessToken();
        try {
            // Log do access token
            logger.info("Utilizando access token: {}", accessToken);
            // Faz o upload da imagem no Imgur
            String response = webClient.post()
                    .uri("/image")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // Token autenticado
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("image", file.getResource())
                            .with("client_id", clientId)) // Garante que a conta seja reconhecida
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Log da resposta do Imgur
            logger.info("Resposta do Imgur: {}", response);
            // Extrai os dados da resposta do Imgur
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode == null) {
                logger.error("Erro ao receber os dados da imagem: {}", response);
                throw new RuntimeException("Erro ao obter dados da imagem do Imgur");
            }

            // Cria uma nova entidade Image com os dados do Imgur
            Image image = new Image();
            image.setUrl(dataNode.get("link").asText());
            image.setImgurId(dataNode.get("id").asText());
            image.setImgurDeletehash(dataNode.get("deletehash").asText());
            image.setType(dataNode.get("type").asText());
            image.setUploadedAt(LocalDateTime.now());
            image.setUsuario(usuario);

            // Salva a imagem no banco de dados
            return imageRepository.save(image);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload da imagem: {}", e.getMessage());
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }
    }

    public Image uploadPrestadorImage(MultipartFile file, Long prestadorId) {
        // Obtém o usuário pelo ID
        PrestadorServico prestadorServico = prestadorServicoRepository.findById(prestadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador nao encontrado"));
        // Garante que o access token esteja ativo
        ensureValidAccessToken();
        try {
            // Log do access token
            logger.info("Utilizando access token: {}", accessToken);
            // Faz o upload da imagem no Imgur
            String response = webClient.post()
                    .uri("/image")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // Token autenticado
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("image", file.getResource())
                            .with("client_id", clientId)) // Garante que a conta seja reconhecida
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Log da resposta do Imgur
            logger.info("Resposta do Imgur: {}", response);
            // Extrai os dados da resposta do Imgur
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode == null) {
                logger.error("Erro ao receber os dados da imagem: {}", response);
                throw new RuntimeException("Erro ao obter dados da imagem do Imgur");
            }

            // Cria uma nova entidade Image com os dados do Imgur
            Image image = new Image();
            image.setUrl(dataNode.get("link").asText());
            image.setImgurId(dataNode.get("id").asText());
            image.setImgurDeletehash(dataNode.get("deletehash").asText());
            image.setType(dataNode.get("type").asText());
            image.setUploadedAt(LocalDateTime.now());
            image.setPrestadorServico(prestadorServico);

            // Salva a imagem no banco de dados
            return imageRepository.save(image);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload da imagem: {}", e.getMessage());
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }
    }

    public Image uploadAnuncioImage(MultipartFile file, Long anuncioId) {
        // Obtém o usuário pelo ID
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado"));
        // Garante que o access token esteja ativo
        ensureValidAccessToken();
        try {
            // Log do access token
            logger.info("Utilizando access token: {}", accessToken);
            // Faz o upload da imagem no Imgur
            String response = webClient.post()
                    .uri("/image")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // Token autenticado
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("image", file.getResource())
                            .with("client_id", clientId)) // Garante que a conta seja reconhecida
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Log da resposta do Imgur
            logger.info("Resposta do Imgur: {}", response);
            // Extrai os dados da resposta do Imgur
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode == null) {
                logger.error("Erro ao receber os dados da imagem: {}", response);
                throw new RuntimeException("Erro ao obter dados da imagem do Imgur");
            }

            // Cria uma nova entidade Image com os dados do Imgur
            Image image = new Image();
            image.setUrl(dataNode.get("link").asText());
            image.setImgurId(dataNode.get("id").asText());
            image.setImgurDeletehash(dataNode.get("deletehash").asText());
            image.setType(dataNode.get("type").asText());
            image.setUploadedAt(LocalDateTime.now());
            image.setAnuncio(anuncio);

            // Salva a imagem no banco de dados
            return imageRepository.save(image);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload da imagem: {}", e.getMessage());
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }
    }

    public String getAccessToken() {
        try {
            // // Log dos parâmetros para garantir que estão corretos
            // logger.info("Tentando obter token com refresh_token: {}", refreshToken);
            // logger.info("client_id: {}", clientId);
            // logger.info("client_secret: {}", clientSecret);

            // Faz a requisição para obter o access token
            String response = webClient.post()
                    .uri("https://api.imgur.com/oauth2/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("refresh_token", refreshToken)
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("grant_type", "refresh_token"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Log da resposta para verificar o retorno
            logger.info("Resposta de autenticação do Imgur: {}", response);

            if (response == null || response.isEmpty()) {
                logger.error("A resposta do Imgur foi nula ou vazia.");
                throw new RuntimeException("Resposta de autenticação do Imgur invalida.");
            }

            // Extrai o access token da resposta
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            this.accessToken = jsonNode.get("access_token").asText(); // Atualiza o access token

            if (accessToken == null || accessToken.isEmpty()) {
                logger.error("Access token não encontrado na resposta.");
                throw new RuntimeException("Falha ao obter o access token do Imgur.");
            }

            return this.accessToken;
        } catch (Exception e) {
            logger.error("Erro ao obter o access token: {}", e.getMessage());
            throw new RuntimeException("Falha ao obter o access token", e);
        }
    }

    public boolean isTokenValid() {
        try {
            // Verifica a validade do token
            String response = webClient.get()
                    .uri("/account/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Log da resposta para verificar se o token é válido
            logger.info("Resposta da verificaçao do token: {}", response);

            return true;
        } catch (WebClientResponseException e) {
            logger.error("Token invalido: {}", e.getMessage());
            return false;
        }
    }

    public void ensureValidAccessToken() {
        if (!isTokenValid()) {
            logger.info("Access token invalido. Obtendo novo token...");
            getAccessToken();
        }
    }
}
