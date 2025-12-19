package com.optativa.thymeleaf.servicio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.optativa.thymeleaf.entidad.Producto;

public class Servicio {

	private final List<Producto> listaProductos = new ArrayList<>();
	private int cont_id = 1;

	public Servicio() {
		agregarProducto(new Producto(0, "Pan", 1.10, "Alimentación"));
		agregarProducto(new Producto(0, "Leche", 0.95, "Alimentación"));
		agregarProducto(new Producto(0, "Café Molido", 3.80, "Alimentación"));
		agregarProducto(new Producto(0, "Auriculares", 29.95, "Electrónica"));
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
		for (int i = 0; i < listaProductos.size(); i++) {
			if (listaProductos.get(i).getId() == p.getId()) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			listaProductos.set(index, p);
		}
	}


}
