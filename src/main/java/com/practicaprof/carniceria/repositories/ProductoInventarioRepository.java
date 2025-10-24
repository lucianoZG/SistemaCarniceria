package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.ProductoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoInventarioRepository extends JpaRepository<ProductoInventario, Integer>{
    
}
