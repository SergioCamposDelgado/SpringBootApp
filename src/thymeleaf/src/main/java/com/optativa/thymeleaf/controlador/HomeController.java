package com.optativa.thymeleaf.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    // Página principal pública (sin login obligatorio)
    @GetMapping({"/", "/home", ""})
    public String home(Model model) {
        model.addAttribute("titulo", "Bienvenido a la Biblioteca Online");
        return "home";  // tu vista home.html
    }

    // Página de login (GET) - Spring Security redirige aquí automáticamente
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("titulo", "Iniciar Sesión");
        return "login";  // tu vista login.html
    }

    // Página de acceso denegado (403) - útil para cuando alguien intenta entrar sin rol
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";  // crea esta vista si quieres
    }
}