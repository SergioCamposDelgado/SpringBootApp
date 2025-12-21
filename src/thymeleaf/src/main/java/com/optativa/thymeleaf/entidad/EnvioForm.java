package com.optativa.thymeleaf.entidad;

import jakarta.validation.constraints.*;

public class EnvioForm {

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que 0")
    @DecimalMax(value = "100.0", message = "El peso máximo permitido es 100 kg")
    private Double peso;

    @NotBlank(message = "Debes seleccionar un destino")
    private String destino;

    private boolean urgente;

    // Constructores
    public EnvioForm() {}

    // Getters y Setters
    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }
}