package com.optativa.thymeleaf.entidad;

import com.optativa.thymeleaf.entidad.enumerado.Rol;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entidad que representa un **Usuario** del sistema (lector o administrador).
 * 
 * Campos principales:
 *  - email (usado como username para login)
 *  - password (debe estar codificada con BCrypt antes de guardar)
 *  - nombreCompleto
 *  - rol (ADMIN o LECTOR, definido en el enum Rol)
 * 
 * Esta entidad se usa tanto para autenticación (Spring Security) 
 * como para la lógica de negocio (quién puede solicitar préstamos, quién administra, etc.).
 */
@Entity
@Table(name = "usuarios")                    // Nombre explícito de la tabla (buena práctica)
public class Usuario {

    /**
     * Clave primaria auto-generada
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * Email del usuario → sirve como identificador único y como "username" en Spring Security
     * 
     * Restricciones:
     * - Obligatorio
     * - Formato válido de email
     * - Único en toda la base de datos (no puede haber dos usuarios con el mismo email)
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Contraseña del usuario
     * 
     * IMPORTANTE:
     *   - Nunca se guarda la contraseña en texto plano
     *   - Debe ser codificada con BCryptPasswordEncoder antes de persistir
     *   - La validación @Size(min=6) aplica a la contraseña **en texto plano**
     *     (antes de codificar). Una vez codificada será mucho más larga (~60 caracteres)
     * 
     * En producción: nunca mostrar este campo ni permitir recuperarlo directamente
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;  // ← ¡EN LA BASE DE DATOS ESTE CAMPO SIEMPRE DEBE ESTAR CODIFICADO!

    /**
     * Nombre completo del usuario (nombre + apellidos)
     * - Obligatorio
     * - Máximo 120 caracteres (suficiente para nombres largos)
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre completo no puede exceder 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    /**
     * Rol del usuario dentro del sistema
     * 
     * - Usamos @Enumerated(EnumType.STRING) → guarda "ADMIN" o "LECTOR" como texto
     *   (mucho más seguro y legible que ORDINAL)
     * - nullable = false → todo usuario debe tener un rol definido
     * 
     * En Spring Security se usa como "ROLE_" + rol.name() → ej: "ROLE_ADMIN", "ROLE_LECTOR"
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // ────────────────────────────────────────────────────────────────
    // Constructores
    // ────────────────────────────────────────────────────────────────
    public Usuario() {
        // Constructor vacío → obligatorio para JPA/Hibernate
    }

    // Constructor útil para creación rápida (muy común en inicializadores o tests)
    // public Usuario(String email, String passwordPlana, String nombreCompleto, Rol rol) {
    //     this.email = email;
    //     this.password = passwordEncoder.encode(passwordPlana); // ← codificar aquí o en servicio
    //     this.nombreCompleto = nombreCompleto;
    //     this.rol = rol;
    // }

    // ────────────────────────────────────────────────────────────────
    // Getters y Setters
    // ────────────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    // ────────────────────────────────────────────────────────────────
    // Métodos convenientes (muy útiles en vistas y controladores)
    // ────────────────────────────────────────────────────────────────
    // Ejemplo recomendado (puedes agregarlo):
    // public boolean esAdmin() {
    //     return this.rol == Rol.ADMIN;
    // }

    // ────────────────────────────────────────────────────────────────
    // toString seguro – nunca incluir la contraseña
    // ────────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol=" + rol +
                '}';
        // ¡Nunca incluir "password" aquí! → riesgo de seguridad en logs
    }
}