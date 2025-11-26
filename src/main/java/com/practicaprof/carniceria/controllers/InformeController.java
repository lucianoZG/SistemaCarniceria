package com.practicaprof.carniceria.controllers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
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
    public String informeStock(@RequestParam(required = false) String busqueda,
            Model model) {

        List<Producto> lista;

        if (busqueda != null && !busqueda.trim().isBlank()) {
            lista = productoServicio.buscarPorCodigoODescripcion(busqueda);
        } else {
            lista = productoServicio.listarProductosDisponibles();
        }

        double total = productoServicio.obtenerTotalStock(lista);

        model.addAttribute("productos", lista);
        model.addAttribute("totalStock", total);
        model.addAttribute("busqueda", busqueda);

        return "informes/stock";
    }

    @GetMapping("/stock/pdf")
    public void descargarPDFStock(HttpServletResponse response) throws Exception {

        List<Producto> productos = productoServicio.listarProductosDisponibles(); // productos con stock > 0
        if (productos.isEmpty()) {
            return; // si no hay productos, no genera PDF
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=stock_disponible.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // ---------------------------
        // ENCABEZADO CON LOGO
        try {
            ClassPathResource imgFile = new ClassPathResource("static/img/logoChancho.png");
            Image logo = new Image(ImageDataFactory.create(imgFile.getInputStream().readAllBytes()));

            logo.setWidth(60);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            Paragraph titulo = new Paragraph("Carnicería JP")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.RED)
                    .setMarginLeft(10)
                    .setMarginBottom(0);

            Paragraph subtitulo = new Paragraph("Informe de Stock Disponible")
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginLeft(10)
                    .setMarginTop(-5);

            float[] encabezadoCols = {80f, 400f};
            Table encabezado = new Table(encabezadoCols).setBorder(Border.NO_BORDER);

            encabezado.addCell(new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            Cell cellTexto = new Cell()
                    .add(titulo)
                    .add(subtitulo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            encabezado.addCell(cellTexto);

            document.add(encabezado);

            document.add(new LineSeparator(new SolidLine()).setMarginBottom(15));

        } catch (Exception e) {
            // si falla el logo, sigue sin él
        }

        // ---------------------------
        // TABLA DE PRODUCTOS
        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 45, 15, 15, 20}))
                .useAllAvailableWidth();

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Código").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Cantidad Disponible").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Valor Total").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        double totalStock = 0.0;
        for (Producto p : productos) {
            table.addCell(String.valueOf(p.getId()));
            table.addCell(p.getDescripcion());
            table.addCell("$ " + p.getPrecioUnitario());
            table.addCell(String.valueOf(p.getStock()));
            double valor = p.getStock() * p.getPrecioUnitario();
            totalStock += valor;
            table.addCell("$ " + valor);
        }

        document.add(table);

        // ---------------------------
        // TOTAL GENERAL
        document.add(new Paragraph("\nValor Total del Stock: $ " + totalStock)
                .setBold()
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT));

        // ---------------------------
        // PIE DE PÁGINA
        agregarPiePagina(document);

        document.close();
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

    @GetMapping("/ventas/pdf")
    public void descargarPDFVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletResponse response
    ) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=informe_ventas.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // ---------------------------
        // 1) ENCABEZADO CON LOGO
        try {
            ClassPathResource imgFile = new ClassPathResource("static/img/logoChancho.png");
            Image logo = new Image(ImageDataFactory.create(imgFile.getInputStream().readAllBytes()));
            logo.setWidth(60);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            Paragraph titulo = new Paragraph("Carnicería JP")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.RED)
                    .setMarginLeft(10)
                    .setMarginBottom(0);

            Paragraph subtitulo = new Paragraph("Informe de Ventas")
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginLeft(10)
                    .setMarginTop(-5);

            // Encabezado en tabla
            float[] encabezadoCols = {80f, 400f};
            Table encabezado = new Table(encabezadoCols).setBorder(Border.NO_BORDER);

            encabezado.addCell(new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            Cell cellTexto = new Cell()
                    .add(titulo)
                    .add(subtitulo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            encabezado.addCell(cellTexto);

            document.add(encabezado);

            // Línea divisoria
            document.add(new LineSeparator(new SolidLine()).setMarginBottom(15));

        } catch (Exception e) {
            // si falla el logo, sigue sin él
        }

        // ---------------------------
        // 2) Formato de fechas y período
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatoFechaSolo = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDateTime desde = null;
        LocalDateTime hasta = null;
        String periodo = "Todos los registros";

        if (fechaInicio != null && fechaFin != null) {
            desde = fechaInicio.atStartOfDay();
            hasta = fechaFin.atTime(23, 59, 59);
            periodo = "Período: " + fechaInicio.format(formatoFechaSolo) + " - " + fechaFin.format(formatoFechaSolo);
        }

        document.add(new Paragraph(periodo).setFontSize(12).setItalic().setMarginBottom(10));

        // ---------------------------
        // 3) Obtener ventas filtradas
        List<Venta> ventasFiltradas = (desde != null)
                ? ventaServicio.findByFechaHoraBetween(desde, hasta)
                : ventaServicio.listarVentas();

        if (ventasFiltradas.isEmpty()) {
            document.add(new Paragraph("No se encontraron ventas para el período seleccionado.")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY));
        } else {
            double totalGeneral = 0.0;

            for (Venta v : ventasFiltradas) {

                // Encabezado de la venta
                Paragraph ventaHeader = new Paragraph("Factura N°: " + v.getNroFactura()
                        + "    Cliente: " + (v.getUsuario() != null ? v.getUsuario().getUsername() : "Consumidor Final"))
                        .setBold()
                        .setFontSize(12);
                Paragraph fechaVenta = new Paragraph("Fecha: " + v.getFechaHora().format(formatoFecha))
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY);

                document.add(ventaHeader);
                document.add(fechaVenta);

                // Tabla de detalles
                Table table = new Table(UnitValue.createPercentArray(new float[]{40, 15, 20, 25})).useAllAvailableWidth();

                table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                table.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                table.addHeaderCell(new Cell().add(new Paragraph("Precio Total").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

                for (VentaDetalle det : v.getListaVentaDetalle()) {
                    table.addCell(det.getProducto() != null ? det.getProducto().getDescripcion() : "");
                    table.addCell(String.valueOf(det.getTotalCantidad()));
                    table.addCell("$ " + det.getPrecioUnitActual());
                    table.addCell("$ " + det.getPrecioTotalProducto());
                }

                document.add(table);

                // Total venta
                Paragraph totalVenta = new Paragraph("Total Venta: $ " + v.getPrecioTotal())
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBold()
                        .setMarginBottom(10);
                document.add(totalVenta);

                totalGeneral += v.getPrecioTotal();
            }

            // Total general al final
            Paragraph totalGral = new Paragraph("Total General: $ " + totalGeneral)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10);
            document.add(totalGral);
        }

        // Pie de página
        agregarPiePagina(document);

        document.close();
    }

    @GetMapping("/ganancias")
    public String informeGanancias(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // --- 1) Lista de productos que alguna vez se vendieron (para el selector) ---
        List<Producto> productosVendidos = productoServicio.listarProductosQueSeVendieron();
        model.addAttribute("productos", productosVendidos);

        // Convertir fechas si vienen
        LocalDateTime desde = null;
        LocalDateTime hasta = null;
        if (fechaInicio != null && fechaFin != null) {
            desde = fechaInicio.atStartOfDay();
            hasta = fechaFin.atTime(23, 59, 59);
            if (hasta.isBefore(desde)) {
                model.addAttribute("errorPeriodo", "La fecha final no puede ser menor que la inicial.");
                return "informes/ganancias";
            }
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
        }

        // --- 2) Si se seleccionó un producto específico (no 0 ni null) -> mostrar solo su ganancia total ---
        if (productoId != null && productoId != 0) {
            Producto producto = productoServicio.obtenerPorId(productoId);
            model.addAttribute("productoSeleccionado", producto);

            // obtener todos los detalles en el rango (si hay fechas) o globalmente
            List<VentaDetalle> detalles;
            if (desde != null) {
                // filtramos por ventas en el periodo y por producto
                List<Venta> ventasPeriodo = ventaServicio.findByFechaHoraBetween(desde, hasta);
                detalles = ventasPeriodo.stream()
                        .flatMap(v -> v.getListaVentaDetalle().stream())
                        .filter(det -> det.getProducto() != null && det.getProducto().getId() == productoId)
                        .collect(Collectors.toList());
            } else {
                // sin periodo: todos los detalles de ese producto
                detalles = ventaDetalleServicio.findByProductoId(productoId);
            }

            double gananciaTotal = detalles.stream()
                    .mapToDouble(det -> det.getPrecioTotalProducto() - (det.getTotalCantidad() * det.getPrecioCostoActual()))
                    .sum();

            model.addAttribute("gananciaTotal", gananciaTotal);
            model.addAttribute("detallesProducto", detalles); // opcional si querés desglosarlo
            return "informes/ganancias"; // la misma vista; la plantilla decidirá qué mostrar
        }

        // --- 3) Si NO se seleccionó producto (o seleccionó "Todos" -> productoId == 0 o null) -> calcular ganancia por producto ---
        // Obtener ventas según periodo o todas
        List<Venta> ventasFiltradas;
        if (desde != null) {
            ventasFiltradas = ventaServicio.findByFechaHoraBetween(desde, hasta);
        } else {
            ventasFiltradas = ventaServicio.listarVentas(); // o un método que traiga todas
        }

        // Map productoId -> ganancia
        Map<Integer, Double> gananciaPorProductoId = new HashMap<>();
        Map<Integer, Producto> productoPorId = new HashMap<>();

        for (Venta v : ventasFiltradas) {
            for (VentaDetalle det : v.getListaVentaDetalle()) {
                if (det.getProducto() == null) {
                    continue;
                }
                int pid = det.getProducto().getId();
                productoPorId.putIfAbsent(pid, det.getProducto());
                double gan = det.getPrecioTotalProducto() - (det.getTotalCantidad() * det.getPrecioCostoActual());
                gananciaPorProductoId.put(pid, gananciaPorProductoId.getOrDefault(pid, 0.0) + gan);
            }
        }

        // Convertir a lista ordenable para la vista: List<{producto, ganancia}>
        List<Map<String, Object>> listaResumen = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : gananciaPorProductoId.entrySet()) {
            Producto p = productoPorId.get(e.getKey());
            Map<String, Object> row = new HashMap<>();
            row.put("producto", p);
            row.put("ganancia", e.getValue());
            listaResumen.add(row);
        }

        // opcional ordenar por ganancia desc
        listaResumen.sort((a, b) -> Double.compare((Double) b.get("ganancia"), (Double) a.get("ganancia")));

        model.addAttribute("resumenPorProducto", listaResumen);
        model.addAttribute("gananciaPeriodo", listaResumen.stream().mapToDouble(m -> (Double) m.get("ganancia")).sum());
        model.addAttribute("ventasPeriodoCount", ventasFiltradas.size());

        return "informes/ganancias";
    }

    @GetMapping("/ganancias/pdf")
    public void descargarPDFGanancias(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletResponse response
    ) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=informe_ganancias.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // ---------------------------
        // 1) ENCABEZADO CON LOGO
        try {
            ClassPathResource imgFile = new ClassPathResource("static/img/logoChancho.png");
            Image logo = new Image(ImageDataFactory.create(imgFile.getInputStream().readAllBytes()));

            logo.setWidth(60);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            Paragraph titulo = new Paragraph("Carnicería JP")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.RED)
                    .setMarginLeft(10)
                    .setMarginBottom(0);

            Paragraph subtitulo = new Paragraph("Informe de Ganancias")
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginLeft(10)
                    .setMarginTop(-5);

            // Encabezado en tabla
            float[] encabezadoCols = {80f, 400f};
            Table encabezado = new Table(encabezadoCols).setBorder(Border.NO_BORDER);

            encabezado.addCell(new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            Cell cellTexto = new Cell()
                    .add(titulo)
                    .add(subtitulo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            encabezado.addCell(cellTexto);

            document.add(encabezado);

            // Línea divisoria
            document.add(new LineSeparator(new SolidLine()).setMarginBottom(15));

        } catch (Exception e) {
            // si falla el logo, sigue sin él
        }

        // ---------------------------
        // 2) FORMATO DE FECHAS
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime desde = null;
        LocalDateTime hasta = null;

        if (fechaInicio != null && fechaFin != null) {
            desde = fechaInicio.atStartOfDay();
            hasta = fechaFin.atTime(23, 59, 59);
        }

        // -----------------------------------------------------
        // 3) PDF PARA PRODUCTO ESPECÍFICO
        // -----------------------------------------------------
        if (productoId != null && productoId != 0) {

            Producto producto = productoServicio.obtenerPorId(productoId);

            List<VentaDetalle> detalles;
            if (desde != null) {
                List<Venta> ventasPeriodo = ventaServicio.findByFechaHoraBetween(desde, hasta);
                detalles = ventasPeriodo.stream()
                        .flatMap(v -> v.getListaVentaDetalle().stream())
                        .filter(det -> det.getProducto() != null && det.getProducto().getId() == productoId)
                        .collect(Collectors.toList());
            } else {
                detalles = ventaDetalleServicio.findByProductoId(productoId);
            }

            double gananciaTotal = detalles.stream()
                    .mapToDouble(det -> det.getPrecioTotalProducto() - det.getTotalCantidad() * det.getPrecioCostoActual())
                    .sum();

            document.add(new Paragraph("Producto: " + producto.getDescripcion())
                    .setFontSize(14).setBold());
            document.add(new Paragraph("Ganancia: $ " + gananciaTotal)
                    .setFontSize(12));
            document.add(new Paragraph("\n"));

            // Tabla
            float[] columnas = {200f, 100f};
            Table table = new Table(columnas).useAllAvailableWidth();

            // Encabezados con estilo
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("Fecha"))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setBold()
            );

            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("Ganancia"))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setBold()
            );

            for (VentaDetalle det : detalles) {
                Venta v = det.getVenta();

                double gan = det.getPrecioTotalProducto() - det.getTotalCantidad() * det.getPrecioCostoActual();
                String fecha = v.getFechaHora().format(formatoFecha);

                table.addCell(fecha);
                table.addCell("$ " + gan);
            }

            document.add(table);

            // Pie de página
            agregarPiePagina(document);
            document.close();
            return;
        }

        // -----------------------------------------------------
        // 4) PDF PARA TODOS LOS PRODUCTOS
        // -----------------------------------------------------
        List<Venta> ventasFiltradas = (desde != null)
                ? ventaServicio.findByFechaHoraBetween(desde, hasta)
                : ventaServicio.listarVentas();

        Map<Integer, Double> gananciaPorProductoId = new HashMap<>();
        Map<Integer, Producto> productoPorId = new HashMap<>();

        for (Venta v : ventasFiltradas) {
            for (VentaDetalle det : v.getListaVentaDetalle()) {

                if (det.getProducto() == null) {
                    continue;
                }

                int pid = det.getProducto().getId();
                productoPorId.putIfAbsent(pid, det.getProducto());

                double gan = det.getPrecioTotalProducto() - (det.getTotalCantidad() * det.getPrecioCostoActual());
                gananciaPorProductoId.put(pid, gananciaPorProductoId.getOrDefault(pid, 0.0) + gan);
            }
        }

        document.add(new Paragraph("Ganancias Totales por Producto")
                .setFontSize(16).setBold().setMarginBottom(10));

//        float[] columnas = {280f, 150f};
//        Table table = new Table(columnas);
//
//        // Asegurate de usar el Cell correcto: com.itextpdf.layout.element.Cell
//        Cell headerFecha = new Cell()
//                .add(new Paragraph("Fecha").setBold())
//                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                .setTextAlignment(TextAlignment.LEFT)
//                .setPadding(5f);
//
//        Cell headerGanancia = new Cell()
//                .add(new Paragraph("Ganancia").setBold())
//                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                .setTextAlignment(TextAlignment.RIGHT)
//                .setPadding(5f);
//
//        table.addHeaderCell(headerFecha);
//        table.addHeaderCell(headerGanancia);
        Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .useAllAvailableWidth();

        table.addHeaderCell(
                new Cell()
                        .add(new Paragraph("Producto"))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setBold()
        );

        table.addHeaderCell(
                new Cell()
                        .add(new Paragraph("Ganancia Total"))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setBold()
        );

        for (Map.Entry<Integer, Double> entry : gananciaPorProductoId.entrySet()) {
            Producto p = productoPorId.get(entry.getKey());
            table.addCell(p.getDescripcion());
            table.addCell("$ " + entry.getValue());
        }

        document.add(table);

        agregarPiePagina(document);

        document.close();
    }

    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n\n"));
        LineSeparator ls = new LineSeparator(new SolidLine());
        ls.setOpacity(0.3f);
        document.add(ls);

        Paragraph footer = new Paragraph("Carnicería JP - Libertad 1950")
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5);

        document.add(footer);
    }

}
