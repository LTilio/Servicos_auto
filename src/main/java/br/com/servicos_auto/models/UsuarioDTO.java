package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private List<String> roles;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<ImageDTO> images;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.roles = usuario.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        this.deleted = usuario.isDeleted();
        this.createdAt = usuario.getCreatedAt();
        this.updatedAt = usuario.getUpdatedAt();
        this.deletedAt = usuario.getDeletedAt();
        this.images = usuario.getImages().stream()
                .map(ImageDTO::new)
                .collect(Collectors.toList());
    }

}
