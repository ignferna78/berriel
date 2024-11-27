package com.project.casaberriel.service.Impl;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Rol;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UserDetailsService, UsuarioService {

    @Autowired
    UsuarioRepositorio usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder; 


    // Constructor para inyección de dependencias
    public UsuarioServiceImpl(PasswordEncoder passwordEncoder, UsuarioRepositorio usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
      
    }
    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario guardar(UsuarioRegistroDto registroDto) {
    	
    	String telefono = registroDto.getTelefono();
        validarTelefono(telefono);
    	String password = registroDto.getPassword();
        validarPassword(password);
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDto.getNombre());
        usuario.setApellidos(registroDto.getApellidos());
        usuario.setDireccion(registroDto.getDireccion());
        usuario.setTelefono(registroDto.getTelefono());
        usuario.setEmail(registroDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDto.getPassword()));

        // Asignación de un rol por defecto 
        Rol userRole = new Rol("ROLE_USER"); 
        usuario.setRoles(Collections.singletonList(userRole));

        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + username);
        }
        return new User(usuario.getEmail(), usuario.getPassword(),
                mapearAutoridadesRoles(usuario.getRoles()));
    }

    public Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario findUserByEmail(String email) {
        return usuarioRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El email no está registrado"));
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

    @Override
    public Usuario findByPasswordResetToken(String token) throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findByPasswordResetToken(token);
        return usuario.filter(user -> user.getTokenExpirationTime() > System.currentTimeMillis())
        		.orElseThrow(() -> new UsernameNotFoundException("Token de restablecimiento de contraseña inválido o caducado."));
    }

    @Override
    public void updatePassword(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setPasswordResetToken(null); // Limpiar el token
        usuario.setTokenExpirationTime(null); // Limpiar el tiempo de expiración
        usuarioRepository.save(usuario);
    }
    @Override
    public void validarPassword(String password) {
        // Verificar que la contraseña no sea nula o vacía
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        // Validar longitud mínima
        if (password.length() < 5) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 5 caracteres.");
        }

        // Validar que no tenga caracteres especiales
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("La contraseña no debe contener caracteres especiales.");
        }
    }
    public void validarTelefono(String telefono) {
        // Verificar que el número no sea nulo o vacío
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío.");
        }

        // Verificar que tenga entre 9 y 12 caracteres
        if (telefono.length() < 9 || telefono.length() > 12) {
            throw new IllegalArgumentException("El número de teléfono debe tener entre 9 y 12 dígitos.");
        }

        // Verificar que solo contenga dígitos
        if (!telefono.matches("\\d+")) {
            throw new IllegalArgumentException("El número de teléfono solo puede contener dígitos.");
        }
    }


}
