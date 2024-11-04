package com.project.casaberriel.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.ReservaService;

@Controller
@RequestMapping("/reservas")
@SessionAttributes("reserva")
public class HomeController {

	public HomeController(ReservaService reservaService) {
		super();
		this.reservaService = reservaService;
	}

	private ReservaService reservaService;

	private IEmailService emailService;

	private static final String REDIRECT_LISTA_RESERVAS = "redirect:/reservas/lista";

	@GetMapping("/index")
	public String index(Model model, Usuario user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Obtiene el nombre del usuario autenticado
		model.addAttribute("username", username);
		model.addAttribute("mail", user.getNombre());
		return "index";
	}

	@GetMapping("/formReserva")
	public String mostrarFormularioReserva(@RequestParam(required = false) String fechaEntrada,
			@RequestParam(required = false) String fechaSalida, Model model) {
		model.addAttribute("fechaEntrada", fechaEntrada);
		model.addAttribute("fechaSalida", fechaSalida);
		return "reservas";
	}

	@PostMapping("/guardar")
	public String guardarReserva(ReservaEntity reserva, ReservaForm fecha, Principal principal, Model model,
			boolean cancelada, boolean modificada) {
		// Obtén el nombre del usuario autenticado
		String username = principal.getName();
		try {

			ReservaEntity savedReserva = reservaService.guardarReserva(reserva, fecha, username, cancelada, modificada);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("message", "Reserva guardada con éxito.");
		return "reservas";
	}

	@GetMapping("/detalle/{id}")
	public String detalleReserva(@PathVariable Long id, Model model) {
		ReservaEntity reserva = reservaService.obtenerReservaPorId(id);
		if (reserva != null) {
			model.addAttribute("reserva", reserva);
		} else {
			// Puedes redirigir a una página de error o mostrar un mensaje de "reserva no
			// encontrada"
			return "redirect:/error";
		}
		return "detalle_reserva";
	}

	@GetMapping("/miReserva")
	public String miReserva(Model model) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		// Obtener la reserva por email
		List<ReservaEntity> reserva = reservaService.obtenerReservaPorEmail(email);

		// Pasar la reserva al modelo
		model.addAttribute("reserva", reserva);

		// Calcular el precio total de todas las reservas
		double precioTotal = reserva.stream().mapToDouble(ReservaEntity::getPrecioTotal).sum();
		model.addAttribute("precioTotal", precioTotal);
		return "miReserva";
	}

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditarReserva(Model model, @PathVariable Long id) {
		ReservaEntity reserva = reservaService.obtenerReservaPorId(id);
		if (reserva != null) {
			model.addAttribute("reserva", reserva);
			return "editar_reserva"; // Nombre de la vista para editar la reserva
		} else {
			return "redirect:/error"; // Redirigir a una página de error si la reserva no se encuentra
		}
	}

	@PostMapping("/editar/{id}")
	public String guardarReservaEditada(@PathVariable Long id,
			@ModelAttribute("reserva") ReservaEntity reservaActualizada, Model model, ReservaForm fecha, String email,
			boolean cancelada, boolean modificada) throws MessagingException {
		ReservaEntity reservaExistente = reservaService.obtenerReservaPorId(id);
		if (reservaExistente != null) {
			reservaExistente.setNombre(reservaActualizada.getNombre());
			reservaExistente.setApellidos(reservaActualizada.getApellidos());
			reservaExistente.setEmail(reservaActualizada.getEmail());
			reservaExistente.setDireccion(reservaActualizada.getDireccion());
			reservaExistente.setFechaEntrada(reservaActualizada.getFechaEntrada());
			reservaExistente.setFechaSalida(reservaActualizada.getFechaSalida());
			reservaExistente.setPrecioTotal(reservaActualizada.getPrecioTotal());
			reservaService.guardarReserva(reservaExistente, fecha, email, cancelada, modificada);
			model.addAttribute("message", "Reserva actualizada con éxito.");
			model.addAttribute("modificada", modificada);
			model.addAttribute("cancelada", cancelada);
			return "editar_reserva"; // Redirigir a la vista de administración
		} else {
			return "redirect:/error"; // Redirigir a una página de error si la reserva no se encuentra
		}
	}


	@GetMapping("/delete/{id}")
	public String deleteReserva(@PathVariable Long id, Model model,String email, boolean cancelada, boolean modificada)
			throws MessagingException {
		reservaService.eliminarReserva(id,email, cancelada, modificada);
		
		model.addAttribute("message", "Reserva eliminada con éxito.");
		return "redirect:/reservas/miReserva";
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

	@GetMapping("/lista")
	public String mostrarFormularioReserva(@RequestParam String fechaEntrada, @RequestParam String fechaSalida,
			Model model, HttpSession session) {

		if (session.getAttribute("usuario") == null) {
			// Redirigir al modal de login
			model.addAttribute("redirectUrl",
					"/reservas/lista?fechaEntrada=" + fechaEntrada + "&fechaSalida=" + fechaSalida);
			return "registro"; // Nombre de la vista del modal de login
		}
		ReservaForm reservaForm = new ReservaForm();
		reservaForm.setFechaEntrada(fechaEntrada);
		reservaForm.setFechaSalida(fechaSalida);
		model.addAttribute("reservaForm", reservaForm);

		return "reservas";
	}

}
