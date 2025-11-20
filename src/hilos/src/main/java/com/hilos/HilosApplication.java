package com.hilos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hilos.util.InspectorDeHilos;

@SpringBootApplication
public class HilosApplication {

	public static void main(String[] args) {
		SpringApplication.run(HilosApplication.class, args);
		InspectorDeHilos.listarHilos();
	}

}
