package br.com.servicos_auto.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;
import br.com.servicos_auto.repositories.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrestadorServicoRepository prestadorServicoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tenta carregar um usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario != null) {
            return new org.springframework.security.core.userdetails.User(
                    usuario.getEmail(),
                    usuario.getSenha(),
                    usuario.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.name()))
                            .collect(Collectors.toSet()));
        }

        // Se não encontrar um usuário, tenta carregar um prestador de serviço pelo email
        PrestadorServico prestadorServico = prestadorServicoRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário ou prestador de serviço não encontrado com o email: " + email));

        return new org.springframework.security.core.userdetails.User(
                prestadorServico.getEmail(),
                prestadorServico.getSenha(),
                prestadorServico.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toSet()));
    }
}
