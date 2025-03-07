package br.com.servicos_auto.services;

import br.com.servicos_auto.models.Anuncio;
import br.com.servicos_auto.models.AnuncioDTO;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.repositories.AnuncioRepository;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnuncioService {

    private static final Logger logger = LoggerFactory.getLogger(AnuncioService.class);

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private PrestadorServicoRepository prestadorServicoRepository;

    public List<AnuncioDTO> findAll() {
        logger.info("Buscando anuncios");
        return anuncioRepository.findAll().stream()
                .map(AnuncioDTO::new)
                .collect(Collectors.toList());
    }

    public AnuncioDTO findById(Long id) {
        logger.info("Buscando anuncio com ID: {}", id);
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Anuncio com o ID {} nao encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado");
                });
        return new AnuncioDTO(anuncio);
    }

    @Transactional
    public AnuncioDTO create(Anuncio anuncio, Long prestadorId) {

        logger.info("Criando novo anuncio: {}", anuncio.getTitulo());

        PrestadorServico prestadorServico = prestadorServicoRepository.findById(prestadorId)
                .orElseThrow(() -> {
                    logger.error("Prestador não encontrado {}", prestadorId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador nao encontrado");
                });
        

        anuncio.setPrestadorServico(prestadorServico);
        Anuncio savedAnuncio = anuncioRepository.save(anuncio);
        logger.info("Anuncio criado com sucesso: {}", savedAnuncio.getId());

        return new AnuncioDTO(savedAnuncio);

    }

    @Transactional
    public AnuncioDTO update(Long id, Anuncio anuncioDetails) {

        logger.info("Atualizando anuncio com ID: {}", id);

        Anuncio existingAnuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Anuncio com ID {} nao encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado");
                });
        ;

        if (anuncioDetails.getTitulo() != null) {
            existingAnuncio.setTitulo(anuncioDetails.getTitulo());
        }
        if (anuncioDetails.getDescricao() != null) {
            existingAnuncio.setDescricao(anuncioDetails.getDescricao());
        }
        Anuncio updatedAnuncio = anuncioRepository.save(existingAnuncio);
        logger.info("Anuncio atualizado com sucesso. ID: {}", updatedAnuncio.getId());

        return new AnuncioDTO(updatedAnuncio);

    }

    public void delete(Long id) {
        logger.info("Deletando anuncio com ID: {}", id);

        Optional<Anuncio> anuncioOpt = anuncioRepository.findById(id);
        Anuncio anuncio = anuncioOpt.get();

        if (anuncio.isDeleted()) {
            logger.error("Anuncio com ID {} ja esta deletado", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anuncio ja deletado");
        }
        anuncio.delete();
        anuncioRepository.save(anuncio);

    }

}