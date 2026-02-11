package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Autor;

import java.util.List;
import java.util.Optional;

public interface AutorServicio {

    List<Autor> obtenerTodosLosAutores();

    Optional<Autor> obtenerAutorPorId(Integer id);

    Autor guardarAutor(Autor autor);

    void eliminarAutor(Integer id);
}