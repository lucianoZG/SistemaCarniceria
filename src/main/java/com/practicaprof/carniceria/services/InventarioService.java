package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Inventario;
import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.ProductoInventario;
import com.practicaprof.carniceria.repositories.InventarioRepository;
import com.practicaprof.carniceria.repositories.ProductoInventarioRepository;
import com.practicaprof.carniceria.repositories.ProductoRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoInventarioRepository productoInventarioRepository;

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }
    
    public InventarioService(InventarioRepository inventarioRepository, ProductoRepository productoRepository, ProductoInventarioRepository productoInventarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.productoInventarioRepository = productoInventarioRepository;
    }

    public void registrarInventario(List<Integer> productosId, List<Integer> stocksRelevados) {

        Inventario inventario = new Inventario();
        inventario.setFecha(LocalDateTime.now());
        inventarioRepository.save(inventario);

        List<ProductoInventario> lista = new ArrayList<>();

        for (int i = 0; i < productosId.size(); i++) {
            Producto producto = productoRepository.findById(productosId.get(i))
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            ProductoInventario pi = new ProductoInventario();
            pi.setProducto(producto);
            pi.setStockRelevado(stocksRelevados.get(i));
            pi.setStockActual(stocksRelevados.get(i));
            pi.setInventario(inventario);

            lista.add(pi);
        }

        productoInventarioRepository.saveAll(lista);
    }

}
