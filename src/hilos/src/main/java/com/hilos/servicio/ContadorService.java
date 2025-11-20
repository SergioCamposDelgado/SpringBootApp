package com.hilos.servicio;

import org.springframework.stereotype.Service;

@Service
public class ContadorService {

    private int contador = 0;

    public void reset() {
        contador = 0;
    }

    // 🚨 SIN sincronización → condición de carrera
    public void incrementarSinSync() {
        contador++; // aquí se produce la race condition
    }

    // ✅ CON sincronización
    public synchronized void incrementarSync() {
        contador++;
    }

    public int getContador() {
        return contador;
    }
}