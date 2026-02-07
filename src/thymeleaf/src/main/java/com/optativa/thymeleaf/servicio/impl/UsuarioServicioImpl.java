package com.optativa.thymeleaf.servicio.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.entidad.enumerado.Rol;
import com.optativa.thymeleaf.repositorio.UsuarioRepositorio;
import com.optativa.thymeleaf.servicio.UsuarioServicio;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

  private final UsuarioRepositorio usuarioRepositorio;
  private final PasswordEncoder passwordEncoder;

  public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
    this.usuarioRepositorio = usuarioRepositorio;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Usuario crear(String nombre, String contrasenaEnClaro, Rol rol) {
    if (usuarioRepositorio.findByNombre(nombre) != null) {
      throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
    }
    Usuario u = new Usuario();
    u.setNombre(nombre);
    u.setContrasena(passwordEncoder.encode(contrasenaEnClaro));
    u.setRol(rol);
    return usuarioRepositorio.save(u);
  }

  @Override
  public Usuario obtenerPorNombre(String nombre) {
    return usuarioRepositorio.findByNombre(nombre);
  }

  @Override
  public Usuario obtenerPorId(Long id) {
    return usuarioRepositorio.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id=" + id));
  }

  @Override
  public List<Usuario> listar() {
    return usuarioRepositorio.findAll();
  }

  @Override
  public Page<Usuario> listar(Pageable pageable) {
    return usuarioRepositorio.findAll(pageable);
  }

  @Override
  public Usuario actualizar(Long id, String nuevoNombre, Rol nuevoRol) {
    Usuario u = obtenerPorId(id);
    u.setNombre(nuevoNombre);
    u.setRol(nuevoRol);
    return usuarioRepositorio.save(u);
  }

  @Override
  public void cambiarContrasena(Long id, String contrasenaActualEnClaro, String nuevaContrasenaEnClaro) {
    Usuario u = obtenerPorId(id);
    if (!passwordEncoder.matches(contrasenaActualEnClaro, u.getContrasena())) {
      throw new IllegalArgumentException("La contraseña actual no es correcta");
    }
    u.setContrasena(passwordEncoder.encode(nuevaContrasenaEnClaro));
    usuarioRepositorio.save(u);
  }

  @Override
  public void eliminar(Long id) {
    usuarioRepositorio.deleteById(id);
  }

  @Override
  public Usuario obtenerUsuarioConectado() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null
        && !(authentication instanceof AnonymousAuthenticationToken)
        && authentication.isAuthenticated()) {

      String nombreUsuarioConectado = authentication.getName();
      return usuarioRepositorio.findByNombre(nombreUsuarioConectado);
    }
    return null;
  }
}