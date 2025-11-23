package com.zhytelnyi.online_radio_server.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Кажемо: все, що починається на /hls/**, шукай у папці користувача
        String hlsPath = "file:" + System.getProperty("user.home") + "/radio-hls-static/hls/";
        registry.addResourceHandler("/hls/**")
                .addResourceLocations(hlsPath);
    }
}
