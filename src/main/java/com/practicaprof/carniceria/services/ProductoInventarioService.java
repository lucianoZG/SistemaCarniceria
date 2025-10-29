package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.repositories.ProductoInventarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoInventarioService {
    
    private final ProductoInventarioRepository productoInventarioRepo;

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
    
}
