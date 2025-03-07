package br.com.servicos_auto.services;

import br.com.servicos_auto.models.Role;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.models.UsuarioDTO;
import br.com.servicos_auto.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> findAll() {
        logger.info("Buscando todos os usuarios");
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    public UsuarioDTO findById(Long id) {
        logger.info("Buscando usuario com ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("usuarios com ID {} nao encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "usuarios nao encontrado");
                });

        return new UsuarioDTO(usuario); // Retorna o DTO
    }

    @Transactional
    public Usuario create(Usuario usuario) {
        logger.info("Criando novo usuario: {}", usuario.getEmail());

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            logger.error("Email ja cadastrado: {}", usuario.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado");
        }

        // Verifica se o CPF já está cadastrado
        if (usuarioRepository.findByCpf(usuario.getCpf()).isPresent()) {
            logger.error("CPF ja cadastrado: {}", usuario.getCpf());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF ja cadastrado");
        }

        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Atribui a role padrão se nenhuma role for fornecida
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.addRole(Role.USUARIO);
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        logger.info("usuario criado com sucesso: {}", savedUsuario.getId());
        return savedUsuario;
    }

    @Transactional
    public Usuario update(Long id, Usuario usuarioDetails) {
        logger.info("Atualizando usuario com ID: {}", id);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        // Verifica se o usuário foi encontrado
        Usuario usuario = usuarioOpt
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado"));

        // Verifica se o usuário está deletado
        if (usuario.isDeleted()) {
            logger.error("Usuario com ID {} está deletado e não pode ser atualizado", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario deletado não pode ser atualizado");
        }

        // Atualiza apenas os campos que foram fornecidos no request
        if (usuarioDetails.getNome() != null) {
            usuario.setNome(usuarioDetails.getNome());
        }

        if (usuarioDetails.getEmail() != null) {
            usuario.setEmail(usuarioDetails.getEmail());
        }

        if (usuarioDetails.getCpf() != null) {
            usuario.setCpf(usuarioDetails.getCpf());
        }

        // Atualiza a senha apenas se uma nova senha for fornecida
        if (usuarioDetails.getSenha() != null && !usuarioDetails.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuarioDetails.getSenha()));
        }

        // Atualiza as roles apenas se forem fornecidas
        if (usuarioDetails.getRoles() != null && !usuarioDetails.getRoles().isEmpty()) {
            usuario.setRoles(usuarioDetails.getRoles());
        }

        // Salva as alterações no banco de dados
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        logger.info("Usuario atualizado com sucesso: {}", id);
        return updatedUsuario;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deletando usuario com ID: {}", id);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        Usuario usuario = usuarioOpt.get();

        // Verifica se o usuário já está deletado
        if (usuario.isDeleted()) {
            logger.error("Usuario com ID {} ja esta deletado", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ja deletado");
        }

        // Realiza o soft delete
        usuario.delete();
        usuarioRepository.save(usuario);
        logger.info("Usuario deletado com sucesso: {}", id);
    }

}
