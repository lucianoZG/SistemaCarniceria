/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Empleado;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.entities.VentaDetalle;
import com.practicaprof.carniceria.services.EmpleadoService;
import com.practicaprof.carniceria.services.ProductoInventarioService;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.UsuarioService;
import com.practicaprof.carniceria.services.VentaService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private VentaService ventaServicio;
    private EmpleadoService empleadoServicio;
    private ProductoService productoServicio;
    private UsuarioService usuarioServicio;
    private ProductoInventarioService productoInventarioServicio;

    public VentaController(VentaService ventaServicio, EmpleadoService empleadoServicio, ProductoService productoServicio, UsuarioService usuarioServicio, ProductoInventarioService productoInventarioServicio) {
        this.ventaServicio = ventaServicio;
        this.empleadoServicio = empleadoServicio;
        this.productoServicio = productoServicio;
        this.usuarioServicio = usuarioServicio;
        this.productoInventarioServicio = productoInventarioServicio;
    }

    //Listar ventas
    @GetMapping
    public String listar(Model model) {
        List<Venta> listaVentas = ventaServicio.listarVentas();
        model.addAttribute("ventas", listaVentas);
        return "/ventas/ventas";
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("empleados", empleadoServicio.listarActivos());
        model.addAttribute("usuarios", usuarioServicio.listarClientesActivos());
        model.addAttribute("productos", productoServicio.listarDisponiblesParaVenta());
        return "ventas/registrarVenta";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("venta") Venta venta, Model model) {
        // Buscar el empleado desde la BD, según su ID
        Empleado empleado = empleadoServicio.obtenerPorId(venta.getEmpleado().getId());
        venta.setEmpleado(empleado);

        Usuario usuario = usuarioServicio.obtenerPorId(venta.getUsuario().getId());
        venta.setUsuario(usuario);

        // Validar y registrar
        String error = ventaServicio.registrarVenta(venta);

        if (error != null) {
            // Si hay error, volvemos al formulario con mensaje
            model.addAttribute("error", error);
            model.addAttribute("venta", venta);
            model.addAttribute("empleados", empleadoServicio.listarActivos());
            model.addAttribute("usuarios", usuarioServicio.listarActivos());
            model.addAttribute("productos", productoServicio.listarDisponiblesParaVenta());
            return "ventas/registrarVenta";
        }

//        ventaServicio.registrarVenta(venta);
        return "redirect:/ventas";
    }

    // Detalle de una venta específica
    @GetMapping("/detalle/{id}")
    public String detalleVenta(@PathVariable("id") int id, Model model) {
        Venta venta = ventaServicio.obtenerPorId(id);

        if (venta == null) {
            // Si no existe, redirige al listado con un mensaje de error
            return "redirect:/ventas?error=VentaNoEncontrada";
        }

        model.addAttribute("venta", venta);
        return "ventas/ventaDetalle";
    }

    //Verificación para que al tocar en el botón de Agregar Producto se muestre si es hay algún error
    @GetMapping("/verificarStock")
    @ResponseBody
    public ResponseEntity<String> verificarStock(
            @RequestParam("productoId") int productoId,
            @RequestParam("cantidad") double cantidad) {

        Optional<ProductoInventario> optionalPI = productoInventarioServicio.findByProductoIdDelUltimoInventario(productoId);

        if (optionalPI.isEmpty()) {
            return ResponseEntity.badRequest().body("El producto no está en el último inventario o está inactivo.");
        }

//        ProductoInventario pi = optionalPI.get();

        Producto producto = productoServicio.obtenerPorId(productoId);

        if (cantidad > producto.getStock()) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + producto.getStock());
        }

        return ResponseEntity.ok("OK");
    }

}
