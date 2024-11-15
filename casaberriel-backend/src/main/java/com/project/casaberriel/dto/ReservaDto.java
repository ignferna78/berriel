package com.project.casaberriel.dto;

import java.util.Date;

public class ReservaDto implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private String apellidos;
	private String email;
	private String direccion;
	private String observaciones;
	private Date fechaEntrada;
	private Date fechaSalida;
    private double precioPorDia;
    private Integer numPersonas;


	public ReservaDto() {
	}

	public ReservaDto(String nombre, String apellidos, String email, String direccion, String observaciones,
			Date fechaEntrada, Date fechaSalida, double precioPorDia,Integer numPersonas) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.direccion = direccion;
		this.observaciones = observaciones;
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.precioPorDia = precioPorDia;
		this.numPersonas = numPersonas;
	}
	

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public Date getFechaEntrada() {
		return fechaEntrada;
	}

	public Date getFechaSalida() {
		return fechaSalida;
	}

	

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public void setFechaSalida(Date fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public double getPrecioPorDia() {
		return precioPorDia;
	}

	public void setPrecioPorDia(double precioPorDia) {
		this.precioPorDia = precioPorDia;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public void setNumeroPersonas(Integer numPersonas) {
		this.numPersonas = numPersonas;
	}
}
