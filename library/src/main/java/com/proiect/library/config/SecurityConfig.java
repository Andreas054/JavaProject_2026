package com.proiect.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/v2/api-docs"),
                                new AntPathRequestMatcher("/v3/api-docs"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/swagger-resources"),
                                new AntPathRequestMatcher("/swagger-resources/**"),
                                new AntPathRequestMatcher("/configuration/ui"),
                                new AntPathRequestMatcher("/configuration/security"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/webjars/**"),
                                new AntPathRequestMatcher("/swagger-ui.html"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/error/**")
                        ).permitAll()

                        // admin restrictions
                        .requestMatchers(HttpMethod.POST, "/api/books", "/api/authors", "/api/genres", "/api/readers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**", "/api/authors/**", "/api/genres/**", "/api/readers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**", "/api/authors/**", "/api/genres/**", "/api/readers/**").hasRole("ADMIN")
                        
                        .requestMatchers(new AntPathRequestMatcher("/view/**/add")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/view/**/edit/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/view/**/delete/**")).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
