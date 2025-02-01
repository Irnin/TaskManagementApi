package com.project.employee_records.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Ustaw tutaj adres Angularowej aplikacji
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Dozwolone metody
                .allowedHeaders("*") // Dozwolone nagłówki
                .allowCredentials(true) // Pozwól na przekazywanie danych autoryzacji
                .maxAge(3600);
    }
}