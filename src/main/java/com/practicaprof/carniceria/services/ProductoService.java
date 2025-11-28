package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.repositories.ProductoInventarioRepository;
import com.practicaprof.carniceria.repositories.ProductoRepository;
import com.practicaprof.carniceria.repositories.VentaDetalleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository repositorio;
    private final ProductoInventarioRepository proInvRepo;
    private final ProductoInventarioService proInvServicio;
    private final VentaDetalleRepository venDetRepo;

    public ProductoService(ProductoRepository repositorio, ProductoInventarioRepository proInvRepo,
            ProductoInventarioService productoInventarioService,
            VentaDetalleRepository venDetRepo) {
        this.repositorio = repositorio;
        this.proInvRepo = proInvRepo;
        this.proInvServicio = productoInventarioService;
        this.venDetRepo = venDetRepo;
    }

//    public Producto registrarProducto(String descripcion, double precio, double cantidad) {
//        Producto pro = new Producto();
//        
//        pro.setDescripcion(descripcion);
//        pro.setPrecioUnitario(precio);
//        pro.setCantidad(cantidad);
//        pro.setEstado(true);
//        
//        return repositorio.save(pro);
//    } 
    public void registrarProducto(Producto producto) {
//        producto.setEstado(true);
        repositorio.save(producto);
    }

    public List<Producto> listarActivos() {
        return repositorio.listarActivos();
    }

    public List<Producto> listarInactivos() {
        return repositorio.listarInactivos();
    }

    public List<Producto> listarTodos() {
        return repositorio.findAll();
    }
    
    public boolean existeProducto(String nombre) {
        return repositorio.existsByDescripcion(nombre);
    }

    public List<Producto> buscarPorDescripcionOCodigo(String texto) {
        return repositorio.findByDescripcionContainingIgnoreCaseOrIdAsString(texto);
    }

    public List<Producto> buscarPorDescripcionOCodigoYEstado(String texto, boolean estado) {
        return repositorio.findByEstadoAndDescripcionContainingIgnoreCaseOrEstadoAndIdAsString(estado, texto, estado, texto);
    }

    public Producto editar(Producto pro) {
//        Optional<Empleado> empleadoBuscado = repositorio.findById(id);
//        Empleado empleadoExistente;
//        
//        if (empleadoBuscado.isEmpty()) {
//            return null;
//        } else {
//            empleadoExistente = emp.get();

//            empleadoExistente.setNombre(emp.getNombre());
//            empleadoExistente.setDni(emp.getDni());
//            empleadoExistente.setDireccion(emp.getDireccion());
//            empleadoExistente.setTelefono(emp.getTelefono());
        pro.setEstado(true);
        return repositorio.save(pro);
//        }
    }

    public void eliminar(int id) {
        Optional<Producto> productoBuscado = repositorio.findById(id);

        if (productoBuscado.isPresent()) {
            Producto pro = productoBuscado.get();

            pro.setEstado(false);

            repositorio.save(pro);
        }
    }

    public Producto obtenerPorId(int id) {
        Optional<Producto> pro = repositorio.findById(id);
        Producto producto = pro.get();
        return producto;
    }

//    public double obtenerTotalStock(List<Producto> productos) {
//        double total = 0;
//        
//        for (Producto p : productos) {
//            total += p.getCantidad() * p.getPrecioUnitario();
//        }
//        
//        return total;
//    }
    public double obtenerTotalStock(List<Producto> lista) {
//        List<ProductoInventario> lista = listarStockActual();
//        List<Producto> lista = listarProductosDisponibles();

        double total = 0;

        for (Producto p : lista) {
            total += p.getStock() * p.getPrecioUnitario();
        }
        return total;
    }

    public List<ProductoInventario> listarStockActual() {
        return proInvRepo.listarConProductosActivos()
                .stream()
                .filter(pi -> pi.getStockActual() > 0) // solo los que tienen stock > 0
                .toList();
    }

//    public List<Producto> listarDisponiblesParaVenta() {
//        int ultimoInventarioId = proInvServicio.obtenerIdDelUltimoInventario();
//        List<Producto> productos = repositorio.findProductosConStockDisponible(ultimoInventarioId);
//
//        //Implementar que al estar inactivo el producto, su stock sea 0.
    ////        for (Producto p : productos) {
////            ProductoInventario pi = proInvRepo.findByProductoAndInventario(p, ultimoInventarioId);
////            if (!p.isEstado()) {
////                pi.setStockActual(0);
////            }
////        }
//        return productos;
//    }

    public List<Producto> listarProductosDisponibles() {
        return repositorio.findProductosConStockDisponible();
    }

    public List<Producto> listarProductosUltimoInventario() {
        int ultimoInventarioId = proInvServicio.obtenerIdDelUltimoInventario();
        List<Producto> productos = repositorio.findProductosUltimoInventario(ultimoInventarioId);

        return productos;
    }

    // in ProductoService (o crear un método que llame a VentaDetalleRepository)
    public List<Producto> listarProductosQueSeVendieron() {
        return venDetRepo.findDistinctProductosVendidos();
    }

    public List<Producto> buscarPorDescripcion(String texto) {
        return repositorio.findByDescripcion(texto);
    }

    public Page<Producto> buscarPorDescripcionPaginado(String descripcion, Pageable pageable) {
        return repositorio.findByDescripcionContainingIgnoreCaseAndStockGreaterThan(descripcion, 0, pageable);
    }

    public Page<Producto> listarProductosDisponiblesPaginado(Pageable pageable) {
        return repositorio.findByStockGreaterThanAndEstadoTrue(0, pageable);
    }

    public List<Producto> buscarPorCodigoODescripcion(String texto) {
        return repositorio.findByDescripcionContainingIgnoreCaseOrIdAsString(texto);
    }

    public String obtenerProductoConMenorStock() {
        // Buscamos el producto usando el método nuevo
        Optional<Producto> productoOpt = repositorio.findTopByEstadoTrueOrderByStockAsc();

        if (productoOpt.isPresent()) {
            Producto p = productoOpt.get();
            // Retornamos el string formateado
            return p.getDescripcion() + " (" + p.getStock() + " kg disponibles)";
        } else {
            return "Sin datos de stock";
        }
    }
}
