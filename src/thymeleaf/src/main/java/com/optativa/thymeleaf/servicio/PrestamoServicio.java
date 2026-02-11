package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Prestamo;
import com.optativa.thymeleaf.entidad.Usuario;

import java.util.List;
import java.util.Optional;

public interface PrestamoServicio {

    /**
     * Solicita un préstamo para un libro específico por parte de un usuario
     * @return el préstamo creado si fue exitoso
     * @throws IllegalStateException si el libro no existe o no está disponible
     */
    Prestamo solicitarPrestamo(Integer libroId, Integer usuarioId);

    /**
     * Marca un préstamo como devuelto
     * @throws IllegalStateException si el préstamo no existe o ya está devuelto
     */
    Prestamo devolverPrestamo(Integer prestamoId);

    List<Prestamo> obtenerTodosLosPrestamos();

    Optional<Prestamo> obtenerPrestamoPorId(Integer id);

    List<Prestamo> obtenerPrestamosPorUsuario(Usuario usuario);

    List<Prestamo> obtenerPrestamosActivos();

    // Opcional: para futuras mejoras
    // void actualizarEstadoVencidos(); // podría ejecutarse con un scheduler
}