package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Autor;
import com.optativa.thymeleaf.repositorio.AutorRepositorio;
import com.optativa.thymeleaf.servicio.AutorServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class AutorServicioImpl implements AutorServicio {

    private final AutorRepositorio autorRepositorio;

    public AutorServicioImpl(AutorRepositorio autorRepositorio) {
        this.autorRepositorio = autorRepositorio;
    }

    @Override
    public List<Autor> obtenerTodosLosAutores() {
        return autorRepositorio.findAll();
    }

    @Override
    public Optional<Autor> obtenerAutorPorId(Integer id) {
        return autorRepositorio.findById(id);
    }

    @Override
    public Autor guardarAutor(Autor autor) {
        return autorRepositorio.save(autor);
    }

    @Override
    public void eliminarAutor(Integer id) {
        if (!autorRepositorio.existsById(id)) {
            throw new IllegalArgumentException("El autor con ID " + id + " no existe");
        }
        autorRepositorio.deleteById(id);
    }
}
