package com.project.casaberriel.service.Impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
import com.project.casaberriel.utils.Utils;

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
	public void sendReservationConfirmation(ReservaEntity reserva,boolean cancelada,boolean modificada) throws MessagingException {
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setTo(reserva.getEmail());
	        helper.addCc("nachin.ifv@gmail.com");
	        helper.setSubject("Confirmación de reserva para " + reserva.getNombre());

	        // Configuración del contexto para la plantilla de reserva
	        Context context = new Context();
	        context.setVariable("nombreCliente", reserva.getNombre());
	        context.setVariable("modificada", modificada);
	        context.setVariable("cancelada", cancelada);

	        // Convertir Date a LocalDate
	        LocalDate fechaEntrada = reserva.getFechaEntrada().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        LocalDate fechaSalida = reserva.getFechaSalida().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	        // Formatear las fechas
	        String fechaEntradaFormateada = Utils.formatDate(fechaEntrada);
	        String fechaSalidaFormateada = Utils.formatDate(fechaSalida);

	        context.setVariable("fechaEntrada", fechaEntradaFormateada);
	        context.setVariable("fechaSalida", fechaSalidaFormateada);

	        // Calcular el precio total
	        double precioTotal = Utils.calculateTotalPrice(fechaEntrada, fechaSalida, reserva.getPrecioPorDia());
	        context.setVariable("precioTotal", precioTotal + "€");
	        context.setVariable("precioPordia", reserva.getPrecioPorDia() + "€");
	        reserva.setPrecioTotal(precioTotal);

	        // Procesa la plantilla y envía el correo
	        String contentHtml = templateEngine.process("reservaConfirmacion", context);
	        helper.setText(contentHtml, true);
	        javaMailSender.send(message);
	    } catch (Exception e) {
	        throw new RuntimeException("Error al enviar el correo de confirmación de reserva: " + e.getMessage(), e);
	    }
	}


}
