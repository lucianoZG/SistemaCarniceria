package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.services.ProductoInventarioService;
import com.practicaprof.carniceria.services.VentaDetalleService;
import com.practicaprof.carniceria.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private VentaDetalleService ventaDetalleServicio;
    
    @Autowired
    private ProductoInventarioService productoInventarioServicio;
    
    @Autowired
    private VentaService ventaServicio;
    
    @GetMapping("/index")
    public String index(Model model) {
        String productoMasVendido = ventaDetalleServicio.obtenerProductoMasVendido();
        String productoMenorStock = productoInventarioServicio.obtenerProductoConMenorStock();
        double gananciasDelDia = ventaServicio.obtenerGananciasDelDia();
        
        model.addAttribute("productoMasVendido", productoMasVendido);
        model.addAttribute("productoConMenorStock", productoMenorStock);
        model.addAttribute("gananciasDelDia", gananciasDelDia);
        return "index";
    }
    
}
