package com.optativa.thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.optativa.thymeleaf.ThymeleafApplication;



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
}
