package com.optativa.thymeleaf;  // ← Paquete base de la aplicación
                               //   → Todas las clases suelen estar dentro de este paquete o subpaquetes

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (entry point) de la aplicación Spring Boot
 * 
 * Esta es la clase que contiene el método main() y es la que se ejecuta
 * cuando inicias la aplicación con java -jar o desde tu IDE.
 */
@SpringBootApplication                 //   Anotación más importante de Spring Boot
public class ThymeleafApplication {    //   El nombre de la clase no es obligatorio que sea "Application",
                                       //   pero es una convención muy común

    /**
     * Método principal (punto de entrada de la aplicación Java)
     * 
     * @param args argumentos que se pasan desde la línea de comandos (normalmente no se usan)
     */
    public static void main(String[] args) {
        
        // SpringApplication.run() → es el método que realmente inicia toda la aplicación Spring Boot
        // Internamente hace muchas cosas:
        // 1. Crea el contexto de aplicación (ApplicationContext)
        // 2. Escanea el classpath buscando componentes (@Component, @Service, @Repository, @Controller, etc.)
        // 3. Carga las propiedades (application.properties / application.yml)
        // 4. Inicializa el servidor web embebido (Tomcat por defecto, pero puede ser Jetty, Undertow)
        // 5. Arranca todos los beans y auto-configuraciones
        SpringApplication.run(ThymeleafApplication.class, args);
        
        // Después de esta línea la aplicación ya está corriendo y escuchando peticiones HTTP
        // (por defecto en http://localhost:9000)
    }
}