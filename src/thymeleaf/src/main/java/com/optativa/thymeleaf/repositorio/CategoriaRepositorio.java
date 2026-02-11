package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de las operaciones de persistencia para la entidad **Categoria**.
 * * Al extender de **JpaRepository**, esta interfaz hereda toda la potencia de Spring Data JPA:
 * - Guardado, actualización y eliminación de categorías.
 * - Consultas paginadas y ordenadas por defecto.
 * - Conversión automática de excepciones SQL a excepciones de Spring.
 */
@Repository
public interface CategoriaRepositorio extends JpaRepository<Categoria, Integer> {

    /*
     * Nota de arquitectura:
     * Al ser una relación ManyToMany con la entidad Libro, este repositorio
     * permite gestionar las etiquetas o géneros (Terror, Drama, Ciencia Ficción)
     * de forma independiente antes de asociarlas a un ejemplar.
     */
     
}