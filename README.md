📚 Sistema de Gestión de Biblioteca (Thymeleaf + Spring Boot)
Un sistema de gestión bibliotecaria robusto desarrollado con Java y Spring Boot. Permite el control total sobre el catálogo de libros, la gestión de usuarios (lectores y administradores) y el ciclo de vida completo de los préstamos.

✨ Características Principales
Gestión de Libros: CRUD completo con control de stock y disponibilidad.

Gestión de Préstamos: Registro de préstamos con estados dinámicos (ACTIVO, DEVUELTO, VENCIDO, CANCELADO).

Seguridad: Autenticación y autorización basada en roles con Spring Security.

ADMIN: Acceso total a la gestión y herramientas de administración.

LECTOR: Vista personal de préstamos y consulta de catálogo.

Interfaz Moderna: Diseño responsivo utilizando Bootstrap 5 y Bootstrap Icons.

Carga Inicial: Generación automática de datos de prueba (libros, usuarios y préstamos) mediante JavaFaker.

🛠️ Tecnologías Utilizadas
Backend: Java 17+, Spring Boot 4.x, Spring Data JPA.

Frontend: Thymeleaf, Bootstrap 5.

Base de Datos: H2 (Memoria)

Seguridad: Spring Security.

Otros: Lombok, JavaFaker, Maven.

🚀 Instrucciones de Ejecución
1. Requisitos Previos
JDK 17 o superior.

Maven (opcional, puedes usar el wrapper ./mvnw incluido).

2. Clonar y Configurar
Bash
git clone https://github.com/SergioCamposDelgado/SpringBootApp.git
cd SpringBootApp
3. Ejecutar la Aplicación
Puedes arrancar el proyecto desde tu IDE favorito o por terminal:

Bash
./mvnw spring-boot:run
La aplicación estará disponible en: http://localhost:9000

4. Credenciales de Acceso (Datos por defecto)
Al iniciar, el sistema carga automáticamente usuarios de prueba:

Administrador: admin@biblioteca.com / 1234

Lector: lector@biblioteca.com / 1234

📂 Estructura del Proyecto
src/main/java: Contiene la lógica de negocio, controladores, entidades y servicios.

src/main/resources/templates: Vistas .html organizadas por módulos (libros, préstamos, usuarios).

src/main/resources/static: Archivos estáticos (CSS, JS, Imágenes).

src/main/resources/application.properties: Configuración de base de datos y puertos.
