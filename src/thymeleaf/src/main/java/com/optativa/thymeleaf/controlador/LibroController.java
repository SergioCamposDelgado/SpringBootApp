package com.optativa.thymeleaf.controlador;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.servicio.*;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping
    public String listarLibros(
            Model model,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable,
            @RequestParam(required = false) String keyword) {

        Page<Libro> page;
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = libroServicio.buscarPorTitulo(keyword, pageable);
        } else {
            page = libroServicio.obtenerTodosLosLibros(pageable);
        }

        model.addAttribute("libros", page.getContent()); // Los libros de la página actual
        model.addAttribute("page", page);               // El objeto Page completo para la vista
        model.addAttribute("keyword", keyword);         // Para mantener el texto en el buscador
        model.addAttribute("titulo", "Catálogo de Libros");

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
    @PostMapping("/guardar")
    public String guardarLibro(
            @Valid @ModelAttribute Libro libro,
            BindingResult bindingResult,
            Model model, // Añadimos Model para recargar las listas si hay error
            RedirectAttributes flash) {

        // 1. Validación personalizada: ¿Existe ya el ISBN?
        // Solo comprobamos si es un libro nuevo (id == null) o si el ID es diferente al que tiene ese ISBN
        Optional<Libro> libroExistente = libroServicio.obtenerLibroPorIsbn(libro.getIsbn());
        
        if (libroExistente.isPresent() && !libroExistente.get().getId().equals(libro.getId())) {
            bindingResult.rejectValue("isbn", "error.libro", "Este ISBN ya está registrado en el sistema");
        }

        // 2. Si hay errores (de Bean Validation o el nuestro manual)
        if (bindingResult.hasErrors()) {
            // IMPORTANTE: Debes volver a cargar las listas de autores y categorías 
            // para que el select no aparezca vacío al recargar la vista con errores
            model.addAttribute("autores", autorServicio.obtenerTodosLosAutores());
            model.addAttribute("categorias", categoriaServicio.obtenerTodasLasCategorias());
            model.addAttribute("titulo", libro.getId() == null ? "Nuevo Libro" : "Editar Libro");
            return "libros/formulario-libro";
        }

        try {
            libroServicio.guardarLibro(libro);
            flash.addFlashAttribute("mensaje", "Libro guardado correctamente");
            flash.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            flash.addFlashAttribute("mensaje", "Error inesperado: " + e.getMessage());
            flash.addFlashAttribute("tipo", "danger");
        }

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