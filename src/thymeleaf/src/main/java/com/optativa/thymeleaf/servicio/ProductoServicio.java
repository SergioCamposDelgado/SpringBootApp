package com.optativa.thymeleaf.servicio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.optativa.thymeleaf.entidad.Producto;

public interface ProductoServicio {
	Producto obtenerProductoPorId(int id);
	void agregarProducto(Producto producto);
	void actualizarProducto(Producto producto);
	void eliminarProducto(int id);
	/** Listado */
	List<Producto> obtenerProductos();
	/** Paginación */
	Page<Producto> obtenerProductoPorPagina(Pageable pageable);
	
	
	
}
