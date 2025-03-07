package br.com.servicos_auto.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.servicos_auto.services.CustomUserDetailsService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita o CORS
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (não necessário para APIs stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticação (públicos)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/docs", "/docs/**")
                        .permitAll()

                        // Cadastro de usuários e prestadores (público)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/prestadores").permitAll()

                        // Listagem de usuários, prestadores e anúncios (público)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prestadores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/anuncios/**").permitAll()

                        // Qualquer usuário autenticado pode atualizar/deletar usuário e prestador
                        .requestMatchers(HttpMethod.PATCH, "/api/usuarios/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/prestadores/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/prestadores/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/{usuarioId}/upload-image").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/prestadores/{prestadorId}/upload-image").authenticated()

                        // Somente PRESTADOR_SERVICO pode criar, atualizar, deletar anúncios e enviar
                        // imagens
                        .requestMatchers(HttpMethod.POST, "/api/anuncios/{prestadorId}")
                        .hasAuthority("PRESTADOR_SERVICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/anuncios/{id}").hasAuthority("PRESTADOR_SERVICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/anuncios/{id}").hasAuthority("PRESTADOR_SERVICO")
                        .requestMatchers(HttpMethod.POST, "/api/anuncios/{id}/upload-image")
                        .hasAuthority("PRESTADOR_SERVICO")

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtTokenFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usa BCrypt para codificar senhas
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permite todas as origens (ajuste conforme necessário)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração a todos os endpoints
        return source;
    }

}
