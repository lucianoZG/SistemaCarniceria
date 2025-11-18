package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.repositories.InventarioRepository;
import com.practicaprof.carniceria.repositories.ProductoInventarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class ProductoInventarioService {
    
    private final ProductoInventarioRepository productoInventarioRepo;
    
    @Autowired
    private InventarioRepository inventarioRepository;

    public ProductoInventarioService(ProductoInventarioRepository productoInventarioRepo) {
        this.productoInventarioRepo = productoInventarioRepo;
    }
    
    public void registrarStock(Producto producto, Inventario inventario, double stockActual, double stockRelevado) {
        ProductoInventario pi = new ProductoInventario(producto, inventario, stockActual, stockRelevado);
        productoInventarioRepo.save(pi);
    }
    
    public List<ProductoInventario> listarUltimoInventario() {
        return productoInventarioRepo.listarDelUltimoInventario();
    }
    
    public void registrarStock(ProductoInventario pi) {
        productoInventarioRepo.save(pi);
    }
    
    public Optional<ProductoInventario> findByProductoIdDelUltimoInventario(@Param("productoId") int productoId) {
        return productoInventarioRepo.findByProductoIdDelUltimoInventario(productoId);
    }
    
    public ProductoInventario findByProductoId(int productoId) {
        return productoInventarioRepo.findByProductoId(productoId).get();
    }
    
    public String obtenerProductoConMenorStock() {
        Object resultado = productoInventarioRepo.obtenerProductoConMenorStock();

        if (resultado == null) {
            return "Sin datos de inventario disponibles";
        }

        Object[] fila = (Object[]) resultado;
        String nombre = (String) fila[0];
        Double stock = ((Number) fila[1]).doubleValue();

        return nombre + " (" + stock + " kg disponibles)";
    }
    
    public int obtenerIdDelUltimoInventario() {
        return inventarioRepository.findTopByOrderByFechaDesc()
                .map(inv -> inv.getId())
                .orElseThrow(() -> new RuntimeException("No hay inventarios registrados"));
    }
}
