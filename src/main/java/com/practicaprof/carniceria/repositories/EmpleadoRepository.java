package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Empleado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    @Query("SELECT e FROM Empleado e WHERE e.estado = true")
    List<Empleado> listarActivos();

    @Query("SELECT e FROM Empleado e WHERE e.estado = false")
    List<Empleado> listarInactivos();

    @Query("SELECT e FROM Empleado e "
            + "WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
            + "OR LOWER(e.dni) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
            + "OR CAST(e.id AS string) LIKE CONCAT('%', :busqueda, '%')")
    List<Empleado> findByNombreOrDniContainingIgnoreCaseOrIdAsString(@Param("busqueda") String busqueda);

    @Query("SELECT e FROM Empleado e "
            + "WHERE e.estado = :estado AND "
            + "LOWER(CONCAT(e.id, ' ', e.nombre, ' ', e.dni)) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Empleado> buscarPorTextoYEstado(@Param("texto") String texto,
            @Param("estado") boolean estado);

}
