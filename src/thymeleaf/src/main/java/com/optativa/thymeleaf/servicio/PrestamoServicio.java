package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Prestamo;
import com.optativa.thymeleaf.entidad.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define la lógica de negocio para la gestión de **Préstamos**.
 * * Esta capa es responsable de:
 * - Coordinar las solicitudes de libros entre Usuarios y Libros.
 * - Gestionar los cambios de estado (Activo, Devuelto, Cancelado).
 * - Diferenciar las operaciones permitidas para un Lector frente a un Administrador.
 */
public interface PrestamoServicio {

    // ────────────────────────────────────────────────────────────────
    // Operaciones para usuarios (LECTOR / USUARIO)
    // ────────────────────────────────────────────────────────────────

    /**
     * Recupera el historial de préstamos personal de un usuario específico.
     * @param usuario Entidad usuario que realiza la consulta.
     * @return Lista de sus préstamos realizados.
     */
    List<Prestamo> obtenerPrestamosPorUsuario(Usuario usuario);

    /**
     * Inicia el proceso de préstamo de un libro.
     * Debería validar que el libro esté disponible antes de proceder.
     * @param libroId ID del libro deseado.
     * @param usuarioId ID del usuario que solicita.
     * @return El objeto Prestamo generado.
     */
    Prestamo solicitarPrestamo(Integer libroId, Integer usuarioId);

    /**
     * Registra la entrega de un libro por parte del usuario.
     * Cambia el estado del préstamo y marca el libro como disponible nuevamente.
     * @param prestamoId ID del préstamo a finalizar.
     * @return El préstamo actualizado.
     */
    Prestamo devolverPrestamo(Integer prestamoId);

    // ────────────────────────────────────────────────────────────────
    // Operaciones de Gestión (ADMIN)
    // ────────────────────────────────────────────────────────────────

    /**
     * Obtiene todos los préstamos registrados en el sistema global.
     * @return Lista total de préstamos (activos, vencidos, devueltos, etc.).
     */
    List<Prestamo> obtenerTodosLosPrestamos();

    /**
     * Busca un préstamo concreto por su ID para edición o auditoría.
     */
    Optional<Prestamo> obtenerPrestamoPorId(Integer id);

    /**
     * Permite a un administrador crear un préstamo directamente.
     * Útil para registros manuales o migraciones de datos.
     */
    Prestamo crearPrestamoManual(Prestamo prestamo);

    /**
     * Actualiza los datos de un préstamo (ej: extender fecha de devolución).
     */
    Prestamo actualizarPrestamo(Prestamo prestamo);

    /**
     * Anula un préstamo. A diferencia de 'devolver', este método suele usarse
     * para corregir errores o cancelaciones inmediatas.
     * @param prestamoId ID del préstamo a cancelar.
     */
    void cancelarPrestamo(Integer prestamoId);
    
    // ────────────────────────────────────────────────────────────────
    // Operaciones de Guardado (Iniciar Datos)
    // ────────────────────────────────────────────────────────────────
    
    Prestamo guardarPrestamo(Prestamo prestamo);
}