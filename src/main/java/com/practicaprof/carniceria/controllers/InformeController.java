package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.services.ProductoInventarioService;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.VentaService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/informes")
public class InformeController {

    private ProductoService productoServicio;
    private ProductoInventarioService productoInventarioServicio;
    private VentaService ventaServicio;

    public InformeController(ProductoService productoServicio, ProductoInventarioService productoInventarioServicio, VentaService ventaServicio) {
        this.productoServicio = productoServicio;
        this.productoInventarioServicio = productoInventarioServicio;
        this.ventaServicio = ventaServicio;
    }

    @GetMapping("/stock")
    public String informeStock(Model model) {
//        List<Producto> productos = productoServicio.listarActivos();
        ////        double totalStock = productos.stream()
////                .mapToDouble(p -> p.getCantidad() * p.getPrecioUnitario())
////                .sum();
//
//        double totalStock = productoServicio.obtenerTotalStock(productos);

        List<ProductoInventario> lista = productoInventarioServicio.listarUltimoInventario();
        double total = productoServicio.obtenerTotalStock();

        model.addAttribute("productos", lista);
        model.addAttribute("totalStock", total);

        return "informes/stock";
    }

    @GetMapping("/consultaVentas")
    public String consultarVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        List<Venta> ventas;

        // Si no hay fechas, usar el día actual
        if (fechaInicio == null || fechaFin == null) {
            model.addAttribute("ventas", new ArrayList<>());
            return "informes/consultaVentas";
        }

        // Convertir LocalDate → LocalDateTime para la consulta
        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.atTime(23, 59, 59);

        // Validación lógica
        if (hasta.isBefore(desde)) {
            model.addAttribute("error", "La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
            model.addAttribute("ventas", new ArrayList<>()); // evita null
            return "informes/consultaVentas";
        }

        ventas = ventaServicio.findByFechaHoraBetween(desde, hasta);

        // ✅ Calcular el total general
        double totalGeneral = ventas.stream()
                .mapToDouble(Venta::getPrecioTotal)
                .sum();

        model.addAttribute("ventas", ventas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("totalGeneral", totalGeneral);
        return "informes/consultaVentas";
    }

}
