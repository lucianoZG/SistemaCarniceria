package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.ProductoInventario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoInventarioRepository extends JpaRepository<ProductoInventario, Integer> {

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

    @Query("""
        SELECT pi
        FROM ProductoInventario pi
        WHERE pi.producto.id = :productoId
        AND pi.inventario.fecha = (
            SELECT MAX(i.fecha) FROM Inventario i
        )
        """)
    Optional<ProductoInventario> findByProductoIdDelUltimoInventario(@Param("productoId") int productoId);

    Optional<ProductoInventario> findByProductoId(int productoId);

    @Query(value = """
        SELECT p.proDescripcion AS nombre, pi.proInvStockActual AS stockActual
        FROM productoinventario pi
        JOIN producto p ON p.proId = pi.proId
        WHERE pi.invId = (
            SELECT MAX(invId) FROM inventario
        )
        ORDER BY pi.proInvStockActual ASC
        LIMIT 1
        """, nativeQuery = true)
    Object obtenerProductoConMenorStock();
}
