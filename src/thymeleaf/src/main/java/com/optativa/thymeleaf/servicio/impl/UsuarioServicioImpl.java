package com.optativa.thymeleaf.servicio.impl;

import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.repositorio.UsuarioRepositorio;
import com.optativa.thymeleaf.servicio.UsuarioServicio;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios para la gestión de **Usuarios**.
 * * Propósito:
 * - Manejar el registro y la consulta de perfiles de usuario.
 * - Servir de base para la lógica de autenticación del sistema.
 */
@Primary
@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    /**
     * Repositorio de usuarios inyectado por constructor para asegurar
     * la disponibilidad de la capa de datos.
     */
    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Localiza a un usuario mediante su correo electrónico.
     * Esencial para verificar credenciales durante el login.
     */
    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    /**
     * Persiste un usuario en la base de datos.
     * * Nota de seguridad: Actualmente guarda los datos tal cual se reciben. 
     * Al implementar Spring Security, este método deberá incluir la 
     * encriptación de la contraseña (ej. passwordEncoder.encode(usuario.getPassword())).
     */
    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    /**
     * Recupera el listado global de usuarios registrados.
     */
    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepositorio.findAll();
    }

    /**
     * Busca un usuario específico por su identificador primario.
     */
    @Override
    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }
}