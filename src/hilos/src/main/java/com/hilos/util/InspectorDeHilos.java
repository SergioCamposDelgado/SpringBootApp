package com.hilos.util;
/**
 * La clase InspectorDeHilos inspecciona todos los hilos de la JVM usando ThreadGroup.
Funciona sin crear objetos porque sus métodos son estáticos.
Listar hilos es una herramienta profesional para entender la arquitectura interna de Spring Boot, Tomcat y la JVM.
 */
public class InspectorDeHilos {

    /**
     * Lista todos los hilos activos en la JVM (incluye Spring, Tomcat, LiveReload,
     * hilos propios, GC, Finalizer, etc.).
     * 
     * 
     */
    public static void listarHilos() {

        ThreadGroup rootGroup = obtenerRootThreadGroup();

        int tamaño = rootGroup.activeCount() * 2;
        Thread[] todos = new Thread[tamaño];

        int numHilos = rootGroup.enumerate(todos, true);

        System.out.println("===== LISTADO DE HILOS ACTIVOS EN LA JVM =====");
        for (int i = 0; i < numHilos; i++) {
            Thread t = todos[i];
            System.out.printf(
                    "Hilo: %-30s  | Estado: %-12s | Daemon: %-5s | Prioridad: %d%n",
                    t.getName(),
                    t.getState(),
                    t.isDaemon(),
                    t.getPriority()
            );
        }
        System.out.println("==============================================");
    }

    /**
     * Obtiene el ThreadGroup raíz (root).
     */
    private static ThreadGroup obtenerRootThreadGroup() {
        ThreadGroup grupo = Thread.currentThread().getThreadGroup();
        ThreadGroup padre;

        while ((padre = grupo.getParent()) != null) {
            grupo = padre; // subimos en la jerarquía
        }
        return grupo;
    }
}
