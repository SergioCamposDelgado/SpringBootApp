package com.optativa.thymeleaf.controlador;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.servicio.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/libros")
public class LibroController {

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

    // Listado de libros (accesible para LECTOR y ADMIN)
    @GetMapping
    public String listarLibros(Model model) {
        model.addAttribute("libros", libroServicio.obtenerTodosLosLibros());
        model.addAttribute("titulo", "Catálogo de Libros");
        return "libros/lista-libros";
    }

    @GetMapping("/{id}")
    public String detalleLibro(@PathVariable Integer id, Model model) {
        Optional<Libro> libroOpt = libroServicio.obtenerLibroPorId(id);
        if (libroOpt.isEmpty()) {
            // Redirigir con mensaje flash
            return "redirect:/libros";
        }
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("titulo", "Detalle: " + libroOpt.get().getTitulo());
        return "libros/detalle-libro";
    }

    // Formulario nuevo libro (solo ADMIN)
    @GetMapping("/nuevo")
    public String nuevoLibro(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("autores", autorServicio.obtenerTodosLosAutores());
        model.addAttribute("categorias", categoriaServicio.obtenerTodasLasCategorias());
        model.addAttribute("titulo", "Nuevo Libro");
        return "libros/formulario-libro";
    }

    // Formulario editar libro (solo ADMIN)
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
        return "libros/formulario-libro";
    }

    // Guardar (crear o actualizar)
    @PostMapping("/guardar")
    public String guardarLibro(@Valid @ModelAttribute Libro libro,
                               BindingResult bindingResult,
                               RedirectAttributes flash) {
        if (bindingResult.hasErrors()) {
            // Si hay errores, volvemos al formulario
            return "libros/formulario-libro";
        }

        try {
            libroServicio.guardarLibro(libro);
            flash.addFlashAttribute("mensaje", "Libro guardado correctamente");
            flash.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            flash.addFlashAttribute("mensaje", "Error al guardar el libro: " + e.getMessage());
            flash.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/libros";
    }

    // Eliminar (solo ADMIN)
    @GetMapping("/{id}/eliminar")
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