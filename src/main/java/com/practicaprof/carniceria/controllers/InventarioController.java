package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.InventarioService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/inventarios")
public class InventarioController {

    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public InventarioController(ProductoService productoService, InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }
    
    @GetMapping
    public String listar(Model model) {
        List<Inventario> listaInventarios = inventarioService.listarTodos();
        model.addAttribute("inventarios", listaInventarios);
        return "/inventarios/inventarios";
    }

    @GetMapping("/registrarInventario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "/inventarios/registrarInventario";
    }

    @PostMapping
    public String registrarInventario(
            @RequestParam("productoId") List<Integer> productoIds,
            @RequestParam("stockRelevado") List<Double> stockRelevados) {

        inventarioService.registrarInventario(productoIds, stockRelevados);

        return "redirect:/inventarios";
    }
}
