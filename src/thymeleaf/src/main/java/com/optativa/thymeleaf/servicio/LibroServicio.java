package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroServicio {

    List<Libro> obtenerTodosLosLibros();

    Optional<Libro> obtenerLibroPorId(Integer id);

    Libro guardarLibro(Libro libro);

    void eliminarLibro(Integer id);

    boolean existeLibroPorId(Integer id);

    // Puedes añadir más métodos cuando necesites (ej: buscar por título, por categoría, disponibles, etc.)
}