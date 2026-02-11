package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Categoria;
import com.optativa.thymeleaf.repositorio.CategoriaRepositorio;
import com.optativa.thymeleaf.servicio.CategoriaServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class CategoriaServicioImpl implements CategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicioImpl(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepositorio.findAll();
    }

    @Override
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepositorio.findById(id);
    }

    @Override
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    @Override
    public void eliminarCategoria(Integer id) {
        if (!categoriaRepositorio.existsById(id)) {
            throw new IllegalArgumentException("La categoría con ID " + id + " no existe");
        }
        categoriaRepositorio.deleteById(id);
    }

    @Override
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        // Si más adelante añades findByNombre al repositorio, cámbialo aquí
        return categoriaRepositorio.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }
}
