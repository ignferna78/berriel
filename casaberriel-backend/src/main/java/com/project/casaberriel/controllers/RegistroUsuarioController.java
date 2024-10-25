package com.project.casaberriel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.UsuarioService;

@Controller
@RequestMapping("/registro")
public class RegistroUsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Bean
    private AuthenticationManager authenticationManager() {
    	return new AuthenticationManager() {
			
			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return null;
			}
		};
    };

    // Este método asegura que siempre haya un objeto "usuario" disponible para el formulario
    @ModelAttribute("usuario")
    public UsuarioRegistroDto getNuevoUsuarioRegistro() {
        return new UsuarioRegistroDto();
    }

    // Guardar cuenta de usuario
    @PostMapping("/nuevo")
    public String guardarCuentaUser(@ModelAttribute("usuario") UsuarioRegistroDto registroDto, Model model) {
    	try {
    		Usuario usuario = usuarioService.guardar(registroDto);
    		 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     usuario.getEmail(), registroDto.getPassword());
             Authentication authentication = authenticationManager().authenticate(authToken);
             SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/registro/inicio?exito";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "El email ya está registrado. Por favor, usa otro email.");
            return "registro";
        }
    }

    // Mostrar página de registro
    @GetMapping("/inicio")
    public String verPaginaDeInicio(Model modelo) {
        modelo.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "registro";
    }
}
