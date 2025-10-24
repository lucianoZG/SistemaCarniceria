package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer>{
    
}
