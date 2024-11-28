package com.project.casaberriel.model.usuarios;

import java.util.Collection;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.project.casaberriel.model.reservas.ReservaEntity;

@Entity
@Table(name = "usuarios",uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="id")
	private Long id;
	
	@Column(name ="nombre")
	private String nombre;
	
	@Column(name ="apellidos")
	private String apellidos;
	
	@Column(name ="direccion")
	private String direccion;
	
	
	@Column(name ="telefono")
	private String telefono;

	@Column(name ="password")
	private String password;
	
	@Column(name ="email")
	private String email;
	
	   // Campos para la recuperación de contraseña
    private String passwordResetToken;
    private Long tokenExpirationTime;
	
	
	
	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public Long getTokenExpirationTime() {
		return tokenExpirationTime;
	}

	public void setTokenExpirationTime(Long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
	}

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(name = "usuarios_roles",
	joinColumns = @JoinColumn(name = "usuarios_id",referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "rol_id",referencedColumnName = "id"))
	private Collection<Rol>roles;
	
	// Relación One-to-Many con ReservaEntity
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaEntity> reservas;
	
	public Usuario() {
		super();
	}
	
	public Usuario(String nombre, String apellidos, String direccion,String telefono, String password, String email, Collection<Rol> roles) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.direccion = direccion;
		this.telefono=telefono;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}
	
	public Usuario(Long id, String nombre, String apellidos, String direccion,String telefono, String password,String email,  Collection<Rol> roles) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.direccion = direccion;
		this.telefono=telefono;
		this.email = email;
		this.password = password;
		this.roles = roles;
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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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

	public Collection<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Rol> roles) {
		this.roles = roles;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public List<ReservaEntity> getReservas() {
		return reservas;
	}

	public void setReservas(List<ReservaEntity> reservas) {
		this.reservas = reservas;
	}


}
