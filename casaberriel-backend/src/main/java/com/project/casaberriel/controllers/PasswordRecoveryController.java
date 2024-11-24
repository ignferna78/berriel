package com.project.casaberriel.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;

@Controller
public class PasswordRecoveryController {

	@Value("${app.url.local}")
	private String localUrl;

	@Value("${app.url.heroku}")
	private String productionUrl;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private IEmailService emailService; // Un servicio para enviar correos electrónicos

	@Autowired
	private UsuarioRepositorio usuarioRepository;

	@PostMapping("/recuperar-password")
	public String recuperarPassword(@RequestParam("email") String email, Model model,
			RedirectAttributes redirectAttributes) {
		Usuario usuario = null;
		try {
			usuario = usuarioService.findUserByEmail(email);
		} catch (UsernameNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMail", e.getMessage());
			return "redirect:/reservas/recuperarPassword"; // Redirige a la misma página con el mensaje de error
		}

		// Generar un token de recuperación de contraseña
		String token = UUID.randomUUID().toString();
		usuarioService.savePasswordResetToken(usuario, token);

		// Construir el enlace de restablecimiento de contraseña
		String baseUrl = activeProfile.equals("heroku") ? productionUrl : localUrl;
		String resetLink = baseUrl + "/restablecer-password?token=" + token;

		// Enviar el correo electrónico
		emailService.sendEmail(email, "Recuperación de Contraseña",
				"Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

		model.addAttribute("mensaje", "Se ha enviado un correo con instrucciones para restablecer tu contraseña.");
		return "index"; // Redirige a la página de inicio de sesión";
	}

	@GetMapping("/restablecer-password")
	public String mostrarFormularioRestablecerPassword(@RequestParam("token") String token, Model model)
			throws Exception {
		Usuario usuario = usuarioService.findByPasswordResetToken(token);
		if (usuario == null) {
			model.addAttribute("error", "Token de restablecimiento de contraseña inválido o caducado.");
			return "index";
		}
		model.addAttribute("token", token);
		return "newPassword"; // Vista con el formulario para ingresar la nueva contraseña
	}

	@PostMapping("/restablecer-password")
	public String restablecerPassword(@RequestParam("token") String token,
			@RequestParam("nuevaPassword") String nuevaPassword,
			@RequestParam("confirmarPassword") String confirmarPassword, Model model,
			RedirectAttributes redirectAttributes) throws Exception {
		try {
			// Verificar si las contraseñas coinciden
			if (!nuevaPassword.equals(confirmarPassword)) {
				model.addAttribute("token", token);
				model.addAttribute("errorPass", "Las contraseñas no coinciden.");
				return "newPassword"; // Retornar al formulario
			}
			// Buscar el usuario por el token
			Usuario usuario = usuarioService.findByPasswordResetToken(token);

			// Validar la contraseña
			usuarioService.validarPassword(nuevaPassword);

			model.addAttribute("mensaje", "Tu contraseña ha sido restablecida con éxito.");
			return "index"; // Redirigir a la página de inicio de sesión

		} catch (UsernameNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorToken", e.getMessage());
			return "redirect:/reservas/recuperarPassword"; // Redirigir a la página de recuperación
		} catch (IllegalArgumentException ex) {
			model.addAttribute("token", token);
			model.addAttribute("errorPass", ex.getMessage());
			return "newPassword"; // Retornar al formulario
		}
	}

}
