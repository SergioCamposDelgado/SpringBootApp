package com.optativa.thymeleaf.entidad;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LineaPedido {

    @NotNull(message = "Debes seleccionar un producto")
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que 0")
    @Min(value = 1, message = "Mínimo 1 unidad")
    private Integer cantidad;

    // Constructor vacío
    public LineaPedido() {
        this.cantidad = null;  // Opcional, pero ayuda
    }

    // Constructor con parámetros (ajustado)
    public LineaPedido(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return producto != null ? producto.getPrecio() : 0.0;
    }

    public double getSubtotal() {
        return (cantidad != null ? cantidad : 0) * getPrecioUnitario();
    }
}