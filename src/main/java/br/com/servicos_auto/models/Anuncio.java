package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do anúncio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    @Schema(description = "Título do anúncio", example = "Troca de óleo e filtro")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres")
    @Schema(description = "Descrição do anúncio", example = "Serviço completo de troca de óleo e filtro para todos os modelos de carros.")
    private String descricao;

    @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "prestador_servico_id", nullable = false)
    @JsonIgnore
    @Schema(hidden = true)
    private PrestadorServico prestadorServico;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(hidden = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(hidden = true)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @Schema(hidden = true)
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Método para soft delete
    @Schema(hidden = true)
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // Método para verificar se o prestador está deletado
    @Schema(hidden = true)
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

}