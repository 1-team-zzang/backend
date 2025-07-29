package com.example.calpick.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "https://calpick.vercel.app/",
                                "http://localhost:5173",
                                "https://localhost:5173",
                                "https://calpick.shop")
                        .allowedMethods("*")
                        .allowCredentials(true) // 쿠키 허용
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Set-Cookie");
            }
        };
    }
}
