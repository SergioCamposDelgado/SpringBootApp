package com.optativa.thymeleaf.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Autor en la base de datos.
 * 
 * Un autor puede escribir múltiples libros (relación @OneToMany).
 * La relación está mapeada como bidireccional (Autor → Libro y Libro → Autor).
 */
@Entity                                      // Indica que esta clase es una entidad JPA → se mapea a una tabla
@Table(name = "autores")                     // Opcional: nombre explícito de la tabla (por defecto sería "autor")
public class Autor {

    /**
     * Clave primaria auto-incremental
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // AUTO → elige la mejor estrategia según la BD (IDENTITY en MySQL/PostgreSQL)
    private Integer id;

    /**
     * Nombre del autor
     * - Obligatorio
     * - Máximo 100 caracteres
     */
    @NotBlank(message = "El nombre es obligatorio")           // No permite null ni cadena vacía/blancos
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)                   // Refuerza en BD (aunque @NotBlank ya lo valida)
    private String nombre;

    /**
     * Apellido del autor
     * - Obligatorio
     * - Máximo 100 caracteres
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;

    /**
     * Fecha de nacimiento
     * - Debe ser una fecha pasada (no futura)
     * - Puede ser null (autor vivo o fecha desconocida)
     */
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    /**
     * Nacionalidad (opcional)
     * - Máximo 100 caracteres
     */
    @Size(max = 100, message = "La nacionalidad no puede exceder 100 caracteres")
    private String nacionalidad;

    /**
     * Relación bidireccional Uno-a-Muchos con Libro
     * 
     * mappedBy = "autor" → el dueño de la relación es el campo "autor" de la entidad Libro
     * cascade = CascadeType.ALL → operaciones en Autor se propagan a sus libros (persist, merge, remove, etc.)
     * orphanRemoval = true → si un libro se quita de la lista, se elimina de la BD (muy útil)
     */
    @OneToMany(mappedBy = "autor", 
               cascade = CascadeType.ALL, 
               orphanRemoval = true,
               fetch = FetchType.LAZY)               // LAZY por defecto en OneToMany → recomendado
    private List<Libro> libros = new ArrayList<>();  // Inicializar la colección evita NullPointerException

    // ────────────────────────────────────────────────────────────────
    // Constructores
    // ────────────────────────────────────────────────────────────────
    public Autor() {
        // Constructor vacío → obligatorio para JPA/Hibernate
    }

    // Puedes agregar un constructor útil para pruebas o creación rápida
    // public Autor(String nombre, String apellido) {
    //     this.nombre = nombre;
    //     this.apellido = apellido;
    // }

    // ────────────────────────────────────────────────────────────────
    // Getters y Setters
    // ────────────────────────────────────────────────────────────────
    // (generados automáticamente por IDE)

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }

    // ────────────────────────────────────────────────────────────────
    // Métodos helper para mantener la consistencia bidireccional
    // ────────────────────────────────────────────────────────────────
    /**
     * Añade un libro a este autor y establece la relación inversa
     * (mantiene la sincronización bidireccional)
     */
    public void addLibro(Libro libro) {
        this.libros.add(libro);
        libro.setAutor(this);           // ¡Importante! Evita inconsistencias en la relación
    }

    /**
     * Elimina un libro de este autor y rompe la relación inversa
     */
    public void removeLibro(Libro libro) {
        this.libros.remove(libro);
        libro.setAutor(null);
    }

    // ────────────────────────────────────────────────────────────────
    // toString (útil para logs y debugging)
    // ────────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                '}';
        // Nota: NO incluir "libros" aquí → evita recursión infinita si se imprime desde un libro
    }
}