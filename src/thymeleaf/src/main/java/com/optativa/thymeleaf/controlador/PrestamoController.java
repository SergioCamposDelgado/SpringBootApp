package com.optativa.thymeleaf.controlador;

import com.optativa.thymeleaf.entidad.Libro;
import com.optativa.thymeleaf.entidad.Prestamo;
import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.servicio.LibroServicio;
import com.optativa.thymeleaf.servicio.PrestamoServicio;
import com.optativa.thymeleaf.servicio.UsuarioServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoServicio prestamoServicio;
    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;

    public PrestamoController(PrestamoServicio prestamoServicio,
                              LibroServicio libroServicio,
                              UsuarioServicio usuarioServicio) {
        this.prestamoServicio = prestamoServicio;
        this.libroServicio = libroServicio;
        this.usuarioServicio = usuarioServicio;
    }

    // Mis préstamos (solo el usuario logueado ve los suyos)
    @GetMapping("/mios")
    public String misPrestamos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("prestamos", prestamoServicio.obtenerPrestamosPorUsuario(usuario));
            model.addAttribute("usuario", usuario);
        } else {
            // Caso raro: usuario logueado pero no encontrado en BD
            model.addAttribute("prestamos", List.of());
            model.addAttribute("mensaje", "Usuario no encontrado");
        }

        return "prestamos/mis-prestamos";
    }

    // Formulario para solicitar préstamo de un libro específico
    @GetMapping("/solicitar/{libroId}")
    public String formularioSolicitar(@PathVariable Integer libroId, Model model, RedirectAttributes flash) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 1. Obtener usuario logueado
        Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            flash.addFlashAttribute("error", "Debes estar autenticado para solicitar préstamos");
            return "redirect:/libros";
        }

        // 2. Obtener libro y validar
        Optional<Libro> libroOpt = libroServicio.obtenerLibroPorId(libroId);
        if (libroOpt.isEmpty()) {
            flash.addFlashAttribute("error", "El libro no existe");
            return "redirect:/libros";
        }

        Libro libro = libroOpt.get();
        if (!libro.getDisponible()) {
            flash.addFlashAttribute("error", "El libro no está disponible actualmente");
            return "redirect:/libros/" + libroId;
        }

        // Preparar objeto Prestamo para el formulario
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuarioOpt.get());

        model.addAttribute("prestamo", prestamo);
        model.addAttribute("libro", libro);
        model.addAttribute("titulo", "Solicitar Préstamo: " + libro.getTitulo());

        return "prestamos/formulario-solicitar";
    }

    // Procesar solicitud de préstamo (POST)
    @PostMapping("/solicitar")
    public String solicitarPrestamo(@ModelAttribute Prestamo prestamo,
                                    RedirectAttributes flash) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                flash.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/libros";
            }

            // Llamada real al servicio (aquí se valida y se marca libro no disponible)
            Prestamo prestamoGuardado = prestamoServicio.solicitarPrestamo(
                prestamo.getLibro().getId(),
                usuarioOpt.get().getId()
            );

            flash.addFlashAttribute("mensaje", "Préstamo solicitado correctamente. Fecha límite: " + 
                prestamoGuardado.getFechaDevolucionPrevista());
            flash.addFlashAttribute("tipo", "success");

        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al solicitar el préstamo: " + e.getMessage());
        }

        return "redirect:/prestamos/mios";
    }

    // Devolver un préstamo
    @GetMapping("/devolver/{id}")
    public String devolverPrestamo(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                flash.addFlashAttribute("error", "Debes estar autenticado");
                return "redirect:/login";
            }

            Prestamo prestamoDevuelto = prestamoServicio.devolverPrestamo(id);

            // Validar que el préstamo pertenece al usuario logueado (seguridad extra)
            if (!prestamoDevuelto.getUsuario().getId().equals(usuarioOpt.get().getId())) {
                flash.addFlashAttribute("error", "No puedes devolver un préstamo que no es tuyo");
                return "redirect:/prestamos/mios";
            }

            flash.addFlashAttribute("mensaje", "Libro devuelto correctamente el " + 
                prestamoDevuelto.getFechaDevolucionReal());
            flash.addFlashAttribute("tipo", "success");

        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al devolver el préstamo: " + e.getMessage());
        }

        return "redirect:/prestamos/mios";
    }
}