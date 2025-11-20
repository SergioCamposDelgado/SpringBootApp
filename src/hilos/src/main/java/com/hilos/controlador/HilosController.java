package com.hilos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HilosController {

    @GetMapping("/hilos")
    public String verHilos(Model model) {

        // Hilo que atiende la petición (Tomcat)
        String requestThread = Thread.currentThread().getName();

        // 🧵 Hilo 1: "Analizador-logs"
        Thread analizadorLogs = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("[Analizador-logs] Analizando logs... paso " + i);
                    /**   #####################
                     *    #🐧SIMULACION TAREA #
                     *    #####################
                     */
                    Thread.sleep(2000); // Simula trabajo pesado
                    /** Fin simulación🐧*/
                }
                System.out.println("[Analizador-logs] Análisis completado.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Analizador-logs] Hilo interrumpido.");
            }
        });
        analizadorLogs.setName("Analizador-logs");
        analizadorLogs.setPriority(Thread.NORM_PRIORITY);

        // 🧵 Hilo 2: "Generador-informes"
        Thread generadorInformes = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("[Generador-informes] Generando informe... paso " + i);
                    /**   #####################
                     *    #🐧SIMULACION TAREA #
                     *    #####################
                     */
                    Thread.sleep(1500);
                    /** Fin simulación🐧*/
                }
                System.out.println("[Generador-informes] Informe listo.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Generador-informes] Hilo interrumpido.");
            }
        });
        generadorInformes.setName("Generador-informes");
        generadorInformes.setPriority(Thread.MIN_PRIORITY);

        // ✅ Iniciamos los hilos
        analizadorLogs.start();
        generadorInformes.start();

        // Datos para mostrar en la vista
        model.addAttribute("requestThread", requestThread);
        model.addAttribute("analizadorName", analizadorLogs.getName());
        model.addAttribute("analizadorPriority", analizadorLogs.getPriority());
        model.addAttribute("generadorName", generadorInformes.getName());
        model.addAttribute("generadorPriority", generadorInformes.getPriority());

        return "debug-hilos"; // debug-hilos.html en templates
    }
}
