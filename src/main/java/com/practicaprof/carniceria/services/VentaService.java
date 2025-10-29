package com.practicaprof.carniceria.services;

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

    public VentaService(VentaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Venta> listarVentas() {
        return repositorio.findAll();
    }

    public void registrarVenta(Venta venta) {
        //Establecemos fecha y hora actuales
        venta.setFechaHora(LocalDateTime.now());
        // Generamos número de factura (ejemplo simple)
        venta.setNroFactura("FAC-" + System.currentTimeMillis());
        
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
}
