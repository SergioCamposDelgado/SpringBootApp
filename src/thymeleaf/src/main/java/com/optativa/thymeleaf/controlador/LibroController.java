package com.optativa.thymeleaf.controlador;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.servicio.*;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador MVC responsable de todas las operaciones relacionadas con libros:
 * - Listado general
 * - Detalle de un libro
 * - Creación (solo ADMIN)
 * - Edición (solo ADMIN)
 * - Eliminación (solo ADMIN)
 * 
 * Rutas base: /libros
 */
@Controller
@RequestMapping("/libros")                          // Prefijo común para todas las rutas de este controlador
public class LibroController {

    // Inyección por constructor (mejor práctica recomendada)
    // No es necesario @Autowired desde Spring 4.3+ si hay un único constructor
    private final LibroServicio libroServicio;
    private final AutorServicio autorServicio;
    private final CategoriaServicio categoriaServicio;

    public LibroController(LibroServicio libroServicio, 
                          AutorServicio autorServicio, 
                          CategoriaServicio categoriaServicio) {
        this.libroServicio = libroServicio;
        this.autorServicio = autorServicio;
        this.categoriaServicio = categoriaServicio;
    }

    /**
     * Listado general de libros
     * Accesible para usuarios autenticados (LECTOR) y ADMIN
     * También accesible sin login (según SecurityConfig)
     */
    @GetMapping                          // → /libros
    public String listarLibros(Model model) {
        // Cargamos todos los libros (normalmente con paginación en proyectos reales)
        model.addAttribute("libros", libroServicio.obtenerTodosLosLibros());
        
        model.addAttribute("titulo", "Catálogo de Libros");
        
        // Vista: templates/libros/lista-libros.html
        return "libros/lista-libros";
    }

    /**
     * Detalle de un libro concreto
     * Accesible públicamente (GET /libros/123)
     */
    @GetMapping("/{id}")                 // → /libros/5
    public String detalleLibro(@PathVariable Integer id, Model model) {
        Optional<Libro> libroOpt = libroServicio.obtenerLibroPorId(id);
        
        if (libroOpt.isEmpty()) {
            // Libro no encontrado → redirección simple (sin mensaje flash aquí)
            return "redirect:/libros";
        }
        
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("titulo", "Detalle: " + libroOpt.get().getTitulo());
        
        return "libros/detalle-libro";   // → templates/libros/detalle-libro.html
    }

    /**
     * Formulario para crear un libro nuevo
     * Solo accesible para ADMIN (protegido en SecurityConfig)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nuevo")
    public String nuevoLibro(Model model) {
        // Objeto vacío para binding del formulario
        model.addAttribute("libro", new Libro());
        
        // Listas necesarias para los <select> del formulario
        model.addAttribute("autores", autorServicio.obtenerTodosLosAutores());
        model.addAttribute("categorias", categoriaServicio.obtenerTodasLasCategorias());
        
        model.addAttribute("titulo", "Nuevo Libro");
        
        return "libros/formulario-libro";   // misma vista que edición
    }

    /**
     * Formulario para editar un libro existente
     * Solo ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editarLibro(@PathVariable Integer id, Model model) {
        Optional<Libro> libroOpt = libroServicio.obtenerLibroPorId(id);
        
        if (libroOpt.isEmpty()) {
            return "redirect:/libros";
        }
        
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("autores", autorServicio.obtenerTodosLosAutores());
        model.addAttribute("categorias", categoriaServicio.obtenerTodasLasCategorias());
        model.addAttribute("titulo", "Editar Libro");
        
        return "libros/formulario-libro";   // reutilizamos la misma plantilla
    }

    /**
     * Acción de guardar (crear o actualizar)
     * 
     * @Valid + BindingResult → validación de Bean Validation (@NotBlank, @Size, etc.)
     * Uso de RedirectAttributes + flash → patrón PRG (Post/Redirect/Get)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")                    // → /libros/guardar (POST)
    public String guardarLibro(
            @Valid @ModelAttribute Libro libro,     // binding automático + validación
            BindingResult bindingResult,            // contiene errores de validación
            RedirectAttributes flash) {             // para mensajes flash tras redirección

        // Si hay errores de validación → volvemos al formulario (sin redirect)
        if (bindingResult.hasErrors()) {
            // Es importante NO hacer redirect aquí, porque perdemos los errores y el objeto
            // Spring re-popula automáticamente los campos con @ModelAttribute
            
            // Nota: en esta vista deberías tener también las listas de autores y categorías
            // Si no las agregas aquí, el formulario quedará sin opciones al volver con error
            // Solución común → usar @ModelAttribute en métodos separados o un @ControllerAdvice
            return "libros/formulario-libro";
        }

        try {
            libroServicio.guardarLibro(libro);
            flash.addFlashAttribute("mensaje", "Libro guardado correctamente");
            flash.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            flash.addFlashAttribute("mensaje", "Error al guardar el libro: " + e.getMessage());
            flash.addFlashAttribute("tipo", "danger");
            // Aquí también podrías volver al formulario preservando datos, pero es más complejo
        }

        // Siempre redirigimos después de POST → evita reenvío del formulario (F5)
        return "redirect:/libros";
    }

    /**
     * Eliminación de un libro
     * Solo ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/eliminar")
    public String eliminarLibro(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            libroServicio.eliminarLibro(id);
            flash.addFlashAttribute("mensaje", "Libro eliminado correctamente");
            flash.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            flash.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            flash.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/libros";
    }
}