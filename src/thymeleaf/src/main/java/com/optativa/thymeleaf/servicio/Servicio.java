package com.optativa.thymeleaf.servicio;

import java.util.ArrayList;
import java.util.List;

import com.optativa.thymeleaf.entidad.Producto;

public class Servicio {

	private final List<Producto> listaProductos = new ArrayList<>();
	private int cont_id = 1;

	public Servicio() {
	    // Alimentación (muchos baratos y algunos medios)
	    agregarProducto(new Producto(0, "Pan de molde", 1.20, "Alimentación"));
	    agregarProducto(new Producto(0, "Leche entera", 0.95, "Alimentación"));
	    agregarProducto(new Producto(0, "Café molido", 3.80, "Alimentación"));
	    agregarProducto(new Producto(0, "Arroz blanco", 1.50, "Alimentación"));
	    agregarProducto(new Producto(0, "Pasta espaguetis", 1.10, "Alimentación"));
	    agregarProducto(new Producto(0, "Aceite de oliva virgen extra", 8.90, "Alimentación"));
	    agregarProducto(new Producto(0, "Tomate frito", 1.40, "Alimentación"));
	    agregarProducto(new Producto(0, "Huevos (docena)", 2.80, "Alimentación"));
	    agregarProducto(new Producto(0, "Queso curado", 12.50, "Alimentación"));
	    agregarProducto(new Producto(0, "Jamón ibérico", 45.00, "Alimentación"));
	    agregarProducto(new Producto(0, "Salmón ahumado", 18.90, "Alimentación"));
	    agregarProducto(new Producto(0, "Vino tinto reserva", 22.50, "Alimentación"));

	    // Electrónica (varios rangos de precio)
	    agregarProducto(new Producto(0, "Auriculares Bluetooth", 29.95, "Electrónica"));
	    agregarProducto(new Producto(0, "Ratón inalámbrico", 15.99, "Electrónica"));
	    agregarProducto(new Producto(0, "Teclado mecánico RGB", 89.99, "Electrónica"));
	    agregarProducto(new Producto(0, "Monitor 24\" Full HD", 149.00, "Electrónica"));
	    agregarProducto(new Producto(0, "Smartphone básico", 199.00, "Electrónica"));
	    agregarProducto(new Producto(0, "Portátil gaming", 1299.00, "Electrónica"));
	    agregarProducto(new Producto(0, "Tablet 10\"", 279.99, "Electrónica"));
	    agregarProducto(new Producto(0, "Smartwatch premium", 349.00, "Electrónica"));
	    agregarProducto(new Producto(0, "Cámara DSLR", 749.00, "Electrónica"));

	    // Hogar y decoración
	    agregarProducto(new Producto(0, "Lámpara de mesa LED", 24.90, "Hogar"));
	    agregarProducto(new Producto(0, "Juego de sábanas", 35.00, "Hogar"));
	    agregarProducto(new Producto(0, "Aspirador robot", 299.00, "Hogar"));
	    agregarProducto(new Producto(0, "Sofá 3 plazas", 599.00, "Hogar"));
	    agregarProducto(new Producto(0, "Cafetera espresso", 89.90, "Hogar"));
	    agregarProducto(new Producto(0, "Planta artificial grande", 42.50, "Hogar"));

	    // Deportes y ocio
	    agregarProducto(new Producto(0, "Balón de fútbol", 19.99, "Deportes"));
	    agregarProducto(new Producto(0, "Bicicleta estática", 219.00, "Deportes"));
	    agregarProducto(new Producto(0, "Raqueta de tenis", 79.90, "Deportes"));
	    agregarProducto(new Producto(0, "Set de pesas", 65.00, "Deportes"));

	    // Moda y complementos
	    agregarProducto(new Producto(0, "Camiseta básica", 12.90, "Moda"));
	    agregarProducto(new Producto(0, "Zapatillas deportivas", 69.99, "Moda"));
	    agregarProducto(new Producto(0, "Reloj analógico", 149.00, "Moda"));
	    agregarProducto(new Producto(0, "Bolso de cuero", 89.90, "Moda"));
	    agregarProducto(new Producto(0, "Gafas de sol polarizadas", 59.00, "Moda"));

	    // Libros y cultura
	    agregarProducto(new Producto(0, "Libro novela bestseller", 18.90, "Libros"));
	    agregarProducto(new Producto(0, "Agenda 2026", 14.50, "Libros"));
	    agregarProducto(new Producto(0, "Auriculares noise-cancelling", 249.00, "Electrónica"));
	}

	public void agregarProducto(Producto p) {
		p.setId(cont_id++);
		listaProductos.add(p);
	}

	public List<Producto> obtenerProductos() {
		return listaProductos;
	}

	public Producto obtenerProductoPorId(int id) {
		return listaProductos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
	}

	public void actualizarProducto(Producto p) {
		int index = -1;
		boolean encontrado = false;
		for (int i = 0; i < listaProductos.size() && !encontrado; i++) {
			if (listaProductos.get(i).getId() == p.getId()) {
				index = i;
				encontrado = true;
			}
		}
		if (index != -1) {
			listaProductos.set(index, p);
		}
	}


}
