package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class PrestadorServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")
    @Column(unique = true)
    private String cpf;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @Size(min = 14, max = 14, message = "O CNPJ deve ter 14 caracteres")
    @Column(unique = true)
    private String cnpj;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "prestador_roles", joinColumns = @JoinColumn(name = "prestador_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "prestadorServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "prestadorServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anuncio> anuncios = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
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
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // Método para verificar se o prestador está deletado
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // Método de validação para CPF ou CNPJ
    @AssertTrue(message = "O prestador deve ter um CPF ou CNPJ, mas não ambos")
    public boolean isCpfOrCnpjValid() {
        boolean hasCpf = cpf != null && !cpf.trim().isEmpty();
        boolean hasCnpj = cnpj != null && !cnpj.trim().isEmpty();
        return (hasCpf || hasCnpj) && !(hasCpf && hasCnpj);
    }

    // Métodos para autenticação e autorização

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public String getUsername() {
        return this.email; // Usamos o email como username
    }

    public String getPassword() {
        return this.senha; // Retorna a senha do prestador
    }

    public Set<Role> getRoles() {
        return this.roles; // Retorna as roles do prestador
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role); // Verifica se o prestador tem uma role específica
    }

    public void addRole(Role role) {
        this.roles.add(role); // Adiciona uma role ao prestador
    }

    public void removeRole(Role role) {
        this.roles.remove(role); // Remove uma role do prestador
    }
}