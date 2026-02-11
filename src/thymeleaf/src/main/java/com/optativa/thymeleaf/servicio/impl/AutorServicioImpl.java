package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Autor;
import com.optativa.thymeleaf.repositorio.AutorRepositorio;
import com.optativa.thymeleaf.servicio.AutorServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de los servicios para **Autor**.
 * * Anotaciones:
 * - @Service: Indica que esta clase es un componente de servicio gestionado por Spring.
 * - @Primary: En caso de existir múltiples implementaciones de AutorServicio, 
 * esta será la preferida para la inyección de dependencias.
 */
@Primary
@Service
public class AutorServicioImpl implements AutorServicio {

    /**
     * Referencia al repositorio (Capa de Datos).
     * Se usa 'final' para garantizar la inmutabilidad y fomentar 
     * la inyección por constructor.
     */
    private final AutorRepositorio autorRepositorio;

    /**
     * Inyección de dependencias por constructor.
     * Es la forma recomendada por Spring, ya que facilita las pruebas unitarias
     * y asegura que el servicio no se cree sin sus dependencias.
     */
    public AutorServicioImpl(AutorRepositorio autorRepositorio) {
        this.autorRepositorio = autorRepositorio;
    }

    /**
     * Recupera todos los autores delegando la tarea al repositorio.
     */
    @Override
    public List<Autor> obtenerTodosLosAutores() {
        return autorRepositorio.findAll();
    }

    /**
     * Busca un autor por su ID.
     * Retorna un Optional que el controlador deberá gestionar (ifPresent o orElse).
     */
    @Override
    public Optional<Autor> obtenerAutorPorId(Integer id) {
        return autorRepositorio.findById(id);
    }

    /**
     * Persiste un autor. 
     * Si el objeto trae un ID que ya existe en la BD, JPA realizará un UPDATE.
     * Si el ID es nulo, realizará un INSERT.
     */
    @Override
    public Autor guardarAutor(Autor autor) {
        return autorRepositorio.save(autor);
    }

    /**
     * Elimina un autor tras validar su existencia.
     * * @throws IllegalArgumentException si el ID no se encuentra en la base de datos,
     * evitando errores silenciosos o excepciones genéricas de SQL.
     */
    @Override
    public void eliminarAutor(Integer id) {
        if (!autorRepositorio.existsById(id)) {
            throw new IllegalArgumentException("El autor con ID " + id + " no existe");
        }
        autorRepositorio.deleteById(id);
    }
}