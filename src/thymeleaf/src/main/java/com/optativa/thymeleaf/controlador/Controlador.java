package com.optativa.thymeleaf.controlador;

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
	public String saludo(@RequestParam(required = false, defaultValue = "Test") String name, Model modelo) {
		System.out.println("#####  Entra en /saluda");
		//String name = "Manuel";
		modelo.addAttribute("nombre", name);
		
		return "saludo";
	}
	
	@GetMapping("/productos")
	public String listado(Model model) {
	
		
	
		List<Producto> listaProductos = new ArrayList<Producto>();
		//String nombre, int precio, String categoria
		Producto p1 = new Producto("Pan", 1, "Alimentación");
		Producto p2 = new Producto("PC", 3000, "Componentes");
		listaProductos.add(p1);
		listaProductos.add(p2);
	
		model.addAttribute("listaProductos", listaProductos);
		
		return "lista";
	}
}
