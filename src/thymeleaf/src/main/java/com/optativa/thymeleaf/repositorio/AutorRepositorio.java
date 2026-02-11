package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de persistencia de la entidad **Autor**.
 * * Al extender de **JpaRepository**, obtenemos automáticamente:
 * - Operaciones CRUD básicas (save, findById, delete, etc.)
 * - Soporte para paginación y ordenamiento.
 * - Integración directa con Hibernate para el mapeo objeto-relacional.
 */
@Repository
public interface AutorRepositorio extends JpaRepository<Autor, Integer> {
    
    /* * Nota: No es necesario implementar métodos básicos. 
     * Spring Data JPA genera la implementación en tiempo de ejecución.
     * * Si en el futuro se requiere buscar autores por nombre o nacionalidad,
     * se pueden añadir Query Methods aquí, por ejemplo:
     * List<Autor> findByNombreContainingIgnoreCase(String nombre);
     */

}