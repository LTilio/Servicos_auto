package br.com.servicos_auto.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.servicos_auto.models.PrestadorServico;

public interface PrestadorServicoRepository extends JpaRepository<PrestadorServico, Long> {
    Optional<PrestadorServico> findByEmail(String email);

    Optional<PrestadorServico> findByCpf(String cpf);

    Optional<PrestadorServico> findByCnpj(String cnpj);
}
