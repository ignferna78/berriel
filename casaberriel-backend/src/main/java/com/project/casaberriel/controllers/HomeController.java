package com.project.casaberriel.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.ReservaService;
import com.project.casaberriel.service.UsuarioService;

@Controller
@RequestMapping("/home")
public class HomeController {

	public HomeController(ReservaService reservaService) {
		super();
		this.reservaService = reservaService;
	}

	@Autowired
	private ReservaService reservaService;
	
	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/index")
	public String index(Model model, Usuario user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Obtiene el nombre del usuario autenticado
		model.addAttribute("username", username);
		model.addAttribute("mail", user.getNombre());
		return "index";
	}

	@GetMapping("/comprobar-disponibilidad")
	public String comprobarDisponibilidad(@RequestParam(required = false) String fechaEntrada,
			@RequestParam(required = false) String fechaSalida, Model model, HttpServletRequest request) {

		// Define los formateadores de fecha para entrada y salida
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
		LocalDate entrada = null;
		LocalDate salida = null;

		try {
			// Intenta convertir las fechas recibidas
			if (fechaEntrada != null && !fechaEntrada.isEmpty()) {
				entrada = LocalDate.parse(fechaEntrada, formatter);
			}
			if (fechaSalida != null && !fechaSalida.isEmpty()) {
				salida = LocalDate.parse(fechaSalida, formatter);
			}
		} catch (DateTimeParseException e) {
			// Si hay un error en el formato, vuelve a la página con un mensaje de error
			model.addAttribute("error", "Formato de fecha no válido");
			return "index";
		}

		// Verifica disponibilidad solo si ambas fechas son válidas
		if (entrada != null && salida != null) {
			ReservaForm reservaForm = new ReservaForm();
			reservaForm.setFechaEntrada(entrada.format(formatter));
			reservaForm.setFechaSalida(salida.format(formatter));

			boolean disponible = reservaService.comprobarDisponibilidad(reservaForm);
			model.addAttribute("disponible", disponible);
		} else {
			model.addAttribute("error", "Las fechas de entrada y salida son requeridas.");
		}

		boolean isUserLoggedIn = request.getRemoteUser() != null;
		model.addAttribute("isUserLoggedIn", isUserLoggedIn);
		// Agrega las fechas formateadas al modelo para mostrarlas en el formulario
		model.addAttribute("fechaEntradaFormateada", entrada != null ? entrada.format(formatter) : "");
		model.addAttribute("fechaSalidaFormateada", salida != null ? salida.format(formatter) : "");

		return "index"; // Retorna a la página principal o el formulario de reservas
	}

}
