package com.optativa.thymeleaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.optativa.thymeleaf.ThymeleafApplication;
import com.optativa.thymeleaf.entidad.Producto;



@Controller
public class Controlador {

    private final ThymeleafApplication thymeleafApplication;

    Controlador(ThymeleafApplication thymeleafApplication) {
        this.thymeleafApplication = thymeleafApplication;
    }

	@GetMapping("/saluda")
	public String saludo(@RequestParam(required = false, defaultValue = "Anonimo") String name, Model modelo) {
		System.out.println("#####  Entra en /saluda");
		//String name = "Manuel";
		modelo.addAttribute("nombre", name);
		
		return "saludo";
	}
	
	@GetMapping("/productos")
	public String listado(Model model) {
		List<Producto> listaProductos = new ArrayList<Producto>();
		
		listaProductos.add(new Producto("Portatil", 1000, "Informatica"));
		listaProductos.add(new Producto("PC", 2000, "Informatica"));
		listaProductos.add(new Producto("Manzanas verdes", 200, "Comida"));
		
		listaProductos.sort(null);
		
		model.addAttribute("listaProductos", listaProductos);
		
		return "lista";
	}
}
