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

@Component
public class IniciarDatos {

    private static final int NUM_AUTORES = 8;
    private static final int NUM_CATEGORIAS = 6;
    private static final int NUM_LIBROS = 25;
    private static final int NUM_USUARIOS = 5;

    private final AutorServicio autorServicio;
    private final CategoriaServicio categoriaServicio;
    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(new Locale("es")); // o Locale("en") si prefieres inglés

    public IniciarDatos(
            AutorServicio autorServicio,
            CategoriaServicio categoriaServicio,
            LibroServicio libroServicio,
            UsuarioServicio usuarioServicio,
            PasswordEncoder passwordEncoder) {
        this.autorServicio = autorServicio;
        this.categoriaServicio = categoriaServicio;
        this.libroServicio = libroServicio;
        this.usuarioServicio = usuarioServicio;
        this.passwordEncoder = passwordEncoder;

    }

    @PostConstruct
    public void init() {
        System.out.println("######### Iniciando datos de prueba para Biblioteca ###########");

        if (libroServicio.obtenerTodosLosLibros().isEmpty()) {
            crearCategorias();
            crearAutores();
            crearLibros();
            crearUsuarios();
            System.out.println("Datos de prueba generados correctamente.");
        } else {
            System.out.println("La base de datos ya tiene datos → se omite la generación.");
        }
    }

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

    private void crearAutores() {
        for (int i = 0; i < NUM_AUTORES; i++) {
            Autor autor = new Autor();
            autor.setNombre(faker.name().firstName());
            autor.setApellido(faker.name().lastName());
            autor.setNacionalidad(faker.address().country());
            // Fecha de nacimiento aleatoria entre 1920 y 1990
            LocalDate nacimiento = faker.date()
                    .birthday(1920, 1990)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            autor.setFechaNacimiento(nacimiento);

            autorServicio.guardarAutor(autor);
        }
        System.out.println("→ Autores creados: " + NUM_AUTORES);
    }

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
            libro.setIsbn(faker.regexify("[0-9]{13}")); // ISBN-13 aproximado
            libro.setAñoPublicacion(faker.number().numberBetween(1950, 2025));
            libro.setSinopsis(faker.lorem().paragraph(2));
            libro.setDisponible(true);

            // Asignar autor aleatorio
            Autor autorRandom = autores.get(faker.number().numberBetween(0, autores.size()));
            libro.setAutor(autorRandom);

            // Asignar 1–3 categorías aleatorias
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

    private void crearUsuarios() {
        // Usuario ADMIN
        Usuario admin = new Usuario();
        admin.setEmail("admin@biblioteca.com");
        admin.setPassword(passwordEncoder.encode("1234"));      // ← ¡cámbialo y codifícalo cuando uses Security!
        admin.setNombreCompleto("Administrador Principal");
        admin.setRol(Rol.ADMIN);
        usuarioServicio.guardarUsuario(admin);

        // Usuario LECTOR normal
        Usuario lector = new Usuario();
        lector.setEmail("lector@biblioteca.com");
        lector.setPassword(passwordEncoder.encode("1234"));
        lector.setNombreCompleto("Juan Lector Pérez");
        lector.setRol(Rol.LECTOR);
        usuarioServicio.guardarUsuario(lector);

        // Usuarios faker adicionales
        for (int i = 0; i < NUM_USUARIOS - 2; i++) {
            Usuario u = new Usuario();
            u.setEmail(faker.internet().emailAddress());
            u.setPassword(passwordEncoder.encode("1234")); // temporal
            u.setNombreCompleto(faker.name().fullName());
            u.setRol(Rol.LECTOR);
            usuarioServicio.guardarUsuario(u);
        }

        System.out.println("→ Usuarios creados: " + (NUM_USUARIOS + 1)); // +1 por el admin
    }
}