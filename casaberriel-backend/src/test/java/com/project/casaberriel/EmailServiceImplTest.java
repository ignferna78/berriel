package com.project.casaberriel;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.service.Impl.EmailServiceImpl;

public class EmailServiceImplTest {

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private TemplateEngine templateEngine;

	@InjectMocks
	private EmailServiceImpl emailService;

	@Mock
	private MimeMessage mimeMessage;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void sendReservationConfirmation_shouldSendEmailSuccessfully() throws MessagingException {
		// Arrange
		ReservaEntity reserva = new ReservaEntity();
		reserva.setId(1L);
		reserva.setEmail("cliente@example.com");
		reserva.setNombre("Cliente");
		reserva.setApellidos("Ejemplo");
		reserva.setObservaciones("Sin observaciones");
		reserva.setNumPersonas(2);
		reserva.setPrecioPorDia(100.0);
		reserva.setFechaEntrada(Date.from(LocalDate.of(2024, 12, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		reserva.setFechaSalida(Date.from(LocalDate.of(2024, 12, 5).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		MimeMessage mockMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

	}

	@Test
	void sendReservationConfirmation_shouldThrowExceptionWhenEmailIsNull() {
		// Arrange
		ReservaEntity reserva = new ReservaEntity();
		reserva.setEmail(null);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> {
			emailService.sendReservationConfirmation(reserva, false, false);
		});
	}

	@Test
	void sendReservationConfirmation_shouldThrowExceptionWhenTemplateEngineFails() throws MessagingException {
		// Arrange
		ReservaEntity reserva = new ReservaEntity();
		reserva.setId(1L);
		reserva.setEmail("cliente@example.com");
		reserva.setNombre("Cliente");

		MimeMessage mockMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			emailService.sendReservationConfirmation(reserva, false, false);
		});

	}

	@Test
	public void testSendEmail_Success() {
		// Arrange
		String to = "test@example.com";
		String subject = "Test Subject";
		String text = "Test Text";

		// Act
		emailService.sendEmail(to, subject, text);

	}

}
