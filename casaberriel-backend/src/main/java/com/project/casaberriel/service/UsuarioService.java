package com.project.casaberriel.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;


@Service
public interface UsuarioService {


	List<Usuario> listarUsuarios();

	Usuario guardar(UsuarioRegistroDto registroDto);

	UserDetails loadUserByUsername(String username);

	Usuario findUserByEmail(String email); 
		
	Usuario findUserById(Long id);
    void updateUser(Usuario usuario);

	void deleteUserById(Long id);

	void savePasswordResetToken(Usuario usuario, String token);

	Usuario findByPasswordResetToken(String token);

	void updatePassword(Usuario usuario, String nuevaPassword);
}
