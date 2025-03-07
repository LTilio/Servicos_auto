package br.com.servicos_auto.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.servicos_auto.models.Anuncio;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByPrestadorServicoId(Long prestadorServicoId);
}
