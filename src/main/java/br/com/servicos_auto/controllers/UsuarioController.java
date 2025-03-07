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

import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.models.UsuarioDTO;
import br.com.servicos_auto.services.ImageService;
import br.com.servicos_auto.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ImageService imageService;

    // Endpoint para listar todos os usuários
    @Operation(summary = "Lista todos os usuarios")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para buscar um usuário por ID
    @Operation(summary = "Busca usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    // Endpoint para criar um novo usuário
    @Operation(summary = "Cria um novo usuario")
    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.create(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(savedUsuario));
    }

    // Endpoint para atualizar um usuário existente
    @Operation(summary = "Edita um usuario")
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Usuario updatedUsuario = usuarioService.update(id, usuarioDetails);
        return ResponseEntity.ok(new UsuarioDTO(updatedUsuario));
    }

    // Endpoint para deletar um usuário (soft delete)
    @Operation(summary = "Soft Delete um usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para upload de imagem para um usuário específico
    @Operation(summary = "Upload de imagem para um usuario")
    @PostMapping("/{usuarioId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long usuarioId,
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
            Image image = imageService.uploadUsuarioImage(file, usuarioId);

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
