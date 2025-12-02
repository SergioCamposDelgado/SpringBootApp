package com.optativa.thymeleaf.controlador;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.servicio.Servicio;



@Controller
public class Controlador {
	
	private Servicio servicio;
	
	public Controlador(Servicio servicio) {
		this.servicio = servicio;
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
	 
		model.addAttribute("listaProductos", servicio.obtenerProductos());
		
		return "lista";
	}
	
	 @GetMapping("/productos/{id}")
	 public String obtenerProducto(@PathVariable int id, Model model) {
		 Producto p = servicio.obtenerProductoPorId(id);
		 model.addAttribute("producto", p);
		 return "vista";
	 }
	 @GetMapping("/formulario")
	 public String mostrarForm(Model model) {
		    model.addAttribute("producto", new Producto());
		    return "formulario";
		}
	 @PostMapping("/formulario")
	 public String obtenerFormulario(Producto producto, Model model) {
		 System.out.println(producto.toString());
		 servicio.agregarProducto(producto);
			model.addAttribute("listaProductos", servicio.obtenerProductos());

		 return "lista";
	 }
}
