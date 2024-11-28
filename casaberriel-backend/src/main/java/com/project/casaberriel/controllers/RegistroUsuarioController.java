package com.project.casaberriel.controllers;

import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;
import com.project.casaberriel.utils.LoginRequest;

@Controller
@RequestMapping("/registro")
public class RegistroUsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private IEmailService emailService;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private static final String REGISTRO = "registro";
	private static final String DETALLE_USUARIO = "detalle_usuario";
	private static final String REDIRECT_PANEL_ADMIN = "redirect:/admin/lista";
	private static final String MESSAGE = "message";
	private static final String ERROR = "error";
	private static final String HOME = "redirect:/home/index";

	@Bean
	private AuthenticationManager authenticationManager() {
		return new AuthenticationManager() {

			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return null;
			}
		};
	};

	// Este método asegura que siempre haya un objeto "usuario" disponible para el
	// formulario
	@ModelAttribute("usuario")
	public UsuarioRegistroDto getNuevoUsuarioRegistro() {
		return new UsuarioRegistroDto();
	}

	@PostMapping("/validarCredenciales")
	public ResponseEntity<?> validarCredenciales(@RequestBody LoginRequest loginRequest) {
		try {
			Usuario usuario = usuarioService.findUserByEmail(loginRequest.getEmail());

			// Verifica la contraseña usando el mecanismo de encriptación configurado
			if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
				return ResponseEntity.ok(Map.of("message", "Credenciales válidas"));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(MESSAGE, "Contraseña incorrecta"));
			}
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, "El email no existe"));
		}
	}

	// Guardar cuenta de usuario
	@PostMapping("/nuevo")
	public String guardarCuentaUser(@ModelAttribute("usuario") UsuarioRegistroDto registroDto, Model model,
			boolean cancelada, boolean modificada) throws MessagingException {
		try {
			Boolean exito = null;
			Usuario usuario = usuarioService.guardar(registroDto);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuario.getEmail(),
					registroDto.getPassword());
			Authentication authentication = authenticationManager().authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			model.addAttribute("exito", exito);
			model.addAttribute("cancelada", cancelada);
			model.addAttribute("modificada", modificada);
			emailService.sendUsuarioConfirmation(usuario, cancelada, modificada);
			return "redirect:/registro/inicio?exito";
		} catch (DataIntegrityViolationException e) {
			model.addAttribute(ERROR, "El email ya está registrado. Por favor, usa otro email.");
			return REGISTRO;
		} catch (IllegalArgumentException ex) {
			model.addAttribute(ERROR, ex.getMessage());
			return REGISTRO;
		}
	}

	// Mostrar página de registro
	@GetMapping("/inicio")
	public String verPaginaDeInicio(Model modelo) {
		modelo.addAttribute("usuarios", usuarioService.listarUsuarios());
		return REGISTRO;
	}

	// Mostrar página de registro
	@GetMapping("/detalle-usuario")
	public String verDatosUsuario(Model model) {
		// Obtener el usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName(); // Obtener el nombre de usuario (email)
		Usuario usuario = null;
		try {
			usuario = usuarioService.findUserByEmail(email);
		} catch (Exception e) {
		}
		model.addAttribute("usuario", usuario);
		return DETALLE_USUARIO;
	}

// Mostrar formulario de edición de usuario
	@GetMapping("/editar-usuario/{id}")
	public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
		Usuario usuario = usuarioService.findUserById(id);
		model.addAttribute("usuario", usuario);
		return DETALLE_USUARIO;
	}

	@PostMapping("/editar-usuario/{id}")
	public String editarUsuario(@PathVariable("id") Long id,
			@ModelAttribute("usuario") UsuarioRegistroDto usuarioActualizado, Model model, boolean modificada) {
		try {
			Usuario usuarioExistente = usuarioService.findUserById(id);
			if (usuarioExistente == null) {
				model.addAttribute(ERROR, "El usuario no existe.");
				return DETALLE_USUARIO;
			}
			// Actualizar campos del usuario
			actualizarDatosUsuario(usuarioExistente, usuarioActualizado);

			// Guardar cambios
			usuarioService.updateUser(usuarioExistente);

			// Enviar correo de confirmación
			emailService.sendUsuarioConfirmation(usuarioExistente, false, modificada);

			model.addAttribute(MESSAGE, "Usuario modificado con éxito.");
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error al actualizar el usuario.");
		}
		return DETALLE_USUARIO;
	}

	// Método auxiliar para actualizar los datos del usuario
	private void actualizarDatosUsuario(Usuario usuarioExistente, UsuarioRegistroDto usuarioActualizado) {
		usuarioExistente.setNombre(usuarioActualizado.getNombre());
		usuarioExistente.setApellidos(usuarioActualizado.getApellidos());
		usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
		usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
		usuarioExistente.setEmail(usuarioActualizado.getEmail());
		// Actualiza otros campos según sea necesario
	}

	@GetMapping("/eliminar-usuario/{id}")
	public String eliminarUsuario(@PathVariable("id") Long id, Model model, HttpServletRequest request,
			HttpServletResponse response, boolean cancelada) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			Usuario user = usuarioService.findUserById(id);
			usuarioService.deleteUserById(id);
			emailService.sendUsuarioConfirmation(user, true, false);
			model.addAttribute("cancelada", cancelada);
			if (isAdmin) {
				model.addAttribute(MESSAGE, "Usuario eliminado con éxito.");
				return REDIRECT_PANEL_ADMIN;
			}

			// Inalidate the session to log out the user
			request.getSession().invalidate();
			// Clear the security context
			SecurityContextHolder.clearContext();
			return HOME;
		} catch (DataIntegrityViolationException e) {
			model.addAttribute(ERROR, "No se puede eliminar el usuario debido a una violación de integridad de datos.");
			return DETALLE_USUARIO;
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error al eliminar el usuario.");
			return DETALLE_USUARIO;
		}
	}

}
