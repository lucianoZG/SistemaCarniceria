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
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.practicaprof.carniceria.dto.CarritoDto;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.services.ProductoService;
import com.practicaprof.carniceria.services.UsuarioService;
import com.practicaprof.carniceria.services.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String mostrarCompras(@RequestParam(required = false) String busqueda, Model model) {
        List<Producto> productos;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoService.buscarPorDescripcion(busqueda);
        } else {
            productos = productoService.listarProductosDisponibles(); // O como obtengas los productos
        }

        model.addAttribute("productos", productos);
        model.addAttribute("venta", new Venta());
        return "cliente/compras";
    }

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

//    @PostMapping("/compras/comprobante")
//    public void generarComprobante(@RequestBody List<CarritoDto> carrito, HttpServletResponse response) throws IOException {
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=comprobante.pdf");
//
//        PdfWriter writer = new PdfWriter(response.getOutputStream());
//        PdfDocument pdf = new PdfDocument(writer);
//        Document document = new Document(pdf);
//
//        // -------- TITULO --------
//        Paragraph titulo = new Paragraph("COMPROBANTE DE COMPRA")
//                .setFontSize(18)
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//                .setMarginBottom(20);
//        document.add(titulo);
//
//        // -------- FECHA --------
//        String fechaHoy = java.time.LocalDate.now()
//                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        document.add(new Paragraph("Fecha: " + fechaHoy).setMarginBottom(15));
//
//        // -------- TABLA --------
//        Table table = new Table(UnitValue.createPercentArray(new float[]{200f, 80f, 80f, 80f}))
//                .useAllAvailableWidth();
//
//        agregarHeader(table, "Producto");
//        agregarHeader(table, "Cantidad");
//        agregarHeader(table, "P.U.");
//        agregarHeader(table, "Subtotal");
//
//        double total = 0;
//        for (CarritoDto item : carrito) {
//            table.addCell(new Cell().add(new Paragraph(item.getDescripcion())));
//            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTotalCantidad()))));
//            table.addCell(new Cell().add(new Paragraph("$" + item.getPrecioUnitActual())));
//            total += item.getPrecioTotalProducto();
//            table.addCell(new Cell().add(new Paragraph("$" + item.getPrecioTotalProducto())));
//        }
//
//        document.add(table);
//
//        document.add(new Paragraph("\nTOTAL: $" + total)
//                .setBold()
//                .setFontSize(14));
//
//        document.close();
//    }
//
//    private void agregarHeader(Table table, String titulo) {
//        Cell cell = new Cell()
//                .add(new Paragraph(titulo).setBold())
//                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(230, 230, 230))
//                .setTextAlignment(TextAlignment.CENTER);
//        table.addCell(cell);
//    }
    @PostMapping("/compras/comprobante")
    public void generarComprobante(
            @RequestBody List<CarritoDto> carrito,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=comprobante.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // =======================================================
        // 1) ENCABEZADO CON LOGO + DATOS DERECHA
        // =======================================================
        try {
            ClassPathResource imgFile = new ClassPathResource("static/img/logoChancho.png");
            Image logo = new Image(ImageDataFactory.create(imgFile.getInputStream().readAllBytes()))
                    .scaleToFit(60, 60);

            Paragraph titulo = new Paragraph("Carnicería JP")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.RED);

            String fecha = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Paragraph datosDerecha = new Paragraph()
                    .add(new Paragraph("Comprobante de Compra")
                            .setFontSize(14)
                            .setBold()
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Fecha: " + fecha)
                            .setTextAlignment(TextAlignment.RIGHT));

            Table encabezado = new Table(new float[]{300f, 200f})
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER);

            Cell cellIzq = new Cell()
                    .add(logo)
                    .add(titulo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell cellDer = new Cell()
                    .add(datosDerecha)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            encabezado.addCell(cellIzq);
            encabezado.addCell(cellDer);

            document.add(encabezado);
            document.add(new LineSeparator(new SolidLine()).setMarginBottom(10));

        } catch (Exception ignored) {
        }

        // =======================================================
        // 2) INFORMACIÓN GENERAL (dirección, etc.)
        // =======================================================
        document.add(new Paragraph("Libertad 1950 - Santiago Del Estero (CP 4200) - Santiago del Estero"));

        document.add(new Paragraph("\n"));

        // =======================================================
        // 3) TABLA DETALLADA
        // =======================================================
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                .useAllAvailableWidth();

        table.addHeaderCell(headerCell("Producto"));
        table.addHeaderCell(headerCell("Cantidad"));
        table.addHeaderCell(headerCell("P.U."));
        table.addHeaderCell(headerCell("Subtotal"));

        double total = 0;

        for (CarritoDto item : carrito) {

            table.addCell(normalCell(item.getDescripcion()));
            table.addCell(normalCell(String.valueOf(item.getTotalCantidad())));
            table.addCell(normalCell("$" + item.getPrecioUnitActual()));
            table.addCell(normalCell("$" + item.getPrecioTotalProducto()));

            total += item.getPrecioTotalProducto();
        }

        document.add(table);

        // =======================================================
        // 4) TOTAL
        // =======================================================
        document.add(new Paragraph("\n"));

        Paragraph totalParagraph = new Paragraph("TOTAL: $" + total)
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT);

        document.add(totalParagraph);

        // =======================================================
        // 5) PIE DE PÁGINA
        // =======================================================
        agregarPiePagina(document);

        document.close();
    }

// ========= HELPERS REUSADOS =========
    private Cell headerCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell normalCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n"));
        LineSeparator ls = new LineSeparator(new SolidLine());
        ls.setOpacity(0.3f);
        document.add(ls);

        document.add(
                new Paragraph("Carnicería JP - Libertad 1950")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.GRAY)
        );
    }

}
