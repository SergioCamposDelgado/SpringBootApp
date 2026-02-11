package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Prestamo;
import com.optativa.thymeleaf.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la gestión de **Préstamos**.
 * * Es el núcleo de la lógica transaccional de la biblioteca, permitiendo 
 * rastrear quién tiene qué libro y en qué estado se encuentra la operación.
 */
@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, Integer> {

    /**
     * Recupera el historial de préstamos de un usuario mediante su ID.
     * @param usuarioId Identificador único del usuario.
     * @return Lista de préstamos asociados.
     */
    List<Prestamo> findByUsuarioId(Integer usuarioId);

    /**
     * Filtra préstamos según su situación actual (ACTIVO, DEVUELTO, etc.).
     * @param estado Valor del enumerado EstadoPrestamo.
     * @return Lista de préstamos que coinciden con el estado.
     */
    List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado);

    /**
     * Query Method avanzado para detectar retrasos.
     * Busca préstamos cuya fecha de devolución ya pasó y siguen en un estado específico.
     * * @param fecha Generalmente se pasa LocalDate.now()
     * @param estado Normalmente se busca por estado 'ACTIVO' para detectar vencimientos.
     * @return Lista de préstamos fuera de plazo.
     */
    List<Prestamo> findByFechaDevolucionPrevistaBeforeAndEstado(LocalDate fecha, Prestamo.EstadoPrestamo estado);

    /**
     * Busca todos los préstamos vinculados a un objeto Usuario completo.
     * Útil cuando ya se tiene la entidad Usuario cargada en la sesión o contexto.
     */
    List<Prestamo> findByUsuario(Usuario usuario);

}