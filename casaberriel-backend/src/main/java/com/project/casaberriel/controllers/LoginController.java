package com.project.casaberriel.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.casaberriel.service.UsuarioService;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/inicio")
    public String verPaginaDeInicio(Model modelo) {
        modelo.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "index";
    }

    @PostMapping("/formLogin")
    public String login(@RequestParam(value = "error", required = false) Boolean error, Model model) {
        if (error) {
            model.addAttribute("error", "Usuario o contraseña inválidos");
        }
        return "index";
    }

       
    

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "logout", required = false) String logout,Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamenteerr.");
        }
        return "redirect:/reservas/index?logout=true";
    }
  
}

