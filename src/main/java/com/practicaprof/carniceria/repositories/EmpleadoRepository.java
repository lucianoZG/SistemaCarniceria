package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Empleado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    @Query("SELECT e FROM Empleado e WHERE e.estado = true")
    List<Empleado> listarActivos();
    
}
