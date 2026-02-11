package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Categoria;
import com.optativa.thymeleaf.repositorio.CategoriaRepositorio;
import com.optativa.thymeleaf.servicio.CategoriaServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de negocio para la entidad **Categoria**.
 * * Esta clase centraliza la lógica de clasificación de los libros, permitiendo
 * gestionar géneros literarios o temáticas de forma aislada.
 */
@Primary
@Service
public class CategoriaServicioImpl implements CategoriaServicio {

    /**
     * Inyección del repositorio mediante constructor (Inmutabilidad garantizada).
     */
    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicioImpl(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    /**
     * Recupera el listado completo de categorías de la base de datos.
     */
    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepositorio.findAll();
    }

    /**
     * Busca una categoría por su ID. Retorna un Optional para manejar
     * la ausencia de datos de forma limpia en el controlador.
     */
    @Override
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepositorio.findById(id);
    }

    /**
     * Guarda una nueva categoría o actualiza una existente.
     * JPA detecta si el objeto ya tiene un ID para decidir entre INSERT o UPDATE.
     */
    @Override
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    /**
     * Elimina una categoría tras comprobar que existe.
     * @throws IllegalArgumentException si el ID es inválido.
     */
    @Override
    public void eliminarCategoria(Integer id) {
        if (!categoriaRepositorio.existsById(id)) {
            throw new IllegalArgumentException("La categoría con ID " + id + " no existe");
        }
        categoriaRepositorio.deleteById(id);
    }

    /**
     * Busca una categoría por su nombre ignorando mayúsculas/minúsculas.
     * * Nota técnica: Actualmente utiliza un Stream sobre la lista completa. 
     * Es funcional para catálogos pequeños, pero si el número de categorías crece,
     * se recomienda sustituir por un método findByNombreIgnoreCase en el Repositorio.
     */
    @Override
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        return categoriaRepositorio.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }
}