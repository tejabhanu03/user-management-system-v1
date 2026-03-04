package com.use_management_system.user_management.config;

import com.use_management_system.user_management.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Example security configuration that downstream Spring Boot services can copy or extract
 * into a shared module. It validates JWTs issued by the user-management service and exposes
 * authorities based on roles and permissions.
 *
 * NOTE: This class is intentionally NOT annotated with @Configuration because this project
 * already has an active SecurityConfig. Keeping both active would register two any-request
 * filter chains and fail startup.
 */
public class UserContextSecurityConfigTemplate {

    @Bean
    public SecurityFilterChain userContextSecurityFilterChain(HttpSecurity http,
                                                              JwtTokenUtil jwtTokenUtil) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new DownstreamJwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    static class DownstreamJwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenUtil jwtTokenUtil;

        DownstreamJwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
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
                    var userContext = jwtTokenUtil.parseToken(token);

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
