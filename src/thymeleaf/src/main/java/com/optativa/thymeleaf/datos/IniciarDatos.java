package com.optativa.thymeleaf.datos;

import com.github.javafaker.Faker;
import com.optativa.thymeleaf.entidad.*;
import com.optativa.thymeleaf.entidad.enumerado.Rol;
import com.optativa.thymeleaf.servicio.*;
import jakarta.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Clase de inicialización de datos de prueba (data seeding / bootstrap)
 * 
 * Se ejecuta automáticamente una sola vez al arrancar la aplicación
 * (gracias a @PostConstruct) SI la base de datos está vacía de libros.
 * 
 * Utiliza la librería JavaFaker para generar datos realistas en español.
 */
@Component                                      // Registrado como bean → Spring lo detecta y ejecuta
public class IniciarDatos {

    // Constantes para controlar cuántos registros crear
    private static final int NUM_AUTORES    = 10;
    private static final int NUM_CATEGORIAS = 10;
    private static final int NUM_LIBROS     = 40;
    private static final int NUM_USUARIOS   = 5;
    private static final int NUM_PRESTAMOS  = 10;

    // Dependencias inyectadas
    private final AutorServicio autorServicio;
    private final CategoriaServicio categoriaServicio;
    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder passwordEncoder;
    private final PrestamoServicio prestamoServicio;

    // Instancia de Faker configurada en español (nombres, textos, etc. más naturales)
    private final Faker faker = new Faker(new Locale("es")); 
    // Alternativa: new Faker() → inglés por defecto
    // o new Faker(Locale("es-MX"), Locale("es")) para mezclar

    public IniciarDatos(
            AutorServicio autorServicio,
            CategoriaServicio categoriaServicio,
            LibroServicio libroServicio,
            UsuarioServicio usuarioServicio,
            PasswordEncoder passwordEncoder,
            PrestamoServicio prestamoServicio) {
        this.autorServicio = autorServicio;
        this.categoriaServicio = categoriaServicio;
        this.libroServicio = libroServicio;
        this.usuarioServicio = usuarioServicio;
        this.passwordEncoder = passwordEncoder;
        this.prestamoServicio = prestamoServicio;
    }

    /**
     * Método que se ejecuta automáticamente después de que el bean se construye
     * y todas las dependencias están inyectadas.
     * 
     * Es el equivalente a un "main" de inicialización en aplicaciones Spring.
     */
    @PostConstruct
    public void init() {
        System.out.println("######### Iniciando datos de prueba para Biblioteca ###########");

        // Evitamos regenerar datos si ya existen registros
        // (criterio simple: si no hay libros → asumimos BD vacía)
        if (libroServicio.obtenerTodosLosLibros().isEmpty()) {
            crearCategorias();
            crearAutores();
            crearLibros();
            crearUsuarios();
            crearPrestamos();
            System.out.println("Datos de prueba generados correctamente.");
        } else {
            System.out.println("La base de datos ya tiene datos → se omite la generación.");
        }
    }

    /**
     * Crea un conjunto fijo de categorías (no usa Faker porque queremos nombres controlados)
     */
    private void crearCategorias() {
        String[] nombresCategorias = {
                "Novela", "Ciencia Ficción", "Fantasía", "Misterio", "Histórica", "Autoayuda"
        };

        for (String nombre : nombresCategorias) {
            Categoria cat = new Categoria();
            cat.setNombre(nombre);
            categoriaServicio.guardarCategoria(cat);
        }
        System.out.println("→ Categorías creadas: " + NUM_CATEGORIAS);
    }

    /**
     * Genera autores con datos aleatorios pero realistas
     */
    private void crearAutores() {
        for (int i = 0; i < NUM_AUTORES; i++) {
            Autor autor = new Autor();
            autor.setNombre(faker.name().firstName());
            autor.setApellido(faker.name().lastName());
            autor.setNacionalidad(faker.address().country());

            // Fecha de nacimiento entre 1920 y 1990 (más o menos autores vivos o recientes)
            LocalDate nacimiento = faker.date()
                    .birthday(1920, 1990)                    // minAge, maxAge
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            autor.setFechaNacimiento(nacimiento);

            autorServicio.guardarAutor(autor);
        }
        System.out.println("→ Autores creados: " + NUM_AUTORES);
    }

    /**
     * Crea libros asignando autores y categorías de forma aleatoria
     */
    private void crearLibros() {
        List<Autor> autores = autorServicio.obtenerTodosLosAutores();
        List<Categoria> categorias = categoriaServicio.obtenerTodasLasCategorias();

        if (autores.isEmpty() || categorias.isEmpty()) {
            System.out.println("No hay autores o categorías → no se crean libros");
            return;
        }

        for (int i = 0; i < NUM_LIBROS; i++) {
            Libro libro = new Libro();
            libro.setTitulo(faker.book().title());
            libro.setIsbn(faker.regexify("[0-9]{13}"));           // ISBN-13 simplificado
            libro.setAñoPublicacion(faker.number().numberBetween(1950, 2025));
            libro.setSinopsis(faker.lorem().paragraph(2));        // 2 párrafos de texto lorem
            libro.setDisponible(true);

            // Autor aleatorio de los existentes
            Autor autorRandom = autores.get(faker.number().numberBetween(0, autores.size()));
            libro.setAutor(autorRandom);

            // 1 a 3 categorías aleatorias (sin repetir gracias al Set)
            Set<Categoria> catsLibro = new HashSet<>();
            int numCats = faker.number().numberBetween(1, 4);
            for (int j = 0; j < numCats; j++) {
                Categoria catRandom = categorias.get(faker.number().numberBetween(0, categorias.size()));
                catsLibro.add(catRandom);
            }
            libro.setCategorias(catsLibro);

            libroServicio.guardarLibro(libro);
        }
        System.out.println("→ Libros creados: " + NUM_LIBROS);
    }

    /**
     * Crea usuarios de prueba:
     *   - 1 administrador fijo
     *   - 1 lector fijo (para pruebas rápidas)
     *   - Resto de usuarios generados con Faker
     */
    private void crearUsuarios() {
        // Usuario ADMIN (credenciales fijas para desarrollo)
        Usuario admin = new Usuario();
        admin.setEmail("admin@biblioteca.com");
        admin.setPassword(passwordEncoder.encode("1234"));      // ← NUNCA dejes "1234" en producción
        admin.setNombreCompleto("Administrador Principal");
        admin.setRol(Rol.ADMIN);
        usuarioServicio.guardarUsuario(admin);

        // Usuario LECTOR de prueba (fácil de recordar)
        Usuario lector = new Usuario();
        lector.setEmail("lector@biblioteca.com");
        lector.setPassword(passwordEncoder.encode("1234"));
        lector.setNombreCompleto("Juan Lector Pérez");
        lector.setRol(Rol.LECTOR);
        usuarioServicio.guardarUsuario(lector);

        // Usuarios aleatorios adicionales (solo LECTOR)
        for (int i = 0; i < NUM_USUARIOS - 2; i++) {
            Usuario u = new Usuario();
            u.setEmail(faker.internet().emailAddress());
            u.setPassword(passwordEncoder.encode("1234")); // contraseña temporal
            u.setNombreCompleto(faker.name().fullName());
            u.setRol(Rol.LECTOR);
            usuarioServicio.guardarUsuario(u);
        }

        // Nota: el +1 es por el admin (no estaba en el contador NUM_USUARIOS)
        System.out.println("→ Usuarios creados: " + (NUM_USUARIOS + 1));
    }
    
    private void crearPrestamos() {
        // 1. Obtenemos libros y usuarios
        List<Libro> libros = libroServicio.obtenerTodosLosLibros();
        List<Usuario> usuarios = usuarioServicio.obtenerTodosLosUsuarios();

        // 2. Creamos una lista MUTABLE de libros que están disponibles
        // Usamos ArrayList para poder usar el método .remove()
        List<Libro> poolLibrosDisponibles = new ArrayList<>(libros.stream()
                .filter(Libro::getDisponible)
                .toList());

        if (poolLibrosDisponibles.isEmpty() || usuarios.isEmpty()) return;

        // Ajustamos el número de préstamos para no intentar pedir más libros de los que hay
        int totalAcrear = Math.min(NUM_PRESTAMOS, poolLibrosDisponibles.size());

        for (int i = 0; i < totalAcrear; i++) {
            Prestamo p = new Prestamo();
            
            // Seleccionamos usuario al azar
            Usuario usuarioAleatorio = usuarios.get(faker.number().numberBetween(0, usuarios.size()));
            
            // Seleccionamos un libro al azar del pool de disponibles y lo extraemos
            // Al usar .remove(index), el libro ya no estará disponible para la siguiente iteración
            int indexAleatorio = faker.number().numberBetween(0, poolLibrosDisponibles.size());
            Libro libroAleatorio = poolLibrosDisponibles.remove(indexAleatorio);

            p.setUsuario(usuarioAleatorio);
            p.setLibro(libroAleatorio);

            // --- Configuración de fechas ---
            LocalDate fechaPrestamo = faker.date().past(60, java.util.concurrent.TimeUnit.DAYS)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            p.setFechaPrestamo(fechaPrestamo);
            p.setFechaDevolucionPrevista(fechaPrestamo.plusDays(14));

            // --- Lógica de Estados ---
            int azar = faker.number().numberBetween(0, 3); 

            if (azar == 0) { // DEVUELTO
                p.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);
                p.setFechaDevolucionReal(fechaPrestamo.plusDays(faker.number().numberBetween(1, 16)));
                libroAleatorio.setDisponible(true);
                // Si se devuelve, podrías volver a añadirlo al pool si quisieras, 
                // pero para datos de prueba es más limpio dejarlo fuera una vez usado.
            } 
            else if (azar == 1) { // ACTIVO o VENCIDO
                p.setEstado(Prestamo.EstadoPrestamo.ACTIVO);
                libroAleatorio.setDisponible(false); 
                
                if (LocalDate.now().isAfter(p.getFechaDevolucionPrevista())) {
                    p.setEstado(Prestamo.EstadoPrestamo.VENCIDO);
                }
            } 
            else { // CANCELADO
                p.setEstado(Prestamo.EstadoPrestamo.CANCELADO);
                libroAleatorio.setDisponible(true);
            }

            // Guardamos los cambios
            // Usamos directamente el repositorio o un método de guardado simple para evitar las validaciones 
            // de "ahora" del servicio (ya que estamos generando fechas del pasado)
            prestamoServicio.guardarPrestamo(p);
            libroServicio.guardarLibro(libroAleatorio);
        }
        System.out.println("→ Préstamos creados exitosamente: " + totalAcrear);
    }
}