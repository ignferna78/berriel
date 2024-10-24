package com.project.casaberriel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.service.ReservaService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ReservaService reservaService;
	
	
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 @GetMapping("/lista")
	 public String admin(Model model) {
		 
		 model.addAttribute("reservas", reservaService.listarReservas());
	        return "admin"; // Devuelve la vista 'admin.html'
	    }
}
