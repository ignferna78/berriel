package com.project.casaberriel.service.Impl;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.utils.Utils;

@Service
public class EmailServiceImpl implements IEmailService {

	private final JavaMailSender javaMailSender;
	
	@Autowired
   TemplateEngine templateEngine;

	public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	@Override
	public void sendMail(EmailDto email) throws MessagingException {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			System.out.println("Email del usuario: " + email.getEmail());

			helper.setTo("ifvillar9@gmail.com");
			helper.setSubject("Nuevo mensaje de contacto de: " + email.getName());
			helper.setReplyTo(email.getEmail());
			Context context = new Context();
			context.setVariable("messages", email.getMessage());
			context.setVariable("name", email.getName());
			context.setVariable("email", email.getEmail());
			String contentHtml = templateEngine.process("email", context);
			System.out.println("plantilla email: " + email);
			helper.setText(contentHtml, true);
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException("Error al enviar el correo electronico" + e.getMessage(), e);
		}

	}

	// Nuevo método para enviar confirmación de reserva
	public void sendReservationConfirmation(ReservaEntity reserva, boolean cancelada, boolean modificada)
			throws MessagingException {
		if (templateEngine == null) {
		    throw new IllegalArgumentException("El nombre de la plantilla no puede ser nulo o vacío.");
		}
		if (reserva.getEmail() == null || reserva.getEmail().isEmpty()) {
	        throw new IllegalArgumentException("El correo electrónico de la reserva es nulo o está vacío.");
	    }
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(reserva.getEmail());
			helper.addCc("nachin.ifv@gmail.com");
			helper.setSubject("Confirmación de reserva para " + reserva.getNombre());

			// Configuración del contexto para la plantilla de reserva
			Context context = new Context();
			context.setVariable("idReserva", reserva.getId());
			context.setVariable("nombre", reserva.getNombre());
			context.setVariable("apellidos", reserva.getApellidos());
			context.setVariable("observaciones", reserva.getObservaciones());
			context.setVariable("numPersonas", reserva.getNumPersonas());
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

	public void sendUsuarioConfirmation(Usuario usuario, boolean cancelada, boolean modificada)
			throws MessagingException {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(usuario.getEmail());
			helper.addCc("nachin.ifv@gmail.com");
			helper.setSubject("Confirmación de nueva cuenta de usuario para " + usuario.getNombre());

			Context context = new Context();
			context.setVariable("nombreCliente", usuario.getNombre());
			context.setVariable("apellidos", usuario.getApellidos());
			context.setVariable("direccion", usuario.getDireccion());
			context.setVariable("telefono", usuario.getTelefono());
			context.setVariable("email", usuario.getEmail());
			context.setVariable("modificada", modificada);
			context.setVariable("cancelada", cancelada);
			
			// Procesa la plantilla y envía el correo
						String contentHtml = templateEngine.process("UsuarioConfirmacion", context);
						helper.setText(contentHtml, true);
						javaMailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Error al enviar el correo de confirmación de reserva: " + e.getMessage(), e);
		}

	}
	

	    @Override
	    public void sendEmail(String to, String subject, String text) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject(subject);
	        message.setText(text);
	        javaMailSender.send(message);
	    }
	

}
