package br.com.servicos_auto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.servicos_auto.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
