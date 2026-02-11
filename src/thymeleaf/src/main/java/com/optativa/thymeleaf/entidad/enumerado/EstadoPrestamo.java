package com.optativa.thymeleaf.entidad.enumerado;

/**
 * Enumerado que define los estados posibles de un **Préstamo** en el sistema.
 * * Uso:
 * - Permite controlar el ciclo de vida de un préstamo desde que se crea hasta que finaliza.
 * - Facilita la lógica de filtrado en la base de datos y la visualización en Thymeleaf.
 */
public enum EstadoPrestamo {

    /**
     * El libro está actualmente en posesión del usuario y el plazo no ha expirado.
     */
    ACTIVO,

    /**
     * El libro ha sido retornado a la biblioteca correctamente.
     * Este estado finaliza la responsabilidad del usuario sobre el ejemplar.
     */
    DEVUELTO,

    /**
     * La fecha límite de devolución ha pasado y el libro aún no ha sido entregado.
     * Útil para sistemas de alertas, multas o notificaciones automáticas.
     */
    VENCIDO,

    /**
     * El préstamo fue anulado antes de hacerse efectivo o por un error administrativo.
     */
    CANCELADO
}