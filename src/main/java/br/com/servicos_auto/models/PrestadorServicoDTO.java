package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


import lombok.Data;

@Data
public class PrestadorServicoDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String cnpj;
    private List<String> roles;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<ImageDTO> images;
    
    private List<AnuncioDTO> anuncio;

    public PrestadorServicoDTO(PrestadorServico prestadorServico) {
        this.id = prestadorServico.getId();
        this.nome = prestadorServico.getNome();
        this.email = prestadorServico.getEmail();
        this.cpf = prestadorServico.getCpf();
        this.cnpj = prestadorServico.getCnpj();
        this.roles = prestadorServico.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        this.deleted = prestadorServico.isDeleted();
        this.createdAt = prestadorServico.getCreatedAt();
        this.updatedAt = prestadorServico.getUpdatedAt();
        this.deletedAt = prestadorServico.getDeletedAt();
        this.images = prestadorServico.getImages().stream()
                .map(ImageDTO::new)
                .collect(Collectors.toList());
        this.anuncio = prestadorServico.getAnuncios().stream()
                .map(AnuncioDTO::new).collect(Collectors.toList());
    }

}
