package com.project.casaberriel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.service.UsuarioService;

@Controller
@RequestMapping("/registro")
public class RegitroUsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@ModelAttribute("usuario")
	public UsuarioRegistroDto getNuevoUsuarioRegistro() {
		return new UsuarioRegistroDto();
	}
	
	@PostMapping("/nuevo")
	public String guardarCuentaUser(@ModelAttribute("usuario")UsuarioRegistroDto registroDto, Model model)
	{
		model.addAttribute("usuario",usuarioService.guardar(registroDto));
		
		return "redirect:/registro/nuevo?exito"; 
	}
	
	@GetMapping("/nuevo")
	public String mostrarFormularioRegistro(Model modelo) {	
		return "registro";
	}


	

	
	@GetMapping("/inicio")
	public String verPaginaDeInicio(Model modelo) {
		modelo.addAttribute("usuarios", usuarioService.listarUsuarios());
		return "index";
	}
	
}
