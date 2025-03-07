package br.com.servicos_auto.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A URL da imagem é obrigatória")
    private String url;

    @Column(name = "imgur_id")
    private String imgurId; // ID da imagem no Imgur

    @Column(name = "imgur_deletehash")
    private String imgurDeletehash; // Hash para deletar a imagem no Imgur

    @Column(name = "type")
    private String type; // Tipo da imagem (ex: "image/jpeg")

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt; // Data e hora do upload no Imgur

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "prestador_servico_id")
    private PrestadorServico prestadorServico;

    @ManyToOne
    @JoinColumn(name = "anuncio_id")
    private Anuncio anuncio;

}
