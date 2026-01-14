package com.optativa.thymeleaf.converter;

import com.optativa.thymeleaf.entidad.Producto;
import com.optativa.thymeleaf.servicio.Servicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProductoConverter implements Converter<String, Producto> {

    @Autowired
    private Servicio servicio;

    @Override
    public Producto convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            int id = Integer.parseInt(source);
            return servicio.obtenerProductoPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}