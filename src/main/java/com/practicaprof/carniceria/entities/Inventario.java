package com.practicaprof.carniceria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invId")
    private int id;

    @Column(name = "invStockRelevado")
    private double stockRelevado;

    @Column(name = "invFecha")
    private LocalDateTime fecha;

    //Relacion con producto
    @OneToMany(mappedBy = "inventario")
    private List<ProductoInventario> productoInventarios;

}
