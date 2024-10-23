package com.project.casaberriel.model.reservas;

public class ReservaForm {

	private String nombre;
	private String apellidos;
	private String email;
	private String direccion;
	private String fechaEntrada;
	private String fechaSalida;
	

	public ReservaForm() {
	}

	public ReservaForm(String nombre, String apellidos, String email, String direccion, String fechaEntrada, String fechaSalida) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.direccion = direccion;
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getApellidos() {
		return apellidos;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
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
