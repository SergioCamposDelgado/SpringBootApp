package com.optativa.thymeleaf.servicio;

import com.optativa.thymeleaf.entidad.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define el contrato de servicios para la gestión de **Usuarios**.
 * * Responsabilidades:
 * - Gestionar el ciclo de vida de las cuentas de usuario (Registro y Consulta).
 * - Proveer métodos de búsqueda para el proceso de autenticación.
 * - Centralizar el acceso a los datos de perfil de los lectores y administradores.
 */
public interface UsuarioServicio {

    /**
     * Localiza a un usuario en la base de datos a través de su email.
     * * Es el método principal utilizado por el motor de seguridad para 
     * verificar la existencia del usuario durante el inicio de sesión.
     * * @param email Correo electrónico del usuario.
     * @return Un **Optional** con el Usuario si existe.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Registra un nuevo usuario o actualiza los datos de uno existente.
     * * Nota: En la implementación, este método suele encargarse de 
     * encriptar la contraseña antes de persistir los datos.
     * * @param usuario Entidad con los datos del usuario.
     * @return El usuario guardado.
     */
    Usuario guardarUsuario(Usuario usuario);

    /**
     * Obtiene el listado completo de personas registradas en el sistema.
     * * Generalmente utilizado en paneles de administración (ADMIN).
     * * @return Lista de todos los usuarios.
     */
    List<Usuario> obtenerTodosLosUsuarios();

    /**
     * Busca un usuario por su identificador numérico único.
     * * @param id ID del usuario.
     * @return Un Optional que encapsula el resultado de la búsqueda.
     */
    Optional<Usuario> obtenerPorId(Integer id);
}