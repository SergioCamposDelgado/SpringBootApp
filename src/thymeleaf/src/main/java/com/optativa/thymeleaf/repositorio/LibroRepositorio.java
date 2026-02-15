package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Libro;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio especializado en la gestión de la entidad **Libro**.
 * * Además de las funciones CRUD estándar de JpaRepository, este repositorio
 * permite realizar consultas personalizadas basadas en el estado del catálogo.
 */
@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Integer> {

    /**
     * Consulta derivada (Query Method) para filtrar el catálogo.
     * * @return Una lista de libros cuyo campo 'disponible' sea true.
     * Útil para mostrar solo ejemplares que pueden ser prestados en la vista pública.
     */
    List<Libro> findByDisponibleTrue();
    
    /*
     * Nota: Spring Data JPA interpreta el nombre del método para generar 
     * automáticamente la consulta SQL: "SELECT * FROM libros WHERE disponible = 1"
     */
    
    // Para listar solo disponibles con paginación
    Page<Libro> findByDisponibleTrue(Pageable pageable);

    // Si quieres buscar por título con paginación
    Page<Libro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    
    /**
     * Comprueba si existe algún libro registrado con el ISBN proporcionado.
     * @param isbn ISBN a verificar.
     * @return true si el ISBN ya existe en la base de datos, false de lo contrario.
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Consulta un libro por su campo ISBN.
     * @param isbn ISBN del libro.
     * @return Optional con el libro encontrado.
     */
    Optional<Libro> findByIsbn(String isbn);
}