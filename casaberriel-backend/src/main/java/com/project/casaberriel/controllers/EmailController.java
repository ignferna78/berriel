
package com.project.casaberriel.controllers;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8333")
public class EmailController {

	@Autowired
	private IEmailService emailService;

	@PostMapping(value = "/send-email", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> sendMessage(@RequestBody EmailDto email) throws MessagingException {
		emailService.sendMail(email);
		return new ResponseEntity<>("{\"messages\":\"Correo enviado exitosamente\"}", HttpStatus.OK);
	}

}
