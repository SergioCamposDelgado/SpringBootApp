package com.optativa.thymeleaf.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Entidad que representa un **Préstamo** de un libro a un usuario.
 * 
 * Registra:
 *  - Fechas clave del préstamo
 *  - Estado actual (ACTIVO, DEVUELTO, VENCIDO, CANCELADO)
 *  - Relación con el libro prestado y el usuario que lo solicitó
 * 
 * Esta es una de las entidades centrales del negocio (junto con Libro y Usuario).
 */
@Entity
// @Table(name = "prestamos")   // opcional – por defecto usa el nombre de la clase
public class Prestamo {

    /**
     * Clave primaria auto-generada
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * Fecha en la que se realizó el préstamo (cuando se solicitó/aprobó)
     * - Obligatoria
     */
    @NotNull(message = "La fecha de préstamo es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    /**
     * Fecha máxima prevista para la devolución
     * - Obligatoria
     * - Normalmente se calcula como: fechaPrestamo + díasPermitidos (ej: +14 o +21 días)
     */
    @NotNull(message = "La fecha de devolución prevista es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaDevolucionPrevista;

    /**
     * Fecha real en la que el libro fue devuelto
     * - Puede ser null mientras el préstamo esté activo o vencido
     */
    private LocalDate fechaDevolucionReal;

    /**
     * Estado actual del préstamo
     * 
     * Usamos @Enumerated(EnumType.STRING) → guarda el nombre del enum como texto
     * (más legible y seguro que ORDINAL en la base de datos)
     * 
     * Valor por defecto: ACTIVO (al crearse un nuevo préstamo)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    /**
     * Enum que define los posibles estados de un préstamo
     * 
     * - ACTIVO: préstamo en curso, libro prestado
     * - DEVUELTO: devuelto a tiempo o con retraso
     * - VENCIDO: pasado la fechaDevolucionPrevista y aún no devuelto
     * - CANCELADO: anulado antes de ser entregado (ej: usuario desistió)
     */
    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, VENCIDO, CANCELADO
    }

    // ────────────────────────────────────────────────────────────────
    // Relaciones
    // ────────────────────────────────────────────────────────────────

    /**
     * Relación ManyToOne con Libro
     * - Un préstamo siempre está asociado a **un solo libro**
     * - fetch = LAZY → no carga el libro automáticamente (mejora rendimiento)
     * - nullable = false → no puede existir préstamo sin libro
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    /**
     * Relación ManyToOne con Usuario
     * - Un préstamo siempre pertenece a **un solo usuario**
     * - fetch = LAZY → evita cargar todos los datos del usuario innecesariamente
     * - nullable = false → obligatorio
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ────────────────────────────────────────────────────────────────
    // Constructores
    // ────────────────────────────────────────────────────────────────
    public Prestamo() {
        // Constructor vacío → obligatorio para JPA
    }

    // Constructor útil muy común (puedes agregarlo):
    // public Prestamo(Libro libro, Usuario usuario, LocalDate fechaPrestamo, LocalDate fechaDevolucionPrevista) {
    //     this.libro = libro;
    //     this.usuario = usuario;
    //     this.fechaPrestamo = fechaPrestamo;
    //     this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    //     this.estado = EstadoPrestamo.ACTIVO;
    // }

    // ────────────────────────────────────────────────────────────────
    // Getters y Setters
    // ────────────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public void setFechaDevolucionPrevista(LocalDate fechaDevolucionPrevista) { 
        this.fechaDevolucionPrevista = fechaDevolucionPrevista; 
    }

    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { 
        this.fechaDevolucionReal = fechaDevolucionReal; 
    }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // ────────────────────────────────────────────────────────────────
    // Método de lógica de negocio – muy útil en vistas y servicios
    // ────────────────────────────────────────────────────────────────
    /**
     * Determina si el préstamo está actualmente vencido.
     * 
     * Regla de negocio:
     *   - Solo aplica si el estado es ACTIVO
     *   - Compara la fecha actual con la fechaDevolucionPrevista
     * 
     * @return true si está vencido, false en caso contrario
     */
    public boolean estaVencido() {
        return this.estado == EstadoPrestamo.ACTIVO &&
               LocalDate.now().isAfter(this.fechaDevolucionPrevista);
    }

    // ────────────────────────────────────────────────────────────────
    // toString útil para logs y debugging
    // ────────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", libro=" + (libro != null ? libro.getTitulo() : "null") +
                ", usuario=" + (usuario != null ? usuario.getNombreCompleto() : "null") +
                ", estado=" + estado +
                '}';
        // Nota: no se incluyen fechas completas para mantenerlo legible
    }
}