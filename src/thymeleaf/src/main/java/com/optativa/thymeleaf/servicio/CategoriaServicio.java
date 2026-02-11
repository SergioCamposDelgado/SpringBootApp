package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaServicio {

    List<Categoria> obtenerTodasLasCategorias();

    Optional<Categoria> obtenerCategoriaPorId(Integer id);

    Categoria guardarCategoria(Categoria categoria);

    void eliminarCategoria(Integer id);

    Optional<Categoria> obtenerPorNombre(String nombre);
}