package com.optativa.thymeleaf.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad central del sistema: representa un **Libro** en la biblioteca.
 * 
 * Campos principales:
 *  - ISBN (único)
 *  - Título
 *  - Año de publicación
 *  - Sinopsis
 *  - Disponibilidad (para préstamos)
 *  
 * Relaciones:
 *  - ManyToOne con Autor   (un libro tiene UN autor)
 *  - ManyToMany con Categoría (un libro puede tener varias categorías)
 */
@Entity
// @Table(name = "libros")   // opcional – por defecto usa el nombre de la clase
public class Libro {

    /**
     * Clave primaria auto-generada
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // AUTO → elige IDENTITY o SEQUENCE según la BD
    private Integer id;

    /**
     * ISBN del libro
     * - Obligatorio
     * - Entre 10 y 13 caracteres (cubriendo ISBN-10 e ISBN-13)
     * - Único en toda la base de datos
     */
    @NotBlank(message = "El ISBN es obligatorio")
    @Size(min = 10, max = 13, message = "El ISBN debe tener entre 10 y 13 caracteres")
    @Column(unique = true, nullable = false)
    private String isbn;

    /**
     * Título del libro
     * - Obligatorio
     * - Máximo 200 caracteres (suficiente para títulos largos)
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    /**
     * Año de publicación
     * - Rango razonable: 1500 → 2100
     * - Evita años ilógicos o futuros lejanos
     */
    @Min(value = 1500, message = "El año debe ser posterior a 1500")
    @Max(value = 2100, message = "El año no puede ser futuro")
    private Integer añoPublicacion;

    /**
     * Sinopsis o descripción del libro
     * - Opcional
     * - Máximo 1000 caracteres (aprox 150–200 palabras)
     */
    @Size(max = 1000, message = "La sinopsis no puede exceder 1000 caracteres")
    @Lob    // ← Muy recomendado si la sinopsis puede ser larga
            //   (en muchas BD cambia el tipo a TEXT o CLOB)
    private String sinopsis;

    /**
     * Indica si el libro está disponible para préstamo
     * - Por defecto = true
     * - Campo obligatorio (aunque casi siempre se inicializa)
     */
    @NotNull(message = "El estado de disponibilidad es obligatorio")
    @Column(nullable = false)
    private Boolean disponible = true;

    // ────────────────────────────────────────────────────────────────
    // Relación con Autor → ManyToOne (lado propietario)
    // ────────────────────────────────────────────────────────────────
    /**
     * Un libro pertenece a **un solo autor**
     * 
     * fetch = LAZY → no carga el autor automáticamente al consultar un libro
     *                 (mejora rendimiento – solo se carga cuando se necesita)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")   // nombre de la FK en la tabla libros
    private Autor autor;

    // ────────────────────────────────────────────────────────────────
    // Relación con Categorías → ManyToMany (lado propietario)
    // ────────────────────────────────────────────────────────────────
    /**
     * Un libro puede pertenecer a varias categorías
     * y una categoría puede tener muchos libros
     * 
     * @JoinTable → define explícitamente la tabla intermedia
     *             (muy buena práctica – da control sobre nombres y columnas)
     */
    @ManyToMany
    @JoinTable(
        name = "libro_categoria",                    // nombre de la tabla pivot
        joinColumns = @JoinColumn(name = "libro_id"),         // FK hacia Libro
        inverseJoinColumns = @JoinColumn(name = "categoria_id") // FK hacia Categoría
    )
    private Set<Categoria> categorias = new HashSet<>();

    // ────────────────────────────────────────────────────────────────
    // Constructores
    // ────────────────────────────────────────────────────────────────
    public Libro() {
        // Constructor vacío → **obligatorio** para JPA/Hibernate
    }

    // Puedes agregar constructores útiles, por ejemplo:
    // public Libro(String isbn, String titulo, Autor autor) { ... }

    // ────────────────────────────────────────────────────────────────
    // Getters y Setters
    // ────────────────────────────────────────────────────────────────
    // (generados automáticamente)

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getAñoPublicacion() { return añoPublicacion; }
    public void setAñoPublicacion(Integer añoPublicacion) { this.añoPublicacion = añoPublicacion; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public Set<Categoria> getCategorias() { return categorias; }
    public void setCategorias(Set<Categoria> categorias) { this.categorias = categorias; }

    // ────────────────────────────────────────────────────────────────
    // Métodos helper – **muy importantes** en relaciones bidireccionales
    // ────────────────────────────────────────────────────────────────
    /**
     * Añade una categoría al libro y mantiene la relación inversa
     * (evita inconsistencias en la base de datos)
     */
    public void addCategoria(Categoria categoria) {
        this.categorias.add(categoria);
        categoria.getLibros().add(this);    // ← sincroniza el otro lado
    }

    /**
     * Elimina una categoría del libro y rompe la relación inversa
     */
    public void removeCategoria(Categoria categoria) {
        this.categorias.remove(categoria);
        categoria.getLibros().remove(this);
    }

    // ────────────────────────────────────────────────────────────────
    // toString seguro – sin colecciones para evitar recursión infinita
    // ────────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", añoPublicacion=" + añoPublicacion +
                ", disponible=" + disponible +
                '}';
        // Nota: NO incluir autor ni categorias aquí
    }
}