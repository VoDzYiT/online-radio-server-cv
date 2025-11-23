package com.zhytelnyi.online_radio_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        // Налаштовуємо, куди йти після виходу
        RedirectServerLogoutSuccessHandler logoutHandler = new RedirectServerLogoutSuccessHandler();
        logoutHandler.setLogoutSuccessUrl(URI.create("/")); // На головну

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Вимикаємо CSRF
                // 1. Правила доступу
                .authorizeExchange(auth -> auth
                        .pathMatchers("/", "/register", "/login", "/css/**", "/js/**", "/hls/**").permitAll() // Публічне
                        .pathMatchers("/admin/**").hasRole("ADMIN") // Тільки для адміна
                        .anyExchange().authenticated() // Решта - тільки для залогінених
                )
                // 2. Налаштування форми входу
                .formLogin(form -> form
                                .loginPage("/login") // Наша кастомна сторінка
                        // Якщо успішно - редірект (автоматично на ту сторінку, куди хотів, або на /)
                )
                // 3. Налаштування виходу
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutHandler)
                )
                // 4. Зберігання сесії
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}