package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.ProductoInventario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoInventarioRepository extends JpaRepository<ProductoInventario, Integer>{
    
    @Query("SELECT pi FROM ProductoInventario pi WHERE pi.producto.estado = true")
    List<ProductoInventario> listarConProductosActivos();
    
    @Query("""
           SELECT pi
           FROM ProductoInventario pi
           WHERE pi.inventario.fecha = (
               SELECT MAX(i.fecha) FROM Inventario i
           )
           AND pi.producto.estado = true
           """)
    List<ProductoInventario> listarDelUltimoInventario();
}
