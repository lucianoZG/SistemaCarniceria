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
public class VentaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detId")
    private int id;
    
    @Column(name = "detPrecioTotalProducto")
    private double precioTotalProducto;
    
    @Column(name = "detTotalCantidad")
    private double totalCantidad;
    
    @Column(name = "detPrecioUnitActual")
    private double precioUnitActual;
    
    @Column(name = "detPrecioCostoActual")
    private double precioCostoActual;
    //Relaciones
    @ManyToOne
    @JoinColumn(name = "venId")
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "proId")
    private Producto producto;
    
}
