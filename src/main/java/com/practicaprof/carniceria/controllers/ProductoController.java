/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.services.ProductoService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    private ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }
    
    //Listar empleados
    @GetMapping
    public String listar(Model model) {
        List<Producto> listaEmpleados = servicio.listarTodos();
        model.addAttribute("productos", listaEmpleados);
        return "/productos/productos";
    }
    
    //Registrar producto
    @GetMapping("/registrarProducto")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("producto", new Producto());
        return "/productos/registrarProducto"; // nombre de la vista Thymeleaf (productos/registrarProducto.html)
    }

    @PostMapping
    public String registrar(@ModelAttribute("producto") Producto producto) {
//        servicio.registrarProducto(producto.getDescripcion(), producto.getPrecioUnitario(), producto.getCantidad());
        servicio.registrarProducto(producto);
        return "redirect:/productos";
    }
    
    //Editar empleado
    @GetMapping("/editar/{id}")
    public String mostrarFormularioModificar(@PathVariable int id, Model model) {
        Producto producto = servicio.obtenerPorId(id);
        model.addAttribute("producto", producto);
        return "/productos/modificarProducto";
    }

    @PostMapping("/editar/{id}")
    public String modificar(@PathVariable int id, @ModelAttribute("producto") Producto producto) {
        servicio.editar(producto);
        return "redirect:/productos";
    }
    
    //Dar de baja producto
    @GetMapping("/baja/{id}")
    public String darDeBaja(@PathVariable int id) {
        servicio.eliminar(id);
        return "redirect:/productos";
    }
}
