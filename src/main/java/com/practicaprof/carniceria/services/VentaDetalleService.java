package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.VentaDetalle;
import com.practicaprof.carniceria.repositories.VentaDetalleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VentaDetalleService {

    private final VentaDetalleRepository repositorio;

    public VentaDetalleService(VentaDetalleRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<VentaDetalle> listarDetalles() {
        return repositorio.findAll();
    }

    public void registrarDetalle(VentaDetalle detalle) {
        repositorio.save(detalle);
    }

    public VentaDetalle obtenerPorId(int id) {
        Optional<VentaDetalle> det = repositorio.findById(id);
        VentaDetalle venDet = det.get();
        return venDet;
    }

    public void eliminar(int id) {
        repositorio.deleteById(id);
    }

    public String obtenerProductoMasVendido() {
        Object resultado = repositorio.obtenerProductoMasVendidoUltimoMes();

        if (resultado == null) {
            return "No hay ventas registradas este mes";
        }

        Object[] fila = (Object[]) resultado;
        String nombre = (String) fila[0];
        Double total = ((Number) fila[1]).doubleValue();

        return nombre + " (" + total + " kg vendidos)";
    }

    public List<VentaDetalle> findByProductoId(int productoId) {
        return repositorio.findByProductoId(productoId);
    }

    public List<Object[]> obtenerTop5ProductosMasVendidos() {
        return repositorio.obtenerTop5ProductosMasVendidos();
    }

}
