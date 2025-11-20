package com.hilos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hilos.servicio.ContadorService;

@Controller
@RequestMapping("/carrera")
public class CondicionCarreraController {

    private final ContadorService contadorService;

    public CondicionCarreraController(ContadorService contadorService) {
        this.contadorService = contadorService;
    }

    // 🔴 1) PRUEBA SIN sincronización (provoca condición de carrera)
    @GetMapping("/sin-sync")
    public String carreraSinSincronizar(Model model) throws InterruptedException {

        int iteraciones = 100_000;
        contadorService.reset();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iteraciones; i++) {
                contadorService.incrementarSinSync();
            }
        }, "carrera-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iteraciones; i++) {
                contadorService.incrementarSinSync();
            }
        }, "carrera-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        int esperado = iteraciones * 2;
        int obtenido = contadorService.getContador();

        model.addAttribute("tipoPrueba", "Sin sincronización (condición de carrera)");
        model.addAttribute("iteraciones", iteraciones);
        model.addAttribute("esperado", esperado);
        model.addAttribute("obtenido", obtenido);

        return "carrera"; // carrera.html
    }

    // ✅ 2) PRUEBA CON synchronized (solución)
    @GetMapping("/con-sync")
    public String carreraConSincronizacion(Model model) throws InterruptedException {

        int iteraciones = 100000;
        contadorService.reset();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iteraciones; i++) {
                contadorService.incrementarSync();
            }
        }, "carrera-sync-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iteraciones; i++) {
                contadorService.incrementarSync();
            }
        }, "carrera-sync-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        int esperado = iteraciones * 2;
        int obtenido = contadorService.getContador();

        model.addAttribute("tipoPrueba", "Con synchronized (solución a la condición de carrera)");
        model.addAttribute("iteraciones", iteraciones);
        model.addAttribute("esperado", esperado);
        model.addAttribute("obtenido", obtenido);

        return "carrera"; // misma vista
    }
}