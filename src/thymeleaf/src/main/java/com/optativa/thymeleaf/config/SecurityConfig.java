package com.optativa.thymeleaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.http.HttpMethod;

import com.optativa.thymeleaf.entidad.enumerado.Rol;

/**
 * Configuración central de Spring Security
 * 
 * Esta clase define:
 *  - Cómo se encriptan las contraseñas
 *  - Qué rutas son públicas, privadas, de solo lectura, solo admin, etc.
 *  - Cómo funciona el login, logout y manejo de errores de autorización
 */
@Configuration
@EnableWebSecurity                          // Activa la configuración de seguridad web
@EnableMethodSecurity                       // Permite usar anotaciones @PreAuthorize, @Secured, @RolesAllowed en métodos
public class SecurityConfig {

    /**
     * Bean que define el algoritmo de encriptación de contraseñas
     * BCrypt es el más recomendado hoy en día para contraseñas de usuarios
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // fuerza por defecto = 10 → buen balance entre seguridad y rendimiento
        return new BCryptPasswordEncoder();
        // Alternativas (no recomendadas para nuevos proyectos): NoOpPasswordEncoder, Pbkdf2, Argon2
    }

    /**
     * Define la cadena de filtros de seguridad (Security Filter Chain)
     * Este es el corazón de la configuración de autorización en Spring Security 6+
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ────────────────────────────────────────────────────────────────
            // Protección CSRF
            // ────────────────────────────────────────────────────────────────
            .csrf(csrf -> csrf
                // Guardamos el token CSRF en una cookie llamada XSRF-TOKEN
                // y lo hacemos legible desde JavaScript (HttpOnly = false)
                // Esto es necesario si usas formularios AJAX o Thymeleaf con fetch/axios
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                // Deshabilitamos CSRF solo para la consola H2 (necesario para que funcione)
                .ignoringRequestMatchers("/h2-console/**")
            )

            // ────────────────────────────────────────────────────────────────
            // Headers de seguridad
            // ────────────────────────────────────────────────────────────────
            .headers(headers -> headers
                // Permite que la consola H2 se muestre en iframe (necesario en desarrollo)
                .frameOptions(f -> f.sameOrigin())
            )

            // ────────────────────────────────────────────────────────────────
            // Autorización de URLs (lo más importante de esta configuración)
            // ────────────────────────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth

            		// 1. RECURSOS ESTÁTICOS
                    .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                    // 2. RUTAS DE ERROR Y ACCESO
                    .requestMatchers("/login", "/error").permitAll()
                    
                    // 3. REGLAS DE ADMIN (Específicas primero)
                    .requestMatchers("/h2/**").hasRole(Rol.ADMIN.name())
                    .requestMatchers("/libros/nuevo", "/libros/*/editar", "/libros/*/eliminar", "/libros/guardar").hasRole(Rol.ADMIN.name())
                    .requestMatchers("/prestamos", "/prestamos/nuevo", "/prestamos/{id}/editar").hasRole(Rol.ADMIN.name())

                    // 4. RUTAS PÚBLICAS (Sin login)
                    // Nota: He separado /libros para que solo el GET sea público
                    .requestMatchers("/", "/home", "/login", "/error", "/acceso-denegado").permitAll()
                    .requestMatchers(HttpMethod.GET, "/libros", "/libros/{id}").permitAll()

                    // 5. RUTAS DE USUARIO AUTENTICADO (Cualquier rol)
                    .requestMatchers("/prestamos/mios", "/prestamos/solicitar/**", "/prestamos/devolver/**", "/prestamos/cancelar/**", "/prestamos/{id}").authenticated()

                    .anyRequest().authenticated()
            )

            // ────────────────────────────────────────────────────────────────
            // Formulario de login personalizado
            // ────────────────────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/login")                    // nuestra página de login (GET)
                .usernameParameter("email")             // campo <input name="email">
                .passwordParameter("password")          // campo <input name="password">
                .defaultSuccessUrl("/libros", true)     // a dónde ir tras login exitoso
                .failureUrl("/login?error=true")        // si falla el login
                .permitAll()                            // cualquiera puede ver la página de login
            )

            // ────────────────────────────────────────────────────────────────
            // Configuración de logout
            // ────────────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")                   // URL que llama al cerrar sesión
                .logoutSuccessUrl("/")            // a dónde redirigir tras logout
                .deleteCookies("JSESSIONID")            // elimina la cookie de sesión
                .invalidateHttpSession(true)            // invalida la sesión HTTP
                .clearAuthentication(true)              // limpia el contexto de seguridad
                .permitAll()
            )

            // ────────────────────────────────────────────────────────────────
            // Manejo de excepciones de autorización
            // ────────────────────────────────────────────────────────────────
            .exceptionHandling(exception -> exception
                // Página personalizada cuando el usuario está logueado pero no tiene permiso
                .accessDeniedPage("/acceso-denegado")
            );

        // Construye y devuelve la cadena de filtros
        return http.build();
    }
}