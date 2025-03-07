package br.com.servicos_auto.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.PrestadorServicoDTO;
import br.com.servicos_auto.services.ImageService;
import br.com.servicos_auto.services.PrestadorServicoService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/prestadores")
public class PrestadorServicoController {

    @Autowired
    private PrestadorServicoService prestadorServicoService;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<List<PrestadorServicoDTO>> findAll() {

        List<PrestadorServicoDTO> prestadores = prestadorServicoService.findall();
        return ResponseEntity.ok(prestadores);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestadorServicoDTO> findById(@PathVariable Long id) {
        PrestadorServicoDTO prestador = prestadorServicoService.findById(id);
        return ResponseEntity.ok(prestador);
    }

    @PostMapping
    public ResponseEntity<PrestadorServicoDTO> create(@Valid @RequestBody PrestadorServico prestadorServico) {
        PrestadorServicoDTO savedPrestadorServico = prestadorServicoService.create(prestadorServico);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrestadorServico);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PrestadorServicoDTO> update(@PathVariable Long id,
             @RequestBody PrestadorServico prestadorServico) {

        PrestadorServicoDTO updatedPrestador = prestadorServicoService.update(id, prestadorServico);
        return ResponseEntity.ok(updatedPrestador);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestadorServicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para upload de imagem para um prestador de serviço específico

    @PostMapping("/{prestadorId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long prestadorId,
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
            Image image = imageService.uploadPrestadorImage(file, prestadorId);

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
