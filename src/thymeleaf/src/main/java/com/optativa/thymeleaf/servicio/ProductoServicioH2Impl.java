package com.optativa.thymeleaf.servicio;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.repositorio.ProductoRepositorio;

@Primary
@Service
public class ProductoServicioH2Impl implements ProductoServicio {

	private ProductoRepositorio repositorio;
	
	public ProductoServicioH2Impl(ProductoRepositorio repositorio) {
		this.repositorio =repositorio;
	}
	
	@Override
	public List<Producto> obtenerProductos() {
		return repositorio.findAll();
	}

	@Override
	public Producto obtenerProductoPorId(int id) {
		return repositorio.getReferenceById(id);
	}

	@Override
	public void agregarProducto(Producto producto) {
		repositorio.save(producto);
	}

	@Override
	public void actualizarProducto(Producto producto) {
		if(repositorio.existsById(producto.getId())) {
			repositorio.save(producto);
		}
	}

	@Override
	public void eliminarProducto(int id) {
		repositorio.deleteById(id);
		
	}

}
