package com.optativa.thymeleaf.servicio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.optativa.thymeleaf.entidad.Producto;
/**
 * FAKE SERVICIO
 */
@Service
public class ProductoServicioImpl implements ProductoServicio{

    private final List<Producto> listaProductos = new ArrayList<>();
    private int cont_id = 1;

    public ProductoServicioImpl() {
        agregarProducto(new Producto("Pan", 1.10, "Alimentación"));
        agregarProducto(new Producto( "Leche", 0.95, "Alimentación"));
        agregarProducto(new Producto(  "Café Molido", 3.80, "Alimentación"));
        agregarProducto(new Producto( "Auriculares", 29.95, "Electrónica"));
    }
    

    public void agregarProducto(Producto p) {
        p.setId(cont_id++);
        listaProductos.add(p);
    }

    public List<Producto> obtenerProductos() {
        return listaProductos;
    }

    public Producto obtenerProductoPorId(int id) {
        return listaProductos
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }


	@Override
	public void actualizarProducto(Producto producto) {
	    eliminarProducto(producto.getId());
	    listaProductos.add(producto);
	}


	@Override
	public void eliminarProducto(int id) {
		listaProductos.removeIf(p -> p.getId() == id);
	}
}
