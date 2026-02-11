package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.repositorio.LibroRepositorio;
import com.optativa.thymeleaf.servicio.LibroServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class LibroServicioImpl implements LibroServicio {

    private final LibroRepositorio libroRepositorio;

    public LibroServicioImpl(LibroRepositorio libroRepositorio) {
        this.libroRepositorio = libroRepositorio;
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepositorio.findAll();
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Integer id) {
        return libroRepositorio.findById(id);
    }

    @Override
    public Libro guardarLibro(Libro libro) {
        // Aquí podrías añadir validaciones adicionales de negocio si quieres
        // Ejemplo: if (libro.getIsbn() == null) throw new IllegalArgumentException("ISBN requerido");
        return libroRepositorio.save(libro);
    }

    @Override
    public void eliminarLibro(Integer id) {
        if (!existeLibroPorId(id)) {
            throw new IllegalArgumentException("El libro con ID " + id + " no existe");
        }
        libroRepositorio.deleteById(id);
    }

    @Override
    public boolean existeLibroPorId(Integer id) {
        return libroRepositorio.existsById(id);
    }
}