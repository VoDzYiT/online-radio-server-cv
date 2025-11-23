package com.zhytelnyi.online_radio_server.controller;

import com.zhytelnyi.online_radio_server.model.User;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.repository.UserRepository;
import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
public class WebPageController {

    @Autowired private StationRepository stationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RadioFacade radioFacade;

    @GetMapping("/")
    public Mono<String> index(Model model) {
        // 1. Дані радіостанцій
        model.addAttribute("stations", stationRepository.findAll());

        // 2. Ручна перевірка безпеки (найнадійніший спосіб для WebFlux+Thymeleaf)
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> {
                    if (auth != null && auth.isAuthenticated()) {
                        model.addAttribute("isLoggedIn", true);
                        model.addAttribute("username", auth.getName());

                        boolean isAdmin = auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                        model.addAttribute("isAdmin", isAdmin);
                    }
                })
                // Навіть якщо контекст порожній, повертаємо сторінку
                .thenReturn("index");
    }

    @GetMapping("/login")
    public Mono<String> login() {
        return Mono.just("login");
    }

    @GetMapping("/register")
    public Mono<String> register() {
        return Mono.just("register");
    }

    // Обробка реєстрації
    @PostMapping("/register")
    public Mono<String> registerUser(@ModelAttribute User user) {
        return Mono.fromCallable(() -> {
            // Перевірка чи існує
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return "redirect:/register?error";
            }

            // Створення нового
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_USER");
            userRepository.save(user);

            return "redirect:/login";
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/favorites")
    public Mono<String> favorites(Model model) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    if (auth != null && auth.isAuthenticated()) {
                        String username = auth.getName();
                        model.addAttribute("username", username);
                        model.addAttribute("isLoggedIn", true);

                        // Викликаємо Фасад, щоб отримати список станцій
                        return radioFacade.getUserFavoriteStations(username)
                                .doOnNext(stations -> model.addAttribute("favoriteStations", stations))
                                .thenReturn("favorites"); // Повертаємо favorites.html
                    }
                    return Mono.just("redirect:/login"); // Якщо не залогінений (хоча Security це перехопить)
                });
    }
}