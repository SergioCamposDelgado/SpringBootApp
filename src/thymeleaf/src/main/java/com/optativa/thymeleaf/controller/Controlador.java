package com.optativa.thymeleaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.optativa.thymeleaf.ThymeleafApplication;
import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.servicio.Servicio;



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
	//Prueba para ver como funcionaria si se le pasa una lista vacia
	public String listadoVacio(Model model) {
		List<Producto> listaProductos = new ArrayList<>();
		
		model.addAttribute("listaProductos", listaProductos);
		
		return "lista";
	}
	
	@GetMapping("/productos/{id}")
	public String vistaProducto (@PathVariable int id, Model model) {
		Producto producto = servicio.obtenerProductoPorId(id);
		
		model.addAttribute("producto", producto);
		
		return "vistaProducto";
	}
}
