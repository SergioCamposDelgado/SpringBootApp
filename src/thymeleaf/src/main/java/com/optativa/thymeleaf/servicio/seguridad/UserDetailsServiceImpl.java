package com.optativa.thymeleaf.servicio.seguridad;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.repositorio.UsuarioRepositorio;

/**
 * Servicio encargado de la autenticación de usuarios en Spring Security.
 * * Al implementar **UserDetailsService**, le decimos a Spring cómo debe 
 * buscar a los usuarios cuando intentan hacer login en la aplicación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Inyectamos el repositorio para consultar los datos reales de la BD.
     */
    public UserDetailsServiceImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Método core de seguridad: localiza al usuario por su "username".
     * En este sistema, utilizamos el **email** como nombre de usuario único.
     * * @param username El email introducido en el formulario de login.
     * @return Un objeto **UserDetails** que Spring Security usará para validar la sesión.
     * @throws UsernameNotFoundException Si el email no existe en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Buscamos el usuario en nuestra base de datos
        Usuario usuario = usuarioRepositorio.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Construimos y retornamos el objeto User (de Spring Security)
        // Este objeto contiene la información necesaria para las comprobaciones posteriores
        return User.withUsername(usuario.getEmail())
                // La contraseña aquí ya debe estar encriptada (ej: BCrypt) en la BD
                .password(usuario.getPassword()) 
                
                /*
                 * Asignación de Roles:
                 * .roles() toma el nombre del enumerado (ej: "ADMIN") y Spring Security 
                 * le añade automáticamente el prefijo "ROLE_" (quedando como ROLE_ADMIN).
                 */
                .roles(usuario.getRol().name()) 
                .build();
    }
}