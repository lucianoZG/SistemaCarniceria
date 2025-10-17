package com.practicaprof.carniceria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Empleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empId")
    private int id;
    
    @NonNull
    @Column(name = "empNombre")
    private String nombre;
    
    @NonNull
    @Column(name = "empDni")
    private String dni;
    
    @NonNull
    @Column(name = "empDireccion")
    private String direccion;
    
    @NonNull
    @Column(name = "empTelefono")
    private String telefono;
    
    @Column(name = "empEstado")
    private boolean estado;
    
}
