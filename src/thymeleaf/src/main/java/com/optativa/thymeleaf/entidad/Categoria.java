package com.optativa.thymeleaf.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una Categoría (género o temática) de libros.
 * 
 * Ejemplos: Novela, Ciencia Ficción, Fantasía, Misterio, etc.
 * 
 * Relación: Many-to-Many con Libro
 *   - Un libro puede pertenecer a varias categorías
 *   - Una categoría puede estar asociada a muchos libros
 * 
 * La relación está mapeada como **bidireccional**, pero el dueño es el lado Libro (campo "categorias")
 */
@Entity
@Table(name = "categorias")  // opcional, por defecto usa el nombre de la clase
public class Categoria {

    /**
     * Clave primaria auto-generada
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // AUTO → elige IDENTITY o SEQUENCE según la BD
    private Integer id;

    /**
     * Nombre de la categoría (ej: "Ciencia Ficción", "Histórica")
     * 
     * Restricciones:
     * - Obligatorio (no null, no vacío, no solo espacios)
     * - Máximo 80 caracteres
     * - Único en toda la tabla (no puede haber dos categorías con el mismo nombre)
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    @Column(unique = true, nullable = false, length = 80)
    private String nombre;

    /**
     * Relación ManyToMany inversa (lado no propietario)
     * 
     * mappedBy = "categorias" → indica que el dueño de la relación está en el campo "categorias"
     *                           de la entidad Libro
     * 
     * Usamos Set (HashSet) en lugar de List porque:
     * - No hay orden natural en las categorías de un libro
     * - Evita duplicados automáticamente
     * - Mejor rendimiento en relaciones ManyToMany
     * 
     * fetch = FetchType.LAZY por defecto en @ManyToMany → recomendado
     */
    @ManyToMany(mappedBy = "categorias")
    private Set<Libro> libros = new HashSet<>();

    // ────────────────────────────────────────────────────────────────
    // Constructores
    // ────────────────────────────────────────────────────────────────
    public Categoria() {
        // Constructor vacío → obligatorio para JPA / Hibernate
    }

    /**
     * Constructor conveniente para creación rápida (muy usado al inicializar datos)
     */
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    // ────────────────────────────────────────────────────────────────
    // Getters y Setters
    // ────────────────────────────────────────────────────────────────
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Libro> getLibros() {
        return libros;
    }

    public void setLibros(Set<Libro> libros) {
        this.libros = libros;
    }

    // ────────────────────────────────────────────────────────────────
    // Métodos helper (opcionales pero recomendados en relaciones bidireccionales)
    // ────────────────────────────────────────────────────────────────
    /*
    // Ejemplo de métodos para mantener consistencia (agregar/quitar desde este lado)
    // No son estrictamente necesarios porque el dueño es Libro, pero ayudan en algunos casos

    public void addLibro(Libro libro) {
        this.libros.add(libro);
        libro.getCategorias().add(this);
    }

    public void removeLibro(Libro libro) {
        this.libros.remove(libro);
        libro.getCategorias().remove(this);
    }
    */

    // ────────────────────────────────────────────────────────────────
    // toString seguro (sin incluir la colección libros)
    // ────────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
        // Nota importante: NO incluir "libros" aquí → evita recursión infinita
        // si se imprime desde un Libro que referencia esta categoría
    }
}