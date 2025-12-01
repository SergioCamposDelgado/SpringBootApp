package com.optativa.thymeleaf.servicio;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.optativa.thymeleaf.entidad.Producto;
public class Servicio {

	private List<Producto> listaProductos;
	
	public Servicio() {
		listaProductos = new ArrayList<>();
	}
	
	public List<Producto> obtenerProductos() {
		listaProductos = new ArrayList<>();
		
		this.agregarProducto(new Producto(1, "Portatil", 1000, "Informatica"));
		this.agregarProducto(new Producto(2, "PC", 2000, "Informatica"));
		this.agregarProducto(new Producto(3, "Manzanas verdes", 200, "Comida"));
		
		listaProductos.sort(null);
		
		return listaProductos;
	}
	
	public void agregarProducto(Producto p) {
		if (p != null) {
			listaProductos.add(p);
		}
		
	}
	
	public Producto obtenerProductoPorId(int id) {
		 for (Producto p : listaProductos) {
			 if (p.getId() == id) {
				return p;
			}
		 }
		 
		 return null;
	}
	
}
