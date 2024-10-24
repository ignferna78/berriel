package com.project.casaberriel.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;


@Service
public interface UsuarioService {

	
	//public Usuario guardar(UsuarioRegistroDto registroDto);

	List<Usuario> listarUsuarios();

	Usuario guardar(UsuarioRegistroDto registroDto);

	UserDetails loadUserByUsername(String username);
		
	
}
