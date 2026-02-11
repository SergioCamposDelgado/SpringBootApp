package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Autor;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define el contrato de servicios para la entidad **Autor**.
 * * Propósito:
 * - Actuar como capa intermedia entre el Controlador y el Repositorio.
 * - Centralizar las reglas de negocio relacionadas con los autores.
 * - Desacoplar la implementación técnica (JPA) de la definición de los servicios.
 */
public interface AutorServicio {

    /**
     * Recupera la lista completa de autores registrados.
     * @return List de entidades Autor.
     */
    List<Autor> obtenerTodosLosAutores();

    /**
     * Busca un autor específico por su identificador único.
     * @param id Identificador del autor.
     * @return Un **Optional** para gestionar de forma segura la posibilidad de que no exista.
     */
    Optional<Autor> obtenerAutorPorId(Integer id);

    /**
     * Registra un nuevo autor o actualiza uno existente en el sistema.
     * @param autor Objeto con los datos a persistir.
     * @return El autor guardado (incluyendo su ID generado si es nuevo).
     */
    Autor guardarAutor(Autor autor);

    /**
     * Elimina de forma definitiva un autor del sistema.
     * @param id Identificador del autor a borrar.
     */
    void eliminarAutor(Integer id);
}