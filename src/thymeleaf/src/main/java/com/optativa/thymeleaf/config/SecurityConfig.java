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

import com.optativa.thymeleaf.entidad.enumerado.Rol;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF activado con cookie (seguro y compatible con formularios POST)
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/h2-console/**")  // excepción solo para H2
            )

            // Permitir iframes para H2 console (desarrollo)
            .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))

            // Autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos siempre públicos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                // Rutas completamente públicas (sin login)
                .requestMatchers(
                    "/", "/home", "/saluda",
                    "/login", "/error", "/acceso-denegado",
                    "/libros", "/libros/**",         // Catálogo y detalle de libros públicos
                    "/autores", "/autores/**",
                    "/categorias", "/categorias/**"
                ).permitAll()

                // H2 console → restringida a ADMIN
                .requestMatchers("/h2-console/**").hasRole(Rol.ADMIN.name())

                // Acciones administrativas → solo ADMIN
                .requestMatchers(
                    "/libros/nuevo", "/libros/*/editar", "/libros/*/eliminar",
                    "/autores/nuevo", "/autores/*/editar", "/autores/*/eliminar",
                    "/categorias/nuevo", "/categorias/*/editar", "/categorias/*/eliminar",
                    "/usuarios/**", "/prestamos/admin/**"
                ).hasRole(Rol.ADMIN.name())

                // Acciones que requieren estar autenticado (prestamos personales)
                .requestMatchers(
                    "/prestamos/mios",
                    "/prestamos/solicitar/**",
                    "/prestamos/devolver/**"
                ).authenticated()

                // Cualquier otra ruta → requiere autenticación
                .anyRequest().authenticated()
            )

            // Configuración del login
            .formLogin(form -> form
                .loginPage("/login")                    // página personalizada
                .usernameParameter("email")             // campo del formulario
                .passwordParameter("password")
                .defaultSuccessUrl("/libros", true)     // éxito → catálogo
                .failureUrl("/login?error=true")        // fallo → vuelve con ?error
                .permitAll()
            )

            // Configuración del logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/libros")            // éxito → catálogo público
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)              // limpia autenticación
                .permitAll()
            )

            // Manejo de acceso denegado (403)
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }
}