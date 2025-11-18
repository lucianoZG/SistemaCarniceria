package com.practicaprof.carniceria.controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.entities.VentaDetalle;
import com.practicaprof.carniceria.services.ProductoInventarioService;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.VentaDetalleService;
import com.practicaprof.carniceria.services.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private VentaDetalleService ventaDetalleServicio;

    public InformeController(ProductoService productoServicio, ProductoInventarioService productoInventarioServicio, VentaService ventaServicio, VentaDetalleService ventaDetalleServicio) {
        this.productoServicio = productoServicio;
        this.productoInventarioServicio = productoInventarioServicio;
        this.ventaServicio = ventaServicio;
        this.ventaDetalleServicio = ventaDetalleServicio;
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

    @GetMapping("/ganancias")
    public String informeGanancias(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // Siempre cargar los productos
        model.addAttribute("productos", productoServicio.listarProductosUltimoInventario());

        // GANANCIA POR PRODUCTO  (si productoId != null)
        if (productoId != null) {

            Producto producto = productoServicio.obtenerPorId(productoId);
            model.addAttribute("productoSeleccionado", producto);

            List<VentaDetalle> detalles = ventaDetalleServicio.findByProductoId(productoId);
            model.addAttribute("detalles", detalles);

            double gananciaTotal = detalles.stream()
                    .mapToDouble(det -> det.getPrecioTotalProducto()
                    - (det.getTotalCantidad() * det.getPrecioCostoActual()))
                    .sum();

            model.addAttribute("gananciaTotal", gananciaTotal);
        }

        // GANANCIA POR PERÍODO (si ambas fechas != null)
        if (fechaInicio != null && fechaFin != null) {

            LocalDateTime desde = fechaInicio.atStartOfDay();
            LocalDateTime hasta = fechaFin.atTime(23, 59, 59);

            if (hasta.isBefore(desde)) {
                model.addAttribute("errorPeriodo", "La fecha final no puede ser menor que la inicial.");
                return "informes/ganancias";
            }

            List<Venta> ventasPeriodo = ventaServicio.findByFechaHoraBetween(desde, hasta);

            Map<Integer, Double> gananciaPorVenta = new HashMap<>();
            double gananciaPeriodo = 0;

            for (Venta v : ventasPeriodo) {
                double ganancia = v.getListaVentaDetalle().stream()
                        .mapToDouble(det -> det.getPrecioTotalProducto()
                        - (det.getTotalCantidad() * det.getPrecioCostoActual()))
                        .sum();

                gananciaPorVenta.put(v.getId(), ganancia);
                gananciaPeriodo += ganancia;
            }

//            gananciaPeriodo = ventasPeriodo.stream()
//                    .flatMap(v -> v.getListaVentaDetalle().stream())
//                    .mapToDouble(det -> det.getPrecioTotalProducto()
//                    - (det.getTotalCantidad() * det.getPrecioCostoActual()))
//                    .sum();
            model.addAttribute("ventasPeriodo", ventasPeriodo);
            model.addAttribute("gananciaPeriodo", gananciaPeriodo);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("gananciaPorVenta", gananciaPorVenta);
        }

        return "informes/ganancias";
    }

    @GetMapping("/ganancias/pdf")
    public void generarPdfGanancias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletResponse response) throws IOException {

        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.atTime(23, 59, 59);

        // Formato deseado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Suponiendo que tu fecha es un LocalDateTime...
        String fechaInicioFormateada = fechaInicio.atStartOfDay().format(formatter);
        String fechaFinFormateada = fechaFin.atTime(23, 59, 59).format(formatter);

        List<Venta> ventasPeriodo = ventaServicio.findByFechaHoraBetween(desde, hasta);

        // Sumar ganancia
        double gananciaTotal = 0;

        // Configurar cabecera del PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=ganancias_" + fechaInicioFormateada + "_a_" + fechaFin + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        document.add(new Paragraph("Informe de Ganancias por Período")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(
                "Período: " + fechaInicioFormateada + " al " + fechaFinFormateada
        ).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Tabla PDF
        Table table = new Table(new float[]{2, 3, 3, 3, 3});
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell("Empleado");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Nro Factura");
        table.addHeaderCell("Precio Total");
        table.addHeaderCell("Ganancia");

        for (Venta v : ventasPeriodo) {

            double gananciaVenta = v.getListaVentaDetalle().stream()
                    .mapToDouble(det
                            -> det.getPrecioTotalProducto()
                    - det.getTotalCantidad() * det.getPrecioCostoActual())
                    .sum();

            gananciaTotal += gananciaVenta;

            table.addCell(v.getEmpleado() != null ? v.getEmpleado().getNombre() : "-");
            LocalDateTime fechaHora = v.getFechaHora();
            String fechaHoraFormateada = fechaHora.format(formatter);
            table.addCell(fechaHoraFormateada);
            table.addCell(v.getNroFactura());
            table.addCell("$ " + v.getPrecioTotal());
            table.addCell("$ " + gananciaVenta);
        }

        document.add(table);

        document.add(new Paragraph("\n"));

        // Total final
        document.add(new Paragraph(
                "Ganancia Total del Período: $" + gananciaTotal
        ).setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));

        document.close();
    }

}
