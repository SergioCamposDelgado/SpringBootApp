package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioServicio {

    Optional<Usuario> findByEmail(String email);

    Usuario guardarUsuario(Usuario usuario);

    List<Usuario> obtenerTodosLosUsuarios();

    Optional<Usuario> obtenerPorId(Integer id);
}