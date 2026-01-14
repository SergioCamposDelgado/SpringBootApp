package com.optativa.thymeleaf.entidad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class PedidoForm {

    @NotEmpty(message = "Debe haber al menos una línea de pedido")
    @Valid  // Valida cada elemento de la lista
    private List<LineaPedido> lineas = new ArrayList<>();


    // Constructores
    public PedidoForm() {}

    // Getters y Setters
    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaPedido> lineas) {
        this.lineas = lineas;
    }
    

    // Método para calcular total del pedido
    public double getTotal() {
        return lineas.stream().mapToDouble(LineaPedido::getSubtotal).sum();
    }
}