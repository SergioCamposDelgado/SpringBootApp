package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Categoria;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define el contrato de servicios para la gestión de **Categorías**.
 * * Responsabilidades:
 * - Proveer la lógica para organizar el catálogo de libros (géneros, temas, etc.).
 * - Gestionar la integridad de los nombres de las categorías.
 */
public interface CategoriaServicio {

    /**
     * Obtiene el listado completo de categorías disponibles.
     * @return List de entidades Categoria.
     */
    List<Categoria> obtenerTodasLasCategorias();

    /**
     * Busca una categoría por su ID único.
     * @param id Identificador de la categoría.
     * @return Un Optional que permite gestionar búsquedas fallidas sin errores de puntero nulo.
     */
    Optional<Categoria> obtenerCategoriaPorId(Integer id);

    /**
     * Crea una nueva categoría o actualiza una existente.
     * @param categoria Objeto con los datos de la categoría.
     * @return La categoría persistida.
     */
    Categoria guardarCategoria(Categoria categoria);

    /**
     * Elimina una categoría del sistema por su ID.
     * @param id Identificador de la categoría a borrar.
     */
    void eliminarCategoria(Integer id);

    /**
     * Busca una categoría específica por su nombre exacto.
     * * Útil para validaciones antes de crear nuevas categorías o para 
     * búsquedas rápidas desde la interfaz de usuario.
     * @param nombre El nombre de la categoría (ej. "Ciencia Ficción").
     * @return Un Optional con la categoría encontrada.
     */
    Optional<Categoria> obtenerPorNombre(String nombre);
}