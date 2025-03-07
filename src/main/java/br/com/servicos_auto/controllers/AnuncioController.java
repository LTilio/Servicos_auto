package br.com.servicos_auto.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.servicos_auto.models.Anuncio;
import br.com.servicos_auto.models.AnuncioDTO;
import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.services.AnuncioService;
import br.com.servicos_auto.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<List<AnuncioDTO>> findAll() {
        List<AnuncioDTO> anuncios = anuncioService.findAll();
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDTO> findById(@PathVariable Long id) {
        AnuncioDTO anuncio = anuncioService.findById(id);
        return ResponseEntity.ok(anuncio);
    }

    @Operation(summary = "Cria um novo anúncio", description = "Endpoint para cadastrar um novo anúncio associado a um prestador de serviço.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{prestadorId}")
    public ResponseEntity<AnuncioDTO> create(
            @Parameter(description = "ID do prestador de serviço", required = true) @PathVariable Long prestadorId,
            @Valid @RequestBody Anuncio anuncio) {
        AnuncioDTO savedAnuncio = anuncioService.create(anuncio, prestadorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnuncio);
    }

    @PatchMapping("/{AnuncioId}")
    public ResponseEntity<AnuncioDTO> update(@PathVariable Long AnuncioId, @RequestBody Anuncio anuncio) {
        AnuncioDTO UpdatedAnuncio = anuncioService.update(AnuncioId, anuncio);
        return ResponseEntity.ok(UpdatedAnuncio);
    }

    @DeleteMapping("/{AnuncioId}")
    public ResponseEntity<Void> delete(@PathVariable Long AnuncioId) {
        anuncioService.delete(AnuncioId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para upload de imagem para um Anuncio específico
    @PostMapping("/{AnuncioId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long AnuncioId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Verifica se o arquivo é nulo
            if (file == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Verifica se o tipo MIME é nulo ou não é uma imagem
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Faz o upload da imagem
            Image image = imageService.uploadAnuncioImage(file, AnuncioId);

            // Retorna a imagem salva
            return ResponseEntity.ok(new ImageDTO(image));
        } catch (ResponseStatusException e) {
            // Captura exceções relacionadas a usuário não encontrado
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            // Captura outros erros inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
