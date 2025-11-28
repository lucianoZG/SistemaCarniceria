/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.services.ProductoService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    //Listar productos
    @GetMapping
    public String listar(@RequestParam(value = "estado", required = false, defaultValue = "activos") String estado,
            @RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
//        List<Producto> listaProductos = servicio.listarTodos();
        List<Producto> listaProductos;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            // Buscar por texto y estado
            switch (estado.toLowerCase()) {
                case "inactivos":
                    listaProductos = servicio.buscarPorDescripcionOCodigoYEstado(busqueda, false);
                    break;
                case "todos":
                    listaProductos = servicio.buscarPorDescripcionOCodigo(busqueda);
                    break;
                default:
                    listaProductos = servicio.buscarPorDescripcionOCodigoYEstado(busqueda, true);
                    break;
            }
        } else {
            if ("inactivos".equalsIgnoreCase(estado)) {
                listaProductos = servicio.listarInactivos();
            } else {
                listaProductos = servicio.listarActivos();
            }
        }

        model.addAttribute("productos", listaProductos);
        model.addAttribute("estado", estado);
        model.addAttribute("busqueda", busqueda);

        return "/productos/productos";
    }

    //Registrar producto
    @GetMapping("/registrarProducto")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("producto", new Producto());
        return "/productos/registrarProducto"; // nombre de la vista Thymeleaf (productos/registrarProducto.html)
    }

    @PostMapping
    public String registrar(@ModelAttribute("producto") Producto producto,
            BindingResult result,
            @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {
//        servicio.registrarProducto(producto.getDescripcion(), producto.getPrecioUnitario(), producto.getCantidad());
        // 1. VALIDACIÓN: Verificamos si el nombre ya existe
        if (servicio.existeProducto(producto.getDescripcion())) {
            // Rechazamos el valor del campo 'proDescripcion' (o 'nombre')
            result.rejectValue("descripcion", "error.producto", "¡Este producto ya está registrado!");
        }

        // 2. Si hay errores (por la validación de arriba o validaciones de @Valid), volvemos al formulario
        if (result.hasErrors()) {
            // No hacemos redirect, retornamos la vista para no perder los datos que el usuario escribió
            return "/productos/registrarProducto";
        }

        // --- Si pasó la validación, procedemos con la lógica normal ---        
        // Si subió una imagen
        if (!imagenFile.isEmpty()) {

            String uploadDir = "uploads/productos/";
            String fileName = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imagenFile.getBytes());

            producto.setImagen("/uploads/productos/" + fileName);
        }

        servicio.registrarProducto(producto);
        return "redirect:/productos";
    }

    //Editar producto
    @GetMapping("/editar/{id}")
    public String mostrarFormularioModificar(@PathVariable int id, Model model) {
        Producto producto = servicio.obtenerPorId(id);
        model.addAttribute("producto", producto);
        return "/productos/modificarProducto";
    }

    @PostMapping("/editar/{id}")
    public String modificar(@PathVariable int id,
            @ModelAttribute("producto") Producto producto,
            @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {

        // 1. Buscamos el producto ORIGINAL que está en la base de datos
        Producto productoEnBD = servicio.obtenerPorId(id);

        // Si subió una nueva imagen
        if (!imagenFile.isEmpty()) {

            String uploadDir = "uploads/productos/";
            String fileName = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imagenFile.getBytes());

            producto.setImagen("/uploads/productos/" + fileName);
        } else {
            // Mantener imagen anterior
            producto.setImagen(productoEnBD.getImagen());
        }

//        producto.setId(id);
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
