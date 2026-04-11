package com.use_management_system.user_management.config;

import com.use_management_system.user_management.dto.UserContextDto;
import com.use_management_system.user_management.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:63747}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenUtil jwtTokenUtil) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/validate",
                                "/api/auth/verify-email",
                                "/api/auth/resend-verification",
                                "/api/users/register",
                                "/api/clients",
                                "/api/clients/**",
                                "/api/statistics/**",
                                "/api/health",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Simple JWT authentication filter that:
     * - Extracts Bearer token from Authorization header
     * - Parses it into UserContextDto
     * - Builds a Spring Security Authentication with roles and permissions as authorities
     */
    static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenUtil jwtTokenUtil;

        JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
            this.jwtTokenUtil = jwtTokenUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    UserContextDto userContext = jwtTokenUtil.parseToken(token);

                    List<? extends GrantedAuthority> authorities = Stream.concat(
                                    userContext.getRoles().stream()
                                            .map(role -> "ROLE_" + role),
                                    userContext.getPermissions().stream()
                                            .map(permission -> "PERM_" + permission)
                            )
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userContext,
                            null,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception ex) {
                    SecurityContextHolder.clearContext();
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
