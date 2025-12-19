package com.optativa.thymeleaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.servicio.Servicio;

import jakarta.validation.Valid;

@Controller
public class Controlador {

	private Servicio servicio;

	Controlador() {
		this.servicio = new Servicio();
	}

	@GetMapping("/saluda")
	public String saludo(@RequestParam(required = false, defaultValue = "Anonimo") String name, Model modelo) {

		modelo.addAttribute("nombre", name);

		return "saludo";
	}

	@GetMapping("/productos")
	public String listado(Model model) {

		model.addAttribute("listaProductos", servicio.obtenerProductos());

		return "lista";
	}

	@GetMapping("/productosVacios")
	// Prueba para ver como funcionaria si se le pasa una lista vacia
	public String listadoVacio(Model model) {
		List<Producto> listaProductos = new ArrayList<>();

		model.addAttribute("listaProductos", listaProductos);

		return "lista";
	}

	@GetMapping("/productos/{id}")
	public String vistaProducto(@PathVariable int id, Model model) {
		Producto producto = servicio.obtenerProductoPorId(id);

		model.addAttribute("producto", producto);

		return "vistaProducto";
	}

	@GetMapping("/formulario")
	public String mostrarForm(Model model) {
		model.addAttribute("producto", new Producto());
		return "form";
	}

	@PostMapping("/formulario")
	public String obtenerFormulario(@Valid @ModelAttribute("producto") Producto producto, BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "form";
		} else if (servicio.obtenerProductoPorId(producto.getId()) == null) {
			servicio.agregarProducto(producto);
			model.addAttribute("listaProductos", servicio.obtenerProductos());
		} else {
			servicio.actualizarProducto(producto);
		}
		return "redirect:/productos";
	}
	
	@GetMapping("/productos/{id}/editar")
	public String editarProducto(@PathVariable int id, Model model) {
		Producto producto = servicio.obtenerProductoPorId(id);

		model.addAttribute("producto", producto);

		return "form";
	}
}
