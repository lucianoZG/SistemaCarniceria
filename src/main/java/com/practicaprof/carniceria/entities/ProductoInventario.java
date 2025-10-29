package com.practicaprof.carniceria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proInvId")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "proId", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "invId", nullable = false)
    private Inventario inventario;

    @Column(name = "proInvStockActual")
    private double stockActual;
    
    @Column(name = "proInvStockRelevado")
    private double stockRelevado;

    public ProductoInventario(Producto producto, Inventario inventario, double stockActual, double stockRelevado) {
        this.producto = producto;
        this.inventario = inventario;
        this.stockActual = stockActual;
        this.stockRelevado = stockRelevado;
    }
    
}
