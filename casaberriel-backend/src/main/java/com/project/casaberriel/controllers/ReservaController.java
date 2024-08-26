package com.project.casaberriel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/lista")
	public String listarReservas(Model model) {

		model.addAttribute("reservas", reservaService.listarReservas());

		return "reservas"; // Nombre de la vista Thymeleaf";
	}

	@GetMapping("/nueva")
	public String mostrarFormularioReserva(Model model) {
		model.addAttribute("reserva", new Reserva());
		return "reservas"; // Nombre de la vista Thymeleaf
	}

	@PostMapping("/guardar")
	public String guardarReserva(Reserva reserva) {
		reservaService.guardarReserva(reserva);
		return "redirect:/reservas";
	}
}
