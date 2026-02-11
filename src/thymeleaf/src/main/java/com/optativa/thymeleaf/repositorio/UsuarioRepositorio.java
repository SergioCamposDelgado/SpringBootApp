package com.optativa.thymeleaf.repositorio;

import com.optativa.thymeleaf.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de **Usuarios** del sistema.
 * * Es una pieza crítica para la seguridad, ya que permite localizar a los 
 * usuarios durante el proceso de login y verificar sus credenciales.
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * * @param email El correo electrónico (que actúa como identificador único de acceso).
     * @return Un **Optional** que contiene el Usuario si existe, o vacío si no.
     * * Nota: El uso de Optional ayuda a evitar el temido NullPointerException 
     * y se integra perfectamente con la lógica de Spring Security.
     */
    Optional<Usuario> findByEmail(String email);

}