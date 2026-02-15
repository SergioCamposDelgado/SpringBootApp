package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.repositorio.LibroRepositorio;
import com.optativa.thymeleaf.servicio.LibroServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de negocio para la gestión de **Libros**.
 * * Esta clase actúa como el motor principal del catálogo, coordinando 
 * el almacenamiento, la disponibilidad y la validación de los ejemplares.
 */
@Primary
@Service
public class LibroServicioImpl implements LibroServicio {

    /**
     * Repositorio inyectado mediante constructor. 
     * Se utiliza la interfaz del repositorio para mantener el bajo acoplamiento.
     */
    private final LibroRepositorio libroRepositorio;

    public LibroServicioImpl(LibroRepositorio libroRepositorio) {
        this.libroRepositorio = libroRepositorio;
    }

    /**
     * Recupera el catálogo completo de libros.
     */
    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepositorio.findAll();
    }

    /**
     * Busca un libro por su ID único.
     * @return Un Optional que evita errores si el ID no existe en la BD.
     */
    @Override
    public Optional<Libro> obtenerLibroPorId(Integer id) {
        return libroRepositorio.findById(id);
    }

    /**
     * Persiste un libro en la base de datos.
     * * Nota de negocio: Antes de hacer el 'save', se podrían implementar 
     * reglas adicionales (ej: verificar que el ISBN no esté duplicado 
     * manualmente si no se confía solo en la restricción de BD).
     */
    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepositorio.save(libro);
    }

    /**
     * Elimina un libro tras verificar su existencia previa.
     * @throws IllegalArgumentException si se intenta eliminar algo inexistente.
     */
    @Override
    public void eliminarLibro(Integer id) {
        if (!existeLibroPorId(id)) {
            throw new IllegalArgumentException("El libro con ID " + id + " no existe");
        }
        libroRepositorio.deleteById(id);
    }

    /**
     * Método de utilidad para comprobaciones rápidas de existencia.
     */
    @Override
    public boolean existeLibroPorId(Integer id) {
        return libroRepositorio.existsById(id);
    }
    
    /**
     * Filtra el catálogo para devolver solo libros listos para préstamo.
     * * @return Lista de libros donde disponible = true.
     * Delega la consulta al método personalizado definido en el repositorio.
     */
    @Override
    public List<Libro> obtenerLibrosDisponibles() {
        return libroRepositorio.findByDisponibleTrue();
    }
    
    /**
     * Recupera el catálogo de libros de forma paginada.
     * @param pageable Objeto con la configuración de paginación (página, tamaño, orden).
     * @return Un objeto Page con los libros correspondientes a la solicitud.
     */
    @Override
    public Page<Libro> obtenerTodosLosLibros(Pageable pageable) {
        return libroRepositorio.findAll(pageable);
    }

    /**
     * Realiza una búsqueda paginada de libros cuyo título contenga la cadena especificada.
     * La búsqueda ignora mayúsculas y minúsculas.
     * @param titulo Texto a buscar dentro de los títulos de los libros.
     * @param pageable Configuración de la paginación.
     * @return Una página de libros que coinciden con el criterio de búsqueda.
     */
    @Override
    public Page<Libro> buscarPorTitulo(String titulo, Pageable pageable) {
        return libroRepositorio.findByTituloContainingIgnoreCase(titulo, pageable);
    }
    
    /**
     * Localiza un libro en la base de datos utilizando su código ISBN único.
     * @param isbn El ISBN del libro a buscar.
     * @return Un Optional que contiene el libro si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Libro> obtenerLibroPorIsbn(String isbn) {
        return libroRepositorio.findByIsbn(isbn); 
    }
}