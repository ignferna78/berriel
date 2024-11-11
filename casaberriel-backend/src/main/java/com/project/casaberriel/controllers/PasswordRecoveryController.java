package com.project.casaberriel.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IEmailService emailService; // Un servicio para enviar correos electrónicos

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestParam("email") String email, Model model) {
        Usuario usuario = usuarioService.findUserByEmail(email);
        if (usuario == null) {
            model.addAttribute("error", "No se encontró ninguna cuenta con ese correo electrónico.");
            return "recuperar-password"; // Redirige a la misma página con el mensaje de error
        }

        // Generar un token de recuperación de contraseña
        String token = UUID.randomUUID().toString();
        usuarioService.savePasswordResetToken(usuario, token);

      
     // Construir el enlace de restablecimiento de contraseña
        String resetLink = "http://localhost:8333/restablecer-password?token=" + token;

        // Enviar el correo electrónico
        emailService.sendEmail(email, "Recuperación de Contraseña", 
            "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

        model.addAttribute("mensaje", "Se ha enviado un correo con instrucciones para restablecer tu contraseña.");
        return "index"; // Redirige a la página de inicio de sesión";
    }
    
   


    
    @GetMapping("/restablecer-password")
    public String mostrarFormularioRestablecerPassword(@RequestParam("token") String token, Model model) {
        Usuario usuario = usuarioService.findByPasswordResetToken(token);
        if (usuario == null) {
            model.addAttribute("error", "Token de restablecimiento de contraseña inválido o caducado.");
            return "index";
        }
        model.addAttribute("token", token);
        return "newPassword"; // Vista con el formulario para ingresar la nueva contraseña
    }

    @PostMapping("/restablecer-password")
    public String restablecerPassword(
            @RequestParam("token") String token,
            @RequestParam("nuevaPassword") String nuevaPassword,
            @RequestParam("confirmarPassword") String confirmarPassword,
            Model model) {
        
        if (!nuevaPassword.equals(confirmarPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "newPassword";
        }

        Usuario usuario = usuarioService.findByPasswordResetToken(token);
        if (usuario == null) {
            model.addAttribute("error", "Token de restablecimiento de contraseña inválido o caducado.");
            return "newPassword";
        }

        // Actualizar la contraseña del usuario
        usuarioService.updatePassword(usuario, nuevaPassword);
        model.addAttribute("mensaje", "Tu contraseña ha sido restablecida con éxito.");
        
        return "index"; // Redirigir a la página de inicio de sesión
    }


}
