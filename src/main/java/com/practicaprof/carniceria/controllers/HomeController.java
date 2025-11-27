package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.services.InventarioService;
import com.practicaprof.carniceria.services.ProductoInventarioService;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.VentaDetalleService;
import com.practicaprof.carniceria.services.VentaService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private InventarioService inventarioServicio;

    @Autowired
    private ProductoService productoServicio;

    @GetMapping("/index")
    public String index(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//        String productoMasVendido = ventaDetalleServicio.obtenerProductoMasVendido();
        Inventario ultimoInventarioObj = inventarioServicio.obtenerUltimoInventario();
        String ultimoInventario = (ultimoInventarioObj != null) ? ultimoInventarioObj.getFecha().format(formatter) : "Sin inventario";
        String productoMenorStock = productoServicio.obtenerProductoConMenorStock();
        double gananciasDelDia = ventaServicio.obtenerGananciasDelDia();
        String empleadoMasVentas = ventaServicio.obtenerEmpleadoConMasVentasUltimoMes();

        //Top 5 para el gráfico
        List<Object[]> top5 = ventaDetalleServicio.obtenerTop5ProductosMasVendidos();

        List<String> nombres = new ArrayList<>();
        List<Double> cantidades = new ArrayList<>();

        for (Object[] fila : top5) {
            nombres.add((String) fila[0]);
            cantidades.add((Double) fila[1]);
        }

        Map<String, Long> ventasDias = ventaServicio.ventasPorDiaSemana();

        model.addAttribute("nombresProductos", nombres);
        model.addAttribute("cantidadesProductos", cantidades);

        model.addAttribute("diasSemana", ventasDias.keySet());
        model.addAttribute("cantidadesDias", ventasDias.values());

//        model.addAttribute("productoMasVendido", productoMasVendido);
        model.addAttribute("ultimoInventario", ultimoInventario);
        model.addAttribute("productoConMenorStock", productoMenorStock);
        model.addAttribute("gananciasDelDia", gananciasDelDia);
        model.addAttribute("empleadoMasVentas", empleadoMasVentas);
        return "index";
    }

    @GetMapping("cliente/indexCliente")
    public String inicioCliente() {
        return "cliente/indexCliente";
    }
}
