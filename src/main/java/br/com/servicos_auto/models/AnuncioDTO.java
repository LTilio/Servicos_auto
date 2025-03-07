package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class AnuncioDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private Long prestadorServicoId;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<ImageDTO> images;

    public AnuncioDTO(Anuncio anuncio) {
        this.id = anuncio.getId();
        this.titulo = anuncio.getTitulo();
        this.descricao = anuncio.getDescricao();
        this.deleted = anuncio.isDeleted();
        this.createdAt = anuncio.getCreatedAt();
        this.updatedAt = anuncio.getUpdatedAt();
        this.deletedAt = anuncio.getDeletedAt();
        this.prestadorServicoId = anuncio.getPrestadorServico().getId();
        this.images = anuncio.getImages().stream()
                .map(ImageDTO::new)
                .collect(Collectors.toList());
    }

}
