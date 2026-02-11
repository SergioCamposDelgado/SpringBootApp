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

/**
 * Implementación de la lógica de negocio para los **Préstamos**.
 * * Esta clase coordina tres repositorios diferentes para asegurar que el flujo
 * de préstamo y devolución sea consistente y seguro.
 */
@Primary
@Service
public class PrestamoServicioImpl implements PrestamoServicio {

    private final PrestamoRepositorio prestamoRepositorio;
    private final LibroRepositorio libroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Política de la biblioteca: Los libros se prestan por un máximo de 14 días.
     */
    private static final int DIAS_PRESTAMO = 14;

    public PrestamoServicioImpl(
            PrestamoRepositorio prestamoRepositorio,
            LibroRepositorio libroRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        this.prestamoRepositorio = prestamoRepositorio;
        this.libroRepositorio = libroRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Proceso de solicitud de préstamo.
     * @Transactional: Asegura que el cambio de estado del libro y la creación 
     * del préstamo ocurran como una única operación atómica.
     */
    @Override
    @Transactional
    public Prestamo solicitarPrestamo(Integer libroId, Integer usuarioId) {
        // 1. Validar existencia del libro
        Libro libro = libroRepositorio.findById(libroId)
                .orElseThrow(() -> new IllegalStateException("El libro no existe"));

        // 2. Validar disponibilidad
        if (!libro.getDisponible()) {
            throw new IllegalStateException("El libro no está disponible actualmente");
        }

        // 3. Validar existencia del usuario
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalStateException("El usuario no existe"));

        // 4. Crear y configurar la instancia de Prestamo
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionPrevista(LocalDate.now().plusDays(DIAS_PRESTAMO));
        prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);

        // 5. Actualizar estado del libro (Efecto colateral necesario)
        libro.setDisponible(false);
        libroRepositorio.save(libro);

        return prestamoRepositorio.save(prestamo);
    }

    /**
     * Proceso de devolución de un ejemplar.
     * Restablece la disponibilidad del libro y cierra el ciclo del préstamo.
     */
    @Override
    @Transactional
    public Prestamo devolverPrestamo(Integer prestamoId) {
        Prestamo prestamo = prestamoRepositorio.findById(prestamoId)
                .orElseThrow(() -> new IllegalStateException("El préstamo no existe"));

        // MODIFICACIÓN: Permitir ACTIVO o VENCIDO
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.ACTIVO && 
            prestamo.getEstado() != Prestamo.EstadoPrestamo.VENCIDO) {
            throw new IllegalStateException("El préstamo no se puede devolver porque ya está " + prestamo.getEstado());
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);

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
        return prestamoRepositorio.findByUsuario(usuario);
    }

    // ────────────────────────────────────────────────────────────────
    // Métodos de Administración (ADMIN)
    // ────────────────────────────────────────────────────────────────

    /**
     * Permite a un administrador forzar la creación de un préstamo.
     */
    @Override
    @Transactional
    public Prestamo crearPrestamoManual(Prestamo prestamo) {
        Libro libro = libroRepositorio.findById(prestamo.getLibro().getId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (!libro.getDisponible()) {
            throw new IllegalStateException("El libro no está disponible");
        }

        usuarioRepositorio.findById(prestamo.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionPrevista(LocalDate.now().plusDays(DIAS_PRESTAMO));
        prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);

        libro.setDisponible(false);
        libroRepositorio.save(libro);

        return prestamoRepositorio.save(prestamo);
    }

    /**
     * Permite editar condiciones de un préstamo existente (como prórrogas).
     */
    @Override
    @Transactional
    public Prestamo actualizarPrestamo(Prestamo prestamo) {
        Prestamo existente = prestamoRepositorio.findById(prestamo.getId())
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        // Se actualiza la fecha prevista (ej: para una extensión de plazo)
        existente.setFechaDevolucionPrevista(prestamo.getFechaDevolucionPrevista());

        return prestamoRepositorio.save(existente);
    }

    /**
     * Cancela un préstamo activo (por error administrativo o anulación).
     * Devuelve el libro a estado disponible sin registrar una devolución exitosa.
     */
    @Override
    @Transactional
    public void cancelarPrestamo(Integer id) {
        Prestamo prestamo = prestamoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.ACTIVO) {
            throw new IllegalStateException("Solo se pueden cancelar préstamos activos");
        }

        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepositorio.save(libro);

        prestamo.setEstado(Prestamo.EstadoPrestamo.CANCELADO);
        prestamoRepositorio.save(prestamo);
    }
    
    // ────────────────────────────────────────────────────────────────
    // Operaciones de Guardado (Iniciar Datos)
    // ────────────────────────────────────────────────────────────────
    
    /**
     * Este método guarda el objeto tal cual viene, 
     * permitiendo que IniciarDatos setee fechas del pasado.
     */
    @Override
    @Transactional
    public Prestamo guardarPrestamo(Prestamo prestamo) {
        return prestamoRepositorio.save(prestamo);
    }
}