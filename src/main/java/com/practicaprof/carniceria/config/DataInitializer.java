package com.practicaprof.carniceria.config;

import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository) {
        return args -> {

            // Verificamos si ya existe el usuario "Consumidor Final"
            boolean existeConsumidorFinal = usuarioRepository
                    .findByUsername("Consumidor Final")
                    .isPresent();

            if (!existeConsumidorFinal) {
                Usuario consumidorFinal = new Usuario();
                consumidorFinal.setUsername("Consumidor Final");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                consumidorFinal.setPassword(encoder.encode("N/A"));
                consumidorFinal.setEmail("consumidor@final.com");
                consumidorFinal.setTelefono("-");
                consumidorFinal.setRol("CLIENTE");
                consumidorFinal.setEstado(true);

                usuarioRepository.save(consumidorFinal);
                System.out.println("✅ Usuario 'Consumidor Final' creado automáticamente.");
            } else {
                System.out.println("ℹ️ Usuario 'Consumidor Final' ya existe, no se recrea.");
            }
        };
    }
}
