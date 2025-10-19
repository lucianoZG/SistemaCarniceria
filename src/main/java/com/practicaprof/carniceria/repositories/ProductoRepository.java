package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    
    @Query("SELECT p FROM Producto p WHERE p.estado = true")
    List<Producto> listarActivos();
}
