package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {
    
    @Autowired
    private UsuarioService servicio;
    
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }
    
    //Mostrar el formulario
    @GetMapping("/registrarUsuario") 
    public String getRegistrarUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registrarUsuario";
    }
    
    //Procesar el formulario
    @PostMapping("/registrarUsuario")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        servicio.registrar(usuario.getUsername(), usuario.getPassword(), usuario.getEmail(), usuario.getTelefono(), usuario.getRol());
        return "redirect:/login"; // después de registrarse lo mando al login
    }
}
