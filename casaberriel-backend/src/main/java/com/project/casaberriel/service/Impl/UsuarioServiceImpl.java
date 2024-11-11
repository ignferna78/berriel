package com.project.casaberriel.service.Impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Rol;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UserDetailsService,UsuarioService {

	@Autowired
	private UsuarioRepositorio usuarioRepository;
	
	@Autowired
	private IEmailService emailService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Bean
	private UsuarioService usuarioService() {
		return new UsuarioService() {


			@Override
			public List<Usuario> listarUsuarios() {
				return usuarioRepository.findAll();
			}

			@Override
			public Usuario guardar(UsuarioRegistroDto registroDto) {
				Usuario usuario = new Usuario();
				usuario.setNombre(registroDto.getNombre());
				usuario.setApellidos(registroDto.getApellidos());
				usuario.setEmail(registroDto.getEmail());
				usuario.setPassword(registroDto.getPassword());
				usuario.setPassword(passwordEncoder.encode(registroDto.getPassword()));
				// Asignación de un rol por defecto (por ejemplo, "USER")
				Rol userRole = new Rol("ROLE_USER"); // Asegúrate de que el rol "USER" existe en la BD
				usuario.setRoles(Collections.singletonList(userRole));

				// Guardado en el repositorio
				return usuarioRepository.save(usuario);
			}

			@Override
			public UserDetails loadUserByUsername(String username) {
				
				return null;
			}

			@Override
			public Usuario findUserByEmail(String email) {
				return usuarioRepository.findUserByEmail(email)
						.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + email));
			}

			@Override
			public Usuario findUserById(Long id) {
				return usuarioRepository.findById(id).orElse(null);
			}

			@Override
			public void updateUser(Usuario usuario) {
				   usuarioRepository.save(usuario);
				
			}

			@Override
			public void deleteUserById(Long id) {
				usuarioRepository.deleteById(id);				
			}

			@Override
			public void savePasswordResetToken(Usuario usuario, String token) {
				  usuario.setPasswordResetToken(token);
			        usuario.setTokenExpirationTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // Expira en 24 horas
			        usuarioRepository.save(usuario);
				
			}
			   public Usuario findByPasswordResetToken(String token) {
				   Optional<Usuario> usuario = usuarioRepository.findByPasswordResetToken(token);
				    if (usuario.isPresent()) {
				        Usuario user = usuario.get();
				        System.out.println("Token encontrado: " + token);
				        System.out.println("Tiempo de expiración del token: " + user.getTokenExpirationTime());
				        if (user.getTokenExpirationTime() > System.currentTimeMillis()) {
				            return user;
				        } else {
				            System.out.println("Token expirado.");
				        }
				    } else {
				        System.out.println("Token no encontrado.");
				    }
			        return null; // Token inválido o expirado
			    }

			@Override
			public void updatePassword(Usuario usuario, String nuevaPassword) {
		        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
		        usuario.setPasswordResetToken(null); // Limpiar el token
		        usuario.setTokenExpirationTime(null); // Limpiar el tiempo de expiración
		        usuarioRepository.save(usuario);
				
			}
		
		};
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Usuario usuario = usuarioRepository.findByEmail(username);
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario no encontrado con email: " + username);
		} else {
			System.out.println("Usuario encontrado: " + usuario.getEmail());
			return new User(usuario.getEmail(), usuario.getPassword(),
					mapearAutoridadesRoles(usuario.getRoles()));
		}
	}
	
	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
	}

	@Override
	public List<Usuario> listarUsuarios() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
    public Usuario findUserById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void updateUser(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

	@Override
	public Usuario guardar(UsuarioRegistroDto registroDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario findUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserById(Long id) {
		usuarioRepository.deleteById(id);	
	}


    // Guardar el token de recuperación de contraseña
	    @Override
    public void savePasswordResetToken(Usuario usuario, String token) {
        usuario.setPasswordResetToken(token);
        usuario.setTokenExpirationTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // Expira en 24 horas
        usuarioRepository.save(usuario);
    }

    // Buscar un usuario por el token de recuperación de contraseña
    @Override
    public Usuario findByPasswordResetToken(String token) {
        Optional<Usuario> usuario = usuarioRepository.findByPasswordResetToken(token);
        if (usuario.isPresent() && usuario.get().getTokenExpirationTime() > System.currentTimeMillis()) {
            return usuario.get();
        }
        return null; // Token inválido o expirado
    }

    // Actualizar la contraseña de un usuario
    @Override
    public void updatePassword(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setPasswordResetToken(null); // Limpiar el token
        usuario.setTokenExpirationTime(null); // Limpiar el tiempo de expiración
        usuarioRepository.save(usuario);
    }
	

}
