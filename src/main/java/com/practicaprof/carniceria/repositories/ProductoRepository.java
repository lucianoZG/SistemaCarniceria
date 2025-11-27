package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p WHERE p.estado = true")
    List<Producto> listarActivos();

    @Query("SELECT p FROM Producto p WHERE p.estado = false")
    List<Producto> listarInactivos();
    
    //Verificar si el nombre de ese producto ya existe en la base de datos
    boolean existsByDescripcion(String descripcion);

    // Buscar por descripción o ID convertido a string, se puede simplificar pasando solo un texto.
    @Query("SELECT p FROM Producto p WHERE "
            + "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR CAST(p.id AS string) LIKE CONCAT('%', :texto, '%')")
    List<Producto> findByDescripcionContainingIgnoreCaseOrIdAsString(@Param("texto") String texto);

    // Buscar por descripcion y que el stock sea mayor a 0
    @Query("SELECT p FROM Producto p WHERE "
            + "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "AND p.stock > 0")
    List<Producto> findByDescripcion(@Param("texto") String texto);

    // Igual pero filtrando por estado, se puede simplificar pasando solo un texto.
    @Query("SELECT p FROM Producto p WHERE "
            + "((p.estado = :estado) AND (LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR CAST(p.id AS string) LIKE CONCAT('%', :texto, '%')))")
    List<Producto> findByEstadoAndDescripcionContainingIgnoreCaseOrEstadoAndIdAsString(@Param("estado") boolean estado,
            @Param("texto") String texto1,
            @Param("estado") boolean estado2,
            @Param("texto") String texto2);

    @Query("""
        SELECT p 
        FROM Producto p 
        JOIN ProductoInventario pi ON pi.producto.id = p.id
        WHERE p.estado = true
        AND pi.inventario.id = :inventarioId
        AND pi.stockActual > 0
    """)
    List<Producto> findProductosConStockDisponible(@Param("inventarioId") int inventarioId);

    @Query("""
        SELECT p 
        FROM Producto p
        WHERE p.estado = true
        AND p.stock > 0
    """)
    List<Producto> findProductosConStockDisponible();

    @Query("""
        SELECT p 
        FROM Producto p 
        JOIN ProductoInventario pi ON pi.producto.id = p.id
        WHERE p.estado = true
        AND pi.inventario.id = :inventarioId
    """)
    List<Producto> findProductosUltimoInventario(@Param("inventarioId") int inventarioId);

    Page<Producto> findByDescripcionContainingIgnoreCaseAndStockGreaterThan(String descripcion, int stock, Pageable pageable);

    Page<Producto> findByStockGreaterThan(int stock, Pageable pageable);

    // Esto busca el primero ordenando por stock ascendente.
    // Devuelve un Optional para evitar NullPointerExceptions si la tabla está vacía.
    // Busca el top 1 por stock ascendente PERO solo de los activos
    Optional<Producto> findTopByEstadoTrueOrderByStockAsc();

}
