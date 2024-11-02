package com.project.casaberriel.service.Impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.dto.ReservaDto;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.service.IEmailService;

@Service
public class EmailServiceImpl implements IEmailService {

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	@Override
	public void sendMail(EmailDto email) throws MessagingException {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(email.getEmail());
			helper.setSubject("Nuevo mensaje de contacto de: " + email.getName());
			Context context = new Context();
			context.setVariable("message", email.getMessage());
            context.setVariable("name", email.getName());
            context.setVariable("email", email.getEmail());
			String contentHtml = templateEngine.process("email", context);

			helper.setText(contentHtml, true);
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException("Error al enviar el correo electronico" + e.getMessage(), e);
		}

	}
	
	// Nuevo método para enviar confirmación de reserva
	public void sendReservationConfirmation(ReservaDto reservaDto) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reservaDto.getEmail());
            helper.setSubject("Confirmación de reserva para " + reservaDto.getNombre());

            // Configuración del contexto para la plantilla de reserva
            Context context = new Context();
            context.setVariable("nombreCliente", reservaDto.getNombre());
            context.setVariable("fechaEntrada", reservaDto.getFechaEntrada());
            context.setVariable("fechaSalida", reservaDto.getFechaSalida());
            //context.setVariable("precioTotal", reservaDto.getPrecioTotal());

            // Procesa la plantilla y envía el correo
            String contentHtml = templateEngine.process("reserva_confirmacion", context);
            helper.setText(contentHtml, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo de confirmación de reserva: " + e.getMessage(), e);
        }
    }

	@Override
	public void sendReservationConfirmation(ReservaEntity savedReserva) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

}
