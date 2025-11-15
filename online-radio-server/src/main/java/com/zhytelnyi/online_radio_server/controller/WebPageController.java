package com.zhytelnyi.online_radio_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class WebPageController {

    @GetMapping("/")
    public Mono<String> index() {
        return Mono.just("index");    }
}
