package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.entities.VentaDetalle;
import com.practicaprof.carniceria.repositories.VentaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    private final VentaRepository repositorio;
    private final ProductoInventarioService productoInventarioServicio;

    public VentaService(VentaRepository repositorio, ProductoInventarioService productoInventarioServicio) {
        this.repositorio = repositorio;
        this.productoInventarioServicio = productoInventarioServicio;
    }

    public List<Venta> listarVentas() {
        return repositorio.findAll();
    }

    public String registrarVenta(Venta venta) {

        for (VentaDetalle detalle : venta.getListaVentaDetalle()) {
            int idProducto = detalle.getProducto().getId();

            // Buscar stock actual
            Optional<ProductoInventario> optionalInventario = productoInventarioServicio.findByProductoIdDelUltimoInventario(idProducto);

            if (optionalInventario.isEmpty()) {
                return "El producto con ID " + idProducto + " no pertenece al último inventario o no está activo.";
            }
            
            ProductoInventario productoInv = optionalInventario.get();

            // Validar stock
            if (detalle.getTotalCantidad() > productoInv.getStockActual()) {
                return "Stock insuficiente para el producto " + productoInv.getProducto().getDescripcion()
                        + ". Disponible: " + productoInv.getStockActual()
                        + ", solicitado: " + detalle.getTotalCantidad();
            }

            // Descontar stock
            productoInv.setStockActual(productoInv.getStockActual() - detalle.getTotalCantidad());
            productoInventarioServicio.registrarStock(productoInv);
        }

        //Establecemos fecha y hora actuales
        venta.setFechaHora(LocalDateTime.now());
        // Generamos número de factura
        int puntoVenta = 1;
        long millis = System.currentTimeMillis() % 100_000_000L; // toma los últimos 8 dígitos del timestamp

        String numeroFormateado = String.format("FAC-%04d-%08d", puntoVenta, millis);
        venta.setNroFactura(numeroFormateado);

        //Calculamos precio total
        double total = 0;
        for (VentaDetalle det : venta.getListaVentaDetalle()) {
            double subtotal = det.getPrecioUnitActual() * det.getTotalCantidad();
            det.setPrecioTotalProducto(subtotal);
            det.setVenta(venta); // establecer la relación inversa
            total += subtotal;
        }

        venta.setPrecioTotal(total);

        repositorio.save(venta);

        return null;
    }

    public Venta obtenerPorId(int id) {
        Optional<Venta> ven = repositorio.findById(id);
        Venta venta = ven.get();
        return venta;
    }

    public void eliminar(int id) {
        repositorio.deleteById(id);
    }

    public List<Venta> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta) {
        return repositorio.findByFechaHoraBetween(desde, hasta);
    }
    
    public Double obtenerGananciasDelDia() {
        Double ganancias = repositorio.obtenerGananciasDelDia();
        return ganancias != null ? ganancias : 0.0;
    }
}
