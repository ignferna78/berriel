package com.project.casaberriel.service.Impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Rol;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
	
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public Usuario guardar(UsuarioRegistroDto registroDto) {
		Usuario usuario = null;
		try {
			usuario = new Usuario(registroDto.getNombre(), registroDto.getApellidos(),passwordEncoder.encode(registroDto.getPassword()) ,
					registroDto.getEmail(), Arrays.asList(new Rol("ROLE_USER")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usuarioRepositorio.save(usuario);
	}
	
	@Override
	public List<Usuario> listarUsuarios() {
		return usuarioRepositorio.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		} else {
			return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRoles()));
		}
	}
	
	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
	}
	

	

}
