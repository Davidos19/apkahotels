package org.example.apkahotels.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // na razie wyłączamy CSRF, by to nie przeszkadzało
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")              // GET /login — formularz
                        .loginProcessingUrl("/perform_login") // POST /perform_login — przetwarzanie
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .key("unikalnySekretnyKlucz")
                )
                .sessionManagement(sess -> sess
                        .maximumSessions(1)
                        // przy nowym logowaniu wygaśnij starą sesję (zamiast blokować login)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired")    // opcjonalnie: przekieruj, gdy sesja wygasła
                )
        ;
        return http.build();
    }


}