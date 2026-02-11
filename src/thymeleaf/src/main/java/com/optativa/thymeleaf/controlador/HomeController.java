package com.optativa.thymeleaf.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador responsable de las páginas más básicas y de entrada de la aplicación:
 * - Página de inicio (home)
 * - Página de login
 * - Página de error 403 (acceso denegado)
 * - Pagina de error 404 (no encontrado)
 * 
 * Este controlador NO maneja lógica de negocio pesada, solo devuelve vistas públicas o de sistema.
 */
@Controller                                         // Indica que esta clase es un controlador MVC de Spring
// @RequestMapping a nivel de clase es opcional aquí porque no hay un prefijo común
// Si quisieras que todas las rutas empiecen con /public por ejemplo:
// @RequestMapping("/public")
public class HomeController {

    /**
     * Maneja las rutas de entrada principal de la aplicación (públicas)
     * 
     * @GetMapping con array de valores → varias URLs ejecutan el mismo método
     * Esto es muy común para la página principal
     * 
     * Estas rutas están configuradas como .permitAll() en SecurityConfig
     * → cualquier usuario (autenticado o no) puede acceder
     */
    @GetMapping({"/", "/home", ""})                 // "" → maneja también la raíz vacía (algunos servidores la envían)
    public String home(Model model) {
        
        // Añadimos atributos al modelo que Thymeleaf podrá usar en la vista
        // El nombre "titulo" es una convención bastante usada para el <title> y encabezados
        model.addAttribute("titulo", "Bienvenido a la Biblioteca Online");
        
        // Retornamos el nombre lógico de la vista (sin .html)
        // Spring Boot + Thymeleaf busca automáticamente en:
        // src/main/resources/templates/home.html
        return "home";
    }

    /**
     * Página de login personalizada
     * 
     * Spring Security redirige automáticamente aquí cuando:
     * 1. El usuario intenta acceder a una ruta protegida sin estar autenticado
     * 2. Se produce un error de autenticación (credenciales incorrectas)
     * 
     * Importante: esta ruta DEBE estar en .permitAll() en SecurityConfig
     * (como ya lo tienes configurado)
     */
    @GetMapping("/login")
    public String login(Model model) {
        // Título para la página de login
        model.addAttribute("titulo", "Iniciar Sesión");
        
        // También podrías añadir aquí otros atributos útiles, ejemplos comunes:
        // model.addAttribute("error", false);   // para mostrar mensaje solo si hay error
        // model.addAttribute("mensaje", "");    // mensaje personalizado
        
        return "login";     // → busca templates/login.html
    }

    /**
     * Página mostrada cuando el usuario está autenticado pero NO tiene permisos
     * para acceder al recurso solicitado (HTTP 403 Forbidden)
     * 
     * Configurado en SecurityConfig con:
     * .exceptionHandling().accessDeniedPage("/acceso-denegado")
     * 
     * Es buena práctica tener esta página personalizada en lugar del mensaje por defecto
     */
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        // No necesitamos Model aquí porque normalmente solo mostramos un mensaje estático
        // Pero si quisieras podrías agregar información extra:
        // model.addAttribute("mensaje", "No tienes permisos para ver esta sección");
        
        return "error/403";   // → busca templates/error/acceso-denegado.html
    }
}