package com.project.casaberriel.model;

public class ReservaForm {

	private String nombre;
	private String apellidos;
	private String fechaEntrada;
	private String fechaSalida;
	private String email;

	public ReservaForm() {
	}

	public ReservaForm(String nombre, String apellidos, String fechaEntrada, String fechaSalida, String email) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.email = email;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getApellidos() {
		return apellidos;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getFechaEntrada() {
		return fechaEntrada;
	}

	public String getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaEntrada(String fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public void setFechaSalida(String fechaSalida) {
		this.fechaSalida = fechaSalida;
	}
}
