package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.UsuarioService;
import com.practicaprof.carniceria.services.VentaService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/compras")
    public String mostrarCompras(Model model) {
        model.addAttribute("productos", productoService.listarDisponiblesParaVenta());
        model.addAttribute("venta", new Venta());
        return "cliente/compras";
    }

//    @PostMapping("/compras/registrar")
//    public String registrarCompra(@ModelAttribute Venta venta,
//                                  Authentication authentication,
//                                  Model model) {
//
//        // obtener el username del usuario autenticado
//        String username = authentication.getName();
//        // Buscar el usuario completo desde la base de datos
//        Usuario usuario = usuarioService.buscarPorUsername(username).get();
//
//        venta.setUsuario(usuario);
//        // ✅ Validar y registrar usando el mismo servicio
//        String error = ventaService.registrarVenta(venta);
//
//        if (error != null) {
//            model.addAttribute("error", error);
//            model.addAttribute("venta", venta);
//            model.addAttribute("productos", productoService.listarDisponiblesParaVenta());
//            return "cliente/compras";
//        }
//
//        return "redirect:/cliente/indexCliente?compraExitosa";
//    }
    @PostMapping("/compras/registrar")
    public ResponseEntity<?> registrarCompra(@RequestBody Venta venta, Authentication authentication) {
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username).get();

            // Asocia el usuario autenticado
            venta.setUsuario(usuario);

            // Usa tu mismo servicio de registrar venta
            String error = ventaService.registrarVenta(venta);
            if (error != null) {
                return ResponseEntity.badRequest().body(error);
            }

            return ResponseEntity.ok("Compra registrada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar compra: " + e.getMessage());
        }
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "cliente/contacto";
    }

}
