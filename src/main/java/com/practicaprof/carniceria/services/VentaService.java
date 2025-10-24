package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Venta;
import com.practicaprof.carniceria.repositories.VentaRepository;
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
}
