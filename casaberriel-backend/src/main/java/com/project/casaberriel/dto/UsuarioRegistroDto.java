package com.project.casaberriel.dto;

public class UsuarioRegistroDto {

	private Long id;
	private String nombre;
	private String apellidos;
	private String email;
	private String password;

	public UsuarioRegistroDto(String email) {
		super();
		this.email = email;
	}

	public UsuarioRegistroDto(String nombre, String apellidos, String email, String password) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
	}

	public UsuarioRegistroDto(Long id, String nombre, String apellidos, String email, String password) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
	}

	public UsuarioRegistroDto() {
		super();

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
