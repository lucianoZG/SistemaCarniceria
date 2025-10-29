package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.repositories.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    
    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(String username, String rawPassword, String email, String telefono, String rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword));// 🔑 encriptar
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setRol(rol);
        usuario.setEstado(true); //Usuario activo
        return repo.save(usuario);
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        return repo.findByUsername(username);
    }
    
    public void desactivarUsuario(Integer id) {
        repo.findById(id).ifPresent(usuario -> {
            usuario.setEstado(false);
            repo.save(usuario);
        });
    }

    public void activarUsuario(Integer id) {
        repo.findById(id).ifPresent(usuario -> {
            usuario.setEstado(true);
            repo.save(usuario);
        });
    }
    
    public List<Usuario> listarActivos() {
        return repo.listarActivos();
    }
    
    public Usuario obtenerPorId(int id) {
        Optional<Usuario> us = repo.findById(id);
        Usuario usuario = us.get();
        return usuario;
    }
}
