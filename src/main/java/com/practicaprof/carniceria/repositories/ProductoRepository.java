package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p WHERE p.estado = true")
    List<Producto> listarActivos();

    @Query("SELECT p FROM Producto p WHERE p.estado = false")
    List<Producto> listarInactivos();

    // Buscar por descripción o ID convertido a string
    @Query("SELECT p FROM Producto p WHERE "
            + "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR CAST(p.id AS string) LIKE CONCAT('%', :texto, '%')")
    List<Producto> findByDescripcionContainingIgnoreCaseOrIdAsString(@Param("texto") String texto, @Param("texto") String texto2);

    // Igual pero filtrando por estado
    @Query("SELECT p FROM Producto p WHERE "
            + "((p.estado = :estado) AND (LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR CAST(p.id AS string) LIKE CONCAT('%', :texto, '%')))")
    List<Producto> findByEstadoAndDescripcionContainingIgnoreCaseOrEstadoAndIdAsString(@Param("estado") boolean estado,
            @Param("texto") String texto1,
            @Param("estado") boolean estado2,
            @Param("texto") String texto2);
}


