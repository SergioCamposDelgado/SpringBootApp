package com.optativa.thymeleaf.entidad.enumerado;

/**
 * Enumerado que define los niveles de acceso y permisos dentro de la aplicación.
 * * Uso:
 * - Integración con Spring Security para control de acceso (HasRole).
 * - Determina qué elementos de la interfaz (Thymeleaf) son visibles para cada usuario.
 */
public enum Rol {
    
    /**
     * Superusuario con acceso total al sistema.
     * Puede gestionar libros, autores, categorías y administrar otros usuarios.
     */
    ADMIN,      

    /**
     * Perfil orientado a la consulta y préstamos.
     * Tiene permisos para solicitar libros y ver su historial, pero no para modificar el catálogo.
     */
    LECTOR,    

    /**
     * Rol básico de acceso al sistema.
     * Generalmente asignado por defecto al registrarse; cuenta con permisos limitados de navegación.
     */
    USUARIO     
    
}