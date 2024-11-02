
package com.project.casaberriel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.service.IEmailService;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8333")
public class EmailController {

    @Autowired
    private IEmailService emailService;

    @PostMapping(value= "/send-email", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> sendMessage(@RequestBody EmailDto email) throws MessagingException {
        emailService.sendMail(email);
        return new ResponseEntity<>("{\"message\":\"Correo enviado exitosamente\"}", HttpStatus.OK);
    }
}

