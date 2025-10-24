package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.repositories.InventarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InventarioService {
    
    private final InventarioRepository repositorio;

    public InventarioService(InventarioRepository repositorio) {
        this.repositorio = repositorio;
    }
    
    public void registrar(Inventario inventario) {
        repositorio.save(inventario);
    }
    
    public List<Inventario> listarTodos() {
        return repositorio.findAll();
    }
    
    public Inventario obtenerPorId(int id) {
        Optional<Inventario> inv = repositorio.findById(id);
        Inventario inventario = inv.get();
        return inventario;
    }
    
}
