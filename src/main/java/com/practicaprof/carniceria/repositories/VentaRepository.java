package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Venta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT SUM(v.precioTotal) FROM Venta v WHERE DATE(v.fechaHora) = CURRENT_DATE")
    Double obtenerGananciasDelDia();
}
