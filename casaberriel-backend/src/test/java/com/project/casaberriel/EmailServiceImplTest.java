package com.project.casaberriel;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.Impl.EmailServiceImpl;

public class EmailServiceImplTest {

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private TemplateEngine templateEngine;

	@InjectMocks
	private EmailServiceImpl emailService;

	@Mock
	private MimeMessageHelper mimeMessageHelper;

	@Mock
	private MimeMessage mimeMessage;

	private Usuario usuario; // Objeto Usuario de prueba

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		// Inicializamos el usuario de prueba
		usuario = new Usuario();
		usuario.setNombre("Juan");
		usuario.setApellidos("Pérez");
		usuario.setEmail("juan.perez@example.com");
		usuario.setDireccion("Calle Falsa 123");
		usuario.setTelefono("123456789");

		// Solo mockea 'templateEngine' en los tests que lo necesiten
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

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

	@Test
	void sendReservationUsuariio_shouldThrowExceptionWhenTemplateEngineFails() throws MessagingException {
		// Arrange
		ReservaEntity reserva = new ReservaEntity();
		reserva.setId(1L);
		reserva.setEmail("cliente@example.com");
		reserva.setNombre("Cliente");

		MimeMessage mockMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			emailService.sendUsuarioConfirmation(usuario, false, false);
		});

	}

	@Test
    public void testSendMail() throws MessagingException {
        // Arrange
        EmailDto email = new EmailDto();
        email.setName("John Doe");
        email.setEmail("john.doe@example.com");
        email.setMessage("This is a test message.");

        MimeMessage mockMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

		// Act & Assert
				RuntimeException exception = assertThrows(RuntimeException.class, () -> {
					emailService.sendMail(email);
				});
      
    }
}
