package com.project.casaberriel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.model.Reserva;
import com.project.casaberriel.service.ReservaService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

	public ReservaController(ReservaService reservaService) {
		super();
		this.reservaService = reservaService;
	}

	private ReservaService reservaService;

	@GetMapping
	public String index() {
		return "index";
	}
	
	@GetMapping("/lista")
	public String listarReservas(Model model) {

		model.addAttribute("reservas", reservaService.listarReservas());

		return "reservas";
	}

	@GetMapping("/nueva")
	public String mostrarFormularioReserva(Model model) {
		model.addAttribute("reserva", new Reserva());
		return "reservas";
	}

	@GetMapping("/detalle/{id}")
	public String detalleReserva(@PathVariable Long id, Model model) {
		Reserva reserva = reservaService.obtenerReservaPorId(id);
		if (reserva != null) {
			model.addAttribute("reserva", reserva);
		} else {
			// manejar el caso cuando la reserva no se encuentra
		}
		return "detalle_reserva";
	}

	@PostMapping("/guardar")
	public String guardarReserva(Reserva reserva) {
		reservaService.guardarReserva(reserva);
		return "detalle_reserva"; // Nombre de la vista Thymeleaf";
	}

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditarReserva(Model model, @PathVariable Long id) {
		model.addAttribute("reserva", reservaService.obtenerReservaPorId(id));
		return "redirect:/reservas/lista";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarReserva(@PathVariable Long id) {
		reservaService.eliminarReserva(id);
		return "redirect:/reservas/lista";
	}

}
