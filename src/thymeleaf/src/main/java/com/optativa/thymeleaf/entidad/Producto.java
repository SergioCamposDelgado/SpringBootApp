package com.optativa.thymeleaf.entidad;

public class Producto implements Comparable<Producto>{

	private int id;
	private String nombre;
	private double precio;
	private String categoria;

	public Producto () {

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
		return "Producto " + this.getNombre()  + " " + this.getPrecio() + "€ " + this.getCategoria();
	}

	@Override
	public int compareTo(Producto o) {
		return this.getNombre().toLowerCase().compareTo(o.getNombre().toLowerCase());
	}
}
