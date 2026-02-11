package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.entidad.Prestamo;
import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.repositorio.LibroRepositorio;
import com.optativa.thymeleaf.repositorio.PrestamoRepositorio;
import com.optativa.thymeleaf.repositorio.UsuarioRepositorio;
import com.optativa.thymeleaf.servicio.PrestamoServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Primary
@Service
public class PrestamoServicioImpl implements PrestamoServicio {

    private final PrestamoRepositorio prestamoRepositorio;
    private final LibroRepositorio libroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    // Duración estándar del préstamo (en días)
    private static final int DIAS_PRESTAMO = 14;

    public PrestamoServicioImpl(
            PrestamoRepositorio prestamoRepositorio,
            LibroRepositorio libroRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        this.prestamoRepositorio = prestamoRepositorio;
        this.libroRepositorio = libroRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public Prestamo solicitarPrestamo(Integer libroId, Integer usuarioId) {
        Libro libro = libroRepositorio.findById(libroId)
                .orElseThrow(() -> new IllegalStateException("El libro no existe"));

        if (!libro.getDisponible()) {
            throw new IllegalStateException("El libro no está disponible actualmente");
        }

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalStateException("El usuario no existe"));

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionPrevista(LocalDate.now().plusDays(DIAS_PRESTAMO));
        prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);

        // Marcar el libro como no disponible
        libro.setDisponible(false);
        libroRepositorio.save(libro);

        return prestamoRepositorio.save(prestamo);
    }

    @Override
    @Transactional
    public Prestamo devolverPrestamo(Integer prestamoId) {
        Prestamo prestamo = prestamoRepositorio.findById(prestamoId)
                .orElseThrow(() -> new IllegalStateException("El préstamo no existe"));

        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.ACTIVO) {
            throw new IllegalStateException("El préstamo ya fue devuelto o está en otro estado");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);

        // Marcar el libro como disponible nuevamente
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepositorio.save(libro);

        return prestamoRepositorio.save(prestamo);
    }

    @Override
    public List<Prestamo> obtenerTodosLosPrestamos() {
        return prestamoRepositorio.findAll();
    }

    @Override
    public Optional<Prestamo> obtenerPrestamoPorId(Integer id) {
        return prestamoRepositorio.findById(id);
    }

    @Override
    public List<Prestamo> obtenerPrestamosPorUsuario(Usuario usuario) {
        // Si no tienes findByUsuario en el repositorio, puedes implementarlo o usar:
        return prestamoRepositorio.findAll().stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .toList();
    }

    @Override
    public List<Prestamo> obtenerPrestamosActivos() {
        return prestamoRepositorio.findAll().stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
                .toList();
    }
}
