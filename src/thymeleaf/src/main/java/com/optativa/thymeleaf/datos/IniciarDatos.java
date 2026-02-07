package com.optativa.thymeleaf.datos;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.servicio.ProductoServicio;

import jakarta.annotation.PostConstruct;

@Component
public class IniciarDatos {

	private final int TOTAL_PRUEBAS = 10;
	private ProductoServicio productoServicio;

	
	public IniciarDatos(ProductoServicio productoServicio) {
		this.productoServicio = productoServicio;
		
	}
	
	
	@PostConstruct
	public void init() {
		Faker faker = new Faker();
		System.out.println("######### FAKE IniciarDatos.init()###########");
		for(int i=0; i<TOTAL_PRUEBAS; i++) {
			Producto p = new Producto();
			p.setCategoria(faker.app().name());
			p.setPrecio(faker.number().randomDouble(2, 1, 32));
			p.setNombre(faker.superhero().name());
			
			productoServicio.agregarProducto(p);
		}
		
	}
}
