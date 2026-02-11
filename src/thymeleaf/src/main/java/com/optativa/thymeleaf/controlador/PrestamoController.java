package com.optativa.thymeleaf.controlador;

import com.optativa.thymeleaf.entidad.*;
import com.optativa.thymeleaf.servicio.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador principal para la gestión de préstamos
 * 
 * Maneja dos tipos de usuarios principales:
 *   - LECTOR (usuario normal): solo ve y gestiona SUS propios préstamos
 *   - ADMIN: ve todos los préstamos, puede crear/editar manualmente, cancelar, etc.
 * 
 * Casi todos los métodos verifican explícitamente permisos (aunque también están protegidos en SecurityConfig)
 */
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

    // ────────────────────────────────────────────────────────────────
    // 1. Mis Préstamos – vista personal del usuario logueado
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/mios")
    public String misPrestamos(Model model) {
        // Obtenemos el usuario actual desde el contexto de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();  // username = email (según configuración)

        Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            // Filtramos SOLO los préstamos de este usuario
            List<Prestamo> prestamos = prestamoServicio.obtenerPrestamosPorUsuario(usuarioOpt.get());
            model.addAttribute("prestamos", prestamos);
            model.addAttribute("titulo", "Mis Préstamos");
        } else {
            // Caso raro: usuario autenticado pero no existe en BD → lista vacía
            model.addAttribute("prestamos", List.of());
        }
        
        return "prestamos/mis-prestamos";
    }

    // ────────────────────────────────────────────────────────────────
    // 2. Listado completo – solo para administradores
    // ────────────────────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listadoAdmin(Model model) {
        // Verificación manual de rol ADMIN (complementa la regla en SecurityConfig)
        if (!SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // Si no es admin → lo mandamos a su vista personal
            return "redirect:/prestamos/mios";
        }

        model.addAttribute("prestamos", prestamoServicio.obtenerTodosLosPrestamos());
        model.addAttribute("titulo", "Gestión de Préstamos - Administración");
        return "prestamos/listado-admin";
    }

    // ────────────────────────────────────────────────────────────────
    // 3. Formulario para solicitar préstamo (desde detalle de libro)
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/solicitar/{libroId}")
    public String formularioSolicitar(
            @PathVariable Integer libroId,
            Model model,
            RedirectAttributes flash) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            flash.addFlashAttribute("error", "Debes iniciar sesión");
            return "redirect:/login";
        }

        Optional<Libro> libroOpt = libroServicio.obtenerLibroPorId(libroId);
        if (libroOpt.isEmpty() || !libroOpt.get().getDisponible()) {
            flash.addFlashAttribute("error", "Libro no disponible");
            return "redirect:/libros";
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libroOpt.get());
        prestamo.setUsuario(usuarioOpt.get());

        model.addAttribute("prestamo", prestamo);
        model.addAttribute("libro", libroOpt.get());
        
        return "prestamos/formulario-solicitar";
    }

    // ────────────────────────────────────────────────────────────────
    // 4. Procesar la solicitud de préstamo (POST)
    // ────────────────────────────────────────────────────────────────
    @PostMapping("/solicitar")
    public String solicitarPrestamo(
            @ModelAttribute Prestamo prestamo,
            RedirectAttributes flash) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioServicio.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                flash.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/libros";
            }

            // Llamada al servicio → aquí se valida cupo, disponibilidad, fechas, etc.
            prestamoServicio.solicitarPrestamo(
                    prestamo.getLibro().getId(),
                    usuarioOpt.get().getId()
            );

            flash.addFlashAttribute("mensaje", "Préstamo solicitado con éxito");
            flash.addFlashAttribute("tipo", "success");

        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/prestamos/mios";
    }

	 // ────────────────────────────────────────────────────────────────
	 // 5. Devolver un préstamo (usuario o admin)
	 // ────────────────────────────────────────────────────────────────
    @GetMapping("/devolver/{id}")
    public String devolverPrestamo(@PathVariable Integer id, RedirectAttributes flash) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        String redirectPath = esAdmin ? "redirect:/prestamos" : "redirect:/prestamos/mios";

        try {
            Prestamo prestamo = prestamoServicio.obtenerPrestamoPorId(id)
                    .orElseThrow(() -> new IllegalStateException("Préstamo no encontrado"));

            // NUEVA LÓGICA DE NEGOCIO:
            // Si está VENCIDO y NO es admin, bloqueamos la acción.
            if (prestamo.getEstado() == Prestamo.EstadoPrestamo.VENCIDO && !esAdmin) {
                flash.addFlashAttribute("error", "Los préstamos vencidos solo pueden ser procesados por un administrador en el mostrador.");
                return "redirect:/prestamos/mios";
            }

            // Validación de propiedad (para usuarios no admin)
            if (!esAdmin && !prestamo.getUsuario().getEmail().equals(auth.getName())) {
                flash.addFlashAttribute("error", "No tienes permiso para devolver este préstamo.");
                return "redirect:/prestamos/mios";
            }

            prestamoServicio.devolverPrestamo(id);
            flash.addFlashAttribute("mensaje", "Devolución registrada con éxito.");
            flash.addFlashAttribute("tipo", "success");

        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            flash.addFlashAttribute("tipo", "danger");
        }

        return redirectPath;
    }

    // ────────────────────────────────────────────────────────────────
    // 6. Cancelar préstamo (antes de que sea aprobado o activo)
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/cancelar/{id}")
    public String cancelarPrestamo(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            prestamoServicio.cancelarPrestamo(id);
            flash.addFlashAttribute("mensaje", "Préstamo cancelado correctamente");
            flash.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos/mios";
    }

    // ────────────────────────────────────────────────────────────────
    // 7. Detalle de un préstamo específico
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes flash) {

        Optional<Prestamo> opt = prestamoServicio.obtenerPrestamoPorId(id);
        if (opt.isEmpty()) {
            flash.addFlashAttribute("error", "Préstamo no encontrado");
            return "redirect:/prestamos/mios";
        }

        Prestamo prestamo = opt.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esDueño = prestamo.getUsuario().getEmail().equals(email);
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esDueño && !esAdmin) {
            flash.addFlashAttribute("error", "No tienes permiso para ver este préstamo");
            return "redirect:/prestamos/mios";
        }

        model.addAttribute("prestamo", prestamo);
        return "prestamos/detalle";
    }

    // ────────────────────────────────────────────────────────────────
    // 8 + 9. Edición de préstamo – SOLO ADMINISTRADORES
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes flash) {

        if (!SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            flash.addFlashAttribute("error", "Solo administradores pueden editar préstamos");
            return "redirect:/prestamos/mios";
        }

        Optional<Prestamo> prestamoOpt = prestamoServicio.obtenerPrestamoPorId(id);
        if (prestamoOpt.isEmpty()) {
            flash.addFlashAttribute("error", "Préstamo no encontrado");
            return "redirect:/prestamos";
        }

        Prestamo prestamo = prestamoOpt.get();
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("usuarios", usuarioServicio.obtenerTodosLosUsuarios());
        model.addAttribute("libros", libroServicio.obtenerTodosLosLibros());
        model.addAttribute("titulo", "Editar Préstamo #" + id);

        return "prestamos/formulario-editar";
    }

    @PostMapping("/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarPrestamo(
            @ModelAttribute Prestamo prestamo,
            RedirectAttributes flash) {

        try {
            if (!SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                flash.addFlashAttribute("error", "Solo administradores pueden editar préstamos");
                return "redirect:/prestamos/mios";
            }

            Prestamo actualizado = prestamoServicio.actualizarPrestamo(prestamo);
            flash.addFlashAttribute("mensaje", "Préstamo #" + actualizado.getId() + " actualizado correctamente");
            flash.addFlashAttribute("tipo", "success");

        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/prestamos";
    }

    // ────────────────────────────────────────────────────────────────
    // 10 + 11. Creación manual de préstamo – SOLO ADMINISTRADORES
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoPrestamo(Model model) {
        if (!SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/prestamos/mios";
        }

        model.addAttribute("prestamo", new Prestamo());
        model.addAttribute("usuarios", usuarioServicio.obtenerTodosLosUsuarios());
        model.addAttribute("librosDisponibles", libroServicio.obtenerLibrosDisponibles());
        return "prestamos/formulario-crear-admin";
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public String crearPrestamo(
            @ModelAttribute Prestamo prestamo,
            RedirectAttributes flash) {

        try {
            prestamoServicio.crearPrestamoManual(prestamo);
            flash.addFlashAttribute("mensaje", "Préstamo creado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }
}