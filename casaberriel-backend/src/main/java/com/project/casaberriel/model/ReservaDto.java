package com.project.casaberriel.model;

public class ReservaDto implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cliente;
	private String fechaEntrada;
	private String fechaSalida;

	public ReservaDto() {
	}

	public ReservaDto(String cliente, String fechaEntrada, String fechaSalida) {
		this.cliente = cliente;
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
	}

	public String getCliente() {
		return cliente;
	}

	public String getFechaEntrada() {
		return fechaEntrada;
	}

	public String getFechaSalida() {
		return fechaSalida;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public void setFechaEntrada(String fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public void setFechaSalida(String fechaSalida) {
		this.fechaSalida = fechaSalida;
	}
}
