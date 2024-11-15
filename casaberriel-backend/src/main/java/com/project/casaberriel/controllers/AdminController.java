package com.project.casaberriel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.service.ReservaService;
import com.project.casaberriel.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ReservaService reservaService;
	
	@Autowired
    private UsuarioService usuarioService;
	
	
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 @GetMapping("/lista")
	 public String admin(Model model) {
		 
		 model.addAttribute("reservas", reservaService.listarReservas());
		 model.addAttribute("usuario", usuarioService.listarUsuarios());
	        return "admin"; // Devuelve la vista 'admin.html'
	    }
}
