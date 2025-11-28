package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.services.UsuarioService;
import com.practicaprof.carniceria.services.VentaDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService servicio;
//    @Autowired
//    private VentaDetalleService vdServicio;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Usuario o contraseña incorrectos");
        }

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
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {
        // 1. VALIDACIÓN: Verificamos si el username ya existe
        if (servicio.existeUsuario(usuario.getUsername())) {
            // "username" es el nombre del campo en tu clase Java
            result.rejectValue("username", "error.usuario", "¡Este nombre de usuario ya está en uso, elige otro!");
        }

        // 2. Si hay errores, volvemos al formulario (NO hacemos redirect)
        if (result.hasErrors()) {
            return "registrarUsuario"; // Tu vista HTML de registro
        }

        // --- Si pasa, guardamos ---
        servicio.registrar(usuario.getUsername(), usuario.getPassword(), usuario.getEmail(), usuario.getTelefono(), usuario.getRol());
        return "redirect:/login";
    }

    //Mostrar el formulario
    @GetMapping("/registrarCliente")
    public String getRegistrarCliente(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registrarCliente";
    }

    //Procesar el formulario
    @PostMapping("/registrarCliente")
    public String registrarCliente(@ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {
        // 1. VALIDACIÓN: Verificamos si el username ya existe
        if (servicio.existeUsuario(usuario.getUsername())) {
            // "username" es el nombre del campo en tu clase Java
            result.rejectValue("username", "error.usuario", "¡Este nombre de usuario ya está en uso, elige otro!");
        }

        // 2. Si hay errores, volvemos al formulario (NO hacemos redirect)
        if (result.hasErrors()) {
            return "registrarCliente"; // Tu vista HTML de registro
        }
        
        
        servicio.registrarCliente(usuario.getUsername(), usuario.getPassword(), usuario.getEmail(), usuario.getTelefono());
        return "redirect:/login"; // después de registrarse lo mando al login
    }

//    @GetMapping("/index")
//    public String getIndex(Model model) {
//        String productoMasVendido = vdServicio.obtenerProductoMasVendido();
//        model.addAttribute("productoMasVendido", productoMasVendido);
//        return "index";
//    }
}
