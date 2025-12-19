package com.optativa.thymeleaf.entidad;

import java.text.DecimalFormat;

public class Temperatura {

	public static final double tempMinGradosC = -273.15;
	private double gradosC;
	
	public Temperatura() {}
	
	public double getGradosC() {
		return gradosC;
	}
	
	public void setGradosC(double gradosC) {
		if (gradosC >= tempMinGradosC) {
			this.gradosC = gradosC;
		} else {
			throw new IllegalArgumentException("La temperatura no puede ser menor a la minima posible");
		}		
	}
	
	public double getGradosF () {
		return ((this.getGradosC() * 9 / 5) + 32);
	}
	
	public void setGradosF(double gradosF) {
		this.setGradosC((gradosF - 32) * 5 / 9);
	}
	
	@Override
	public String toString() {
	    DecimalFormat df = new DecimalFormat("#.##");
	    return "Temperatura: " + df.format(getGradosC()) + " ºC <-> " + df.format(getGradosF()) + " ºF";
	}
}
