package com.practicaprof.carniceria.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venId")
    private int id;
    
    @Column(name = "venFechaHora")
    private LocalDateTime fechaHora;
    
    @Column(name = "venPrecioTotal")
    private double precioTotal;
    
    @Column(name = "venNroFactura", length = 50)
    private String nroFactura;
    
    //Relaciones
    @ManyToOne
    @JoinColumn(name = "usId")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "empId")
    private Empleado empleado;
    
    //Relacion con ventaDetalle
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> listaVentaDetalle = new ArrayList<>();
    
}
