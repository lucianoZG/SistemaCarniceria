package com.practicaprof.carniceria.dto;

import lombok.Data;

@Data
public class CarritoDto {

    private Long idProducto;
    private String descripcion;
    private double totalCantidad;
    private double precioUnitActual;
    private double precioTotalProducto;
}
