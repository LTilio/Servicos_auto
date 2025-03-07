package br.com.servicos_auto.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.servicos_auto.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
}
