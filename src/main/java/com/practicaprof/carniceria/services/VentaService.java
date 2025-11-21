package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.entities.Usuario;
import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.entities.VentaDetalle;
import com.practicaprof.carniceria.repositories.VentaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    private final VentaRepository repositorio;
    private final ProductoInventarioService productoInventarioServicio;
    private final ProductoService productoServicio;

    public VentaService(VentaRepository repositorio, ProductoInventarioService productoInventarioServicio, ProductoService productoServicio) {
        this.repositorio = repositorio;
        this.productoInventarioServicio = productoInventarioServicio;
        this.productoServicio = productoServicio;
    }

    public List<Venta> listarVentas() {
        return repositorio.findAll();
    }

    public String registrarVenta(Venta venta) {

        for (VentaDetalle detalle : venta.getListaVentaDetalle()) {
            int idProducto = detalle.getProducto().getId();

            // Buscar stock actual
//            Optional<ProductoInventario> optionalInventario = productoInventarioServicio.findByProductoIdDelUltimoInventario(idProducto);
            Producto producto = productoServicio.obtenerPorId(idProducto);

//            if (optionalInventario.isEmpty()) {
//                return "El producto con ID " + idProducto + " no pertenece al último inventario o no está activo.";
//            }

//            ProductoInventario productoInv = optionalInventario.get();
            // Validar stock
            if (detalle.getTotalCantidad() > producto.getStock()) {
                return "Stock insuficiente para el producto " + producto.getDescripcion()
                        + ". Disponible: " + producto.getStock()
                        + ", solicitado: " + detalle.getTotalCantidad();
            }

            // Descontar stock
//            productoInv.setStockActual(productoInv.getStockActual() - detalle.getTotalCantidad());
//            productoInventarioServicio.registrarStock(productoInv);
            detalle.setPrecioUnitActual(producto.getPrecioUnitario());
            detalle.setPrecioCostoActual(producto.getPrecioCosto());
//            System.out.println("Producto: " + producto.getDescripcion() + " | Costo: " + detalle.getPrecioCostoActual());

            producto.setStock(producto.getStock() - detalle.getTotalCantidad());
            productoServicio.registrarProducto(producto);
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

    public String obtenerEmpleadoConMasVentasUltimoMes() {
        LocalDateTime haceUnMes = LocalDateTime.now().minusMonths(1);
        List<Object[]> resultados = repositorio.obtenerEmpleadoConMasVentasUltimoMes(haceUnMes);

        if (resultados.isEmpty()) {
            return "Sin datos disponibles";
        }

        Object[] fila = resultados.get(0); // solo tomamos la primera fila

        String nombreEmpleado = fila[0].toString();
        Number cantidadVentasNum = (Number) fila[1];
        long cantidadVentas = cantidadVentasNum.longValue();

        return nombreEmpleado + " (" + cantidadVentas + " ventas)";
    }

    public List<Venta> buscarPorFacturaClienteOFecha(String texto, LocalDate fecha) {
        return repositorio.buscarPorFacturaClienteOFecha(texto, fecha);
    }

}
