package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, Integer> {

    List<Prestamo> findByUsuarioId(Integer usuarioId);
    List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado);
    List<Prestamo> findByFechaDevolucionPrevistaBeforeAndEstado(LocalDate fecha, Prestamo.EstadoPrestamo estado);

}