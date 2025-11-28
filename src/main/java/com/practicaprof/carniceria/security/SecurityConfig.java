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
                // 2. Rutas COMPARTIDAS (Cliente Y Admin)
                // Usamos hasAnyRole para permitir a cualquiera de los dos
                .requestMatchers("/ventas/**").hasAnyRole("CLIENTE", "ADMIN")
                // 2. Rutas EXCLUSIVAS de CLIENTE
                // Todo lo que empiece con /cliente/ solo lo puede ver un CLIENTE
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                // 3. Rutas EXCLUSIVAS de ADMIN (Aquí bloqueamos al cliente)
                // Debes listar todas las rutas de tu sistema de gestión
                .requestMatchers(
                        "/index", // El dashboard principal
                        "/productos/**", // ABM productos
//                        "/ventas/**", // Gestión ventas
                        "/inventarios/**", // Inventarios
                        "/empleados/**", // Gestión empleados
                        "/informes/**" // Reportes
                ).hasRole("ADMIN")
                // 4. El resto requiere estar logueado (Cualquier rol)
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login") // podés definir tu propio controlador/vista
                //                .defaultSuccessUrl("/index", true)
                .successHandler(customSuccessHandler)
                .failureUrl("/login?error=true") // <-- URL en caso de error
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
