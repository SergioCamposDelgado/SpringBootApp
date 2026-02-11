package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Libro;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de negocio para la entidad **Libro**.
 * * Propósito:
 * - Gestionar el inventario de la biblioteca.
 * - Servir como puente entre los controladores y el repositorio de libros.
 * - Exponer métodos para el filtrado de libros según su estado.
 */
public interface LibroServicio {

    /**
     * Obtiene el catálogo completo de libros de la base de datos.
     * @return Lista con todos los objetos Libro.
     */
    List<Libro> obtenerTodosLosLibros();

    /**
     * Recupera un libro específico mediante su identificador.
     * @param id El ID del libro a buscar.
     * @return Un Optional que encapsula el libro si es encontrado.
     */
    Optional<Libro> obtenerLibroPorId(Integer id);

    /**
     * Procesa el guardado (creación o actualización) de un libro.
     * @param libro La entidad con los datos a persistir.
     * @return El libro guardado.
     */
    Libro guardarLibro(Libro libro);

    /**
     * Elimina un libro del sistema de forma permanente.
     * @param id ID del libro a eliminar.
     */
    void eliminarLibro(Integer id);

    /**
     * Verifica la existencia de un libro antes de realizar operaciones críticas.
     * @param id ID del libro a comprobar.
     * @return true si el libro existe, false en caso contrario.
     */
    boolean existeLibroPorId(Integer id);

    /**
     * Recupera únicamente los libros que están marcados como disponibles.
     * * Es fundamental para la vista del usuario final (lector), 
     * evitando que se intenten prestar libros que ya están fuera.
     * @return Lista de libros con estado disponible = true.
     */
    List<Libro> obtenerLibrosDisponibles();
}