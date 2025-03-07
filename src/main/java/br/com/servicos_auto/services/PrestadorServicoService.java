package br.com.servicos_auto.services;

import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.PrestadorServicoDTO;
import br.com.servicos_auto.models.Role;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrestadorServicoService {

    private static final Logger logger = LoggerFactory.getLogger(PrestadorServicoService.class);

    @Autowired
    private PrestadorServicoRepository prestadorServicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<PrestadorServicoDTO> findall() {
        logger.info("Buscando todos os prestadores");
        return prestadorServicoRepository.findAll().stream()
                .map(PrestadorServicoDTO::new)
                .collect(Collectors.toList());
    }

    public PrestadorServicoDTO findById(Long id) {
        logger.info("buscando prestador com o ID: ", id);
        PrestadorServico prestadorServico = prestadorServicoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Prestador com ID {} nao encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador nao encontrado");
                });
        return new PrestadorServicoDTO(prestadorServico);
    }

    @Transactional
    public PrestadorServicoDTO create(PrestadorServico prestadorServico) {
        logger.info("Criando novo prestador: ", prestadorServico.getEmail());

        // Verifica se o email já está cadastrado
        if (prestadorServicoRepository.findByEmail(prestadorServico.getEmail()).isPresent()) {
            logger.error("Email já cadastrado: {}", prestadorServico.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        // Verifica se o CNPJ já está cadastrado (apenas se o CNPJ não for nulo)
        if (prestadorServico.getCnpj() != null
                && prestadorServicoRepository.findByCnpj(prestadorServico.getCnpj()).isPresent()) {
            logger.error("CNPJ já cadastrado: {}", prestadorServico.getCnpj());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado");
        }

        // Verifica se o CPF já está cadastrado (apenas se o CPF não for nulo)
        if (prestadorServico.getCpf() != null
                && prestadorServicoRepository.findByCpf(prestadorServico.getCpf()).isPresent()) {
            logger.error("CPF já cadastrado: {}", prestadorServico.getCpf());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        if (prestadorServico.getRoles() == null || prestadorServico.getRoles().isEmpty()) {
            prestadorServico.addRole(Role.PRESTADOR_SERVICO);
        }

        prestadorServico.setSenha(passwordEncoder.encode(prestadorServico.getSenha()));

        PrestadorServico prestadorServicoSaved = prestadorServicoRepository.save(prestadorServico);
        logger.info("Prestador criado com sucesso: ", prestadorServicoSaved.getId());
        return new PrestadorServicoDTO(prestadorServicoSaved);

    }

    @Transactional
    public PrestadorServicoDTO update(Long id, PrestadorServico prestadorServicoDetails) {
        logger.info("Atualizando prestador com o id: {}", id);

        PrestadorServico prestadorServico = prestadorServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador não encontrado"));

        if (prestadorServico.isDeleted()) {
            logger.error("Prestador com o ID {} está deletado e não pode ser atualizado", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prestador deletado");
        }

        if (prestadorServicoDetails.getNome() != null) {
            prestadorServico.setNome(prestadorServicoDetails.getNome());
        }

        if (prestadorServicoDetails.getEmail() != null) {
            prestadorServico.setEmail(prestadorServicoDetails.getEmail());
        }

        if (prestadorServicoDetails.getCpf() != null) {
            prestadorServico.setCpf(prestadorServicoDetails.getCpf());
        }

        if (prestadorServicoDetails.getCnpj() != null) {
            prestadorServico.setCnpj(prestadorServicoDetails.getCnpj());
        }

        if (prestadorServicoDetails.getSenha() != null) {
            prestadorServico.setSenha(prestadorServicoDetails.getSenha());
        }

        if (prestadorServicoDetails.getRoles() != null && !prestadorServicoDetails.getRoles().isEmpty()) {
            prestadorServico.setRoles(prestadorServicoDetails.getRoles());
        }

        prestadorServicoRepository.save(prestadorServico);

        logger.info("Prestador com ID {} atualizado com sucesso", id);
        return new PrestadorServicoDTO(prestadorServico);
    }

    public void delete(Long id) {
        logger.info("Deletando prestado com ID {}", id);
        Optional<PrestadorServico> prestadorOpt = prestadorServicoRepository.findById(id);
        PrestadorServico prestadorServico = prestadorOpt.get();

        if (prestadorServico.isDeleted()) {
            logger.error("Prestador com ID {} ja esta deletado", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prestador ja deletado");
        }

        prestadorServico.delete();
        prestadorServicoRepository.save(prestadorServico);
        logger.info("Prestador deletado com sucesso: {}", id);

    }

}