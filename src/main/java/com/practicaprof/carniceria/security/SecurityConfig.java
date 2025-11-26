package com.practicaprof.carniceria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, CustomSuccessHandler customSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", 
                                "/registrarUsuario", 
                                "/img/**",
                                "/images/**",
                                "/uploads/**",
                                "/static/**",
                                "/catalogo", 
                                "/cliente/catalogo",
                                "/registrarCliente").permitAll() // rutas públicas
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login") // podés definir tu propio controlador/vista
//                .defaultSuccessUrl("/index", true)
                .successHandler(customSuccessHandler)
                .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
}
