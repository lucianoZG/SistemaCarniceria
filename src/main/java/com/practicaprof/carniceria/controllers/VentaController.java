/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.controllers;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
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
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
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
    public String listar(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {

        List<Venta> listaVentas;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            LocalDate fecha = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            try {
                fecha = LocalDate.parse(busqueda, formatter);
            } catch (DateTimeParseException ignored) {
            }

            listaVentas = ventaServicio.buscarPorFacturaClienteOFecha(busqueda, fecha);
        } else {
            listaVentas = ventaServicio.listarVentas();
        }

        model.addAttribute("ventas", listaVentas);
        model.addAttribute("busqueda", busqueda);

        return "/ventas/ventas";
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("empleados", empleadoServicio.listarActivos());
        model.addAttribute("usuarios", usuarioServicio.listarClientesActivos());
        model.addAttribute("productos", productoServicio.listarProductosDisponibles());
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
            model.addAttribute("productos", productoServicio.listarProductosDisponibles());
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

//        Optional<ProductoInventario> optionalPI = productoInventarioServicio.findByProductoIdDelUltimoInventario(productoId);
//
//        if (optionalPI.isEmpty()) {
//            return ResponseEntity.badRequest().body("El producto no está en el último inventario o está inactivo.");
//        }
//        ProductoInventario pi = optionalPI.get();
        Producto producto = productoServicio.obtenerPorId(productoId);

        if (cantidad > producto.getStock()) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + producto.getStock());
        }

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/pdf/{id}")
    public void descargarPDFVenta(
            @PathVariable int id,
            HttpServletResponse response
    ) throws Exception {

        Venta venta = ventaServicio.obtenerPorId(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=venta_" + venta.getNroFactura() + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // ---------------------------
        // 1) ENCABEZADO CON LOGO + DATOS DERECHA
        try {
            ClassPathResource imgFile = new ClassPathResource("static/img/logoChancho.png");
            Image logo = new Image(ImageDataFactory.create(imgFile.getInputStream().readAllBytes()))
                    .scaleToFit(60, 60);

            Paragraph titulo = new Paragraph("Carnicería JP")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.RED);

            // === Nuevo: factura + fecha arriba a la derecha ===
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Paragraph datosDerecha = new Paragraph()
                    .add(new Paragraph("Factura: " + venta.getNroFactura())
                            .setFontSize(14)
                            .setBold()
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Fecha: " + venta.getFechaHora().format(fmt))
                            .setTextAlignment(TextAlignment.RIGHT));

            // Tabla de 2 columnas: izquierda (logo + empresa) / derecha (datos)
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

        // ---------------------------
        // 2) INFORMACIÓN GENERAL (SIN EMPLEADO)
        document.add(new Paragraph("Libertad 1950 - Santiago Del Estero (CP 4200) - Santiago del Estero"));
        
        document.add(new Paragraph("Cliente: "
                + (venta.getUsuario() != null ? venta.getUsuario().getUsername() : "—")));
        
        document.add(new Paragraph("Correo: "
                + (venta.getUsuario() != null ? venta.getUsuario().getEmail(): "—")));

        // ---------------------------
        // 3) TABLA DE DETALLES
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                .useAllAvailableWidth();

        tabla.addHeaderCell(headerCell("Producto"));
        tabla.addHeaderCell(headerCell("Cantidad"));
        tabla.addHeaderCell(headerCell("Prec. Unit."));
        tabla.addHeaderCell(headerCell("Total"));

        for (VentaDetalle det : venta.getListaVentaDetalle()) {

            tabla.addCell(normalCell(det.getProducto().getDescripcion()));
            tabla.addCell(normalCell(det.getTotalCantidad() + ""));
            tabla.addCell(normalCell("$ " + det.getPrecioUnitActual()));
            tabla.addCell(normalCell("$ " + det.getPrecioTotalProducto()));
        }

        document.add(tabla);

        // ---------------------------
        // 4) TOTAL
        document.add(new Paragraph("\n"));
        Paragraph total = new Paragraph("TOTAL: $ " + venta.getPrecioTotal())
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT);

        document.add(total);

        // ---------------------------
        // 5) PIE DE PÁGINA
        agregarPiePagina(document);

        document.close();
    }

    // Helpers estéticos
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
