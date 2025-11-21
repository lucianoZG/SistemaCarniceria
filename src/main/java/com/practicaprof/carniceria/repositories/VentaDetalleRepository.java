/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.entities.VentaDetalle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Integer> {

    @Query(value = """
        SELECT p.proDescripcion AS nombre, SUM(vd.detTotalCantidad) AS totalVendido
        FROM ventadetalle vd
        JOIN producto p ON p.proId = vd.proId
        JOIN venta v ON v.venId = vd.venId
        WHERE v.venFechaHora >= DATE_SUB(NOW(), INTERVAL 1 MONTH)
        GROUP BY p.proId
        ORDER BY totalVendido DESC
        LIMIT 1
    """, nativeQuery = true)
    Object obtenerProductoMasVendidoUltimoMes();
    
    List<VentaDetalle> findByProductoId(int productoId);
    
    @Query("SELECT DISTINCT vd.producto FROM VentaDetalle vd")
    List<Producto> findDistinctProductosVendidos();

}
