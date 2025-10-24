package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.repositories.ProductoInventarioRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductoInventarioService {
    
    private final ProductoInventarioRepository productoInventarioRepo;

    public ProductoInventarioService(ProductoInventarioRepository productoInventarioRepo) {
        this.productoInventarioRepo = productoInventarioRepo;
    }
    
    public void registrarStock(Producto producto, Inventario inventario, double stockActual) {
        ProductoInventario pi = new ProductoInventario(producto, inventario, stockActual);
        productoInventarioRepo.save(pi);
    }
    
}
