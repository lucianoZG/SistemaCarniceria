package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>{
    
}
