package br.com.servicos_auto.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatorio")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email e obrigatório")
    @Email(message = "O email deve ser valido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O CPF e obrigatorio")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotBlank(message = "A senha e obrigatoria")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

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

    // Método para verificar se o usuário está deletado
    public boolean isDeleted() {
        return this.deletedAt != null;
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
        return this.senha; // Retorna a senha do usuário
    }

    public Set<Role> getRoles() {
        return this.roles; // Retorna as roles do usuário
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role); // Verifica se o usuário tem uma role específica
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role); // Remove uma role do usuário
    }

}
