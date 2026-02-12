package com.workout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS設定：React(5173)からのアクセスを完全に許可
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(List.of("http://localhost:5173","https://*.onrender.com"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            // 2. CSRFを無効化：これがないとReactからのPOSTがエラーになります
            .csrf(csrf -> csrf.disable())
            // 3. 全リクエストを許可：これが「全開放」の肝です
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );

        return http.build();
    }
}