package com.optativa.thymeleaf.controlador;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.entidad.Usuario;
import com.optativa.thymeleaf.servicio.ProductoServicio;
import com.optativa.thymeleaf.servicio.UsuarioServicio;

@Controller
public class ProductoControlador {

  private final ProductoServicio productoServicio;
  private final UsuarioServicio usuarioServicio;

  public ProductoControlador(ProductoServicio productoServicio, UsuarioServicio usuarioServicio) {
    this.productoServicio = productoServicio;
    this.usuarioServicio = usuarioServicio;
  }

  @GetMapping("/productos")
  public String listado(Model model, @PageableDefault(size = 20) Pageable page) {

    Page<Producto> productos = productoServicio.obtenerProductoPorPagina(page);
    Usuario usuario = usuarioServicio.obtenerUsuarioConectado();

    model.addAttribute("usuario", usuario);
    model.addAttribute("productos", productos);
    model.addAttribute("total", productos.getTotalElements());

    return "lista";
  }
}