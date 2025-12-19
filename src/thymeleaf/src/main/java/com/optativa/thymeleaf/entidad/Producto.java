package com.optativa.thymeleaf.entidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Producto implements Comparable<Producto> {

	private int id;

	@NotBlank (message = "No puede tener el nombre vacio")
	private String nombre;
	
	@Positive (message = "El precio debe ser superior a 0")
	private double precio;
	
	@NotBlank (message = "No puede tener la categoria vacia")
	private String categoria;

	public Producto() {

	}

	public Producto(int id, String nombre, double precio, String categoria) {
		this.setId(id);
		this.setNombre(nombre);
		this.setPrecio(precio);
		this.setCategoria(categoria);
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Override
	public String toString() {
		return "Producto " + this.getNombre() + " " + this.getPrecio() + "€ " + this.getCategoria();
	}

	@Override
	public int compareTo(Producto o) {
		return this.getNombre().toLowerCase().compareTo(o.getNombre().toLowerCase());
	}
}
