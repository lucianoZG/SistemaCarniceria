package com.practicaprof.carniceria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proId")
    private int id;
    
    @Column(name = "proDescripcion")
    private String descripcion;
    
    @Column(name = "proPrecioUnitario")
    private double precioUnitario;
    
    @Column(name = "proCantidad")
    private double cantidad;
    
    @Column(name = "proEstado", nullable = false)
    private boolean estado = true;
}
