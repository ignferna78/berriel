package com.project.casaberriel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.ReservaRepositorio;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.Impl.ReservaServiceImpl;
import com.project.casaberriel.utils.Utils;

class ReservaServiceImplTest {

    @Mock
    private ReservaRepositorio reservaRepository;

    @Mock
    private UsuarioRepositorio usuarioRepository;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private ReservaServiceImpl reservaService;
    
    private ReservaEntity reserva;
    
    @Mock
    private Utils utils;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reserva = new ReservaEntity();
    }

    @Test
    void guardarReserva_shouldSaveAndSendEmail() throws MessagingException {
        // Arrange
        ReservaEntity reserva = new ReservaEntity();
        reserva.setPrecioPorDia(80.0);

        ReservaForm reservaForm = new ReservaForm();
        reservaForm.setFechaEntrada("25/11/2024");
        reservaForm.setFechaSalida("27/11/2024");

        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@example.com");

        when(usuarioRepository.findByEmail(eq("cliente@example.com"))).thenReturn(usuario);
        when(reservaRepository.save(any(ReservaEntity.class))).thenReturn(reserva);

        // Act
        ReservaEntity result = reservaService.guardarReserva(reserva, reservaForm, "cliente@example.com", false, false);

        // Assert
        verify(usuarioRepository, times(1)).findByEmail(eq("cliente@example.com"));
        verify(reservaRepository, times(1)).save(any(ReservaEntity.class));
        verify(emailService, times(1)).sendReservationConfirmation(any(ReservaEntity.class), eq(false), eq(false));

        assert result != null;
        assert result.getPrecioTotal() == 160.0; // 80 * 2 días
    }

    @Test
    void guardarReserva_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        ReservaEntity reserva = new ReservaEntity();
        ReservaForm reservaForm = new ReservaForm();
        reservaForm.setFechaEntrada("25/11/2024");
        reservaForm.setFechaSalida("27/11/2024");

        when(usuarioRepository.findByEmail(eq("cliente@example.com"))).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.guardarReserva(reserva, reservaForm, "cliente@example.com", false, false);
        });

        assert exception.getMessage().contains("Usuario no encontrado");
    }
    
    @Test
    public void testEliminarReserva_success() throws MessagingException {
        // Configuramos el mock para que findById devuelva la reserva
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.of(reserva));
        
        // Ejecutamos el método
        ReservaEntity resultado = reservaService.eliminarReserva(1L, "cliente@example.com", false, false);

        // Verificamos que la reserva fue eliminada
        verify(reservaRepository).deleteById(1L);

        // Verificamos que se haya enviado el correo
        verify(emailService).sendReservationConfirmation(reserva, true, false);

        // Comprobamos que el resultado sea el correcto
        assertNotNull(resultado);
        assertEquals(reserva.getId(), resultado.getId());
    }
    
    @Test
    public void testListarReservas() {
        // Arrange
        List<ReservaEntity> expectedReservas = new ArrayList<>();
        expectedReservas.add(new ReservaEntity());
        expectedReservas.add(new ReservaEntity());

        when(reservaRepository.findAll()).thenReturn(expectedReservas);

        // Debugging statements
        System.out.println("Expected Reservas: " + expectedReservas);

        // Act
        List<ReservaEntity> actualReservas = reservaService.listarReservas();

        // Verificación de Resultados
        assertNotNull(actualReservas);
        assertEquals(expectedReservas.size(), actualReservas.size());
        assertEquals(expectedReservas, actualReservas);
        verify(reservaRepository).findAll();
    }
    


    @Test
    public void testObtenerFechaFormateada_nullOrEmpty() {
        // Probamos con una fecha nula
        Date resultNull = reservaService.obtenerFechaFormateada(null);
        assertNull(resultNull);

        // Probamos con una fecha vacía
        Date resultEmpty = reservaService.obtenerFechaFormateada("");
        assertNull(resultEmpty);
    }
    
    @Test
    public void testObtenerReservaPorId() {
        // Arrange
        Long id = 1L;
        ReservaEntity expectedReserva = new ReservaEntity();
        expectedReserva.setId(id);

        when(reservaRepository.findById(id)).thenReturn(Optional.of(expectedReserva));

        // Debugging statements
        System.out.println("Expected Reserva: " + expectedReserva);

        // Act
        ReservaEntity actualReserva = reservaService.obtenerReservaPorId(id);

        // Verificación de Resultados
        assertNotNull(actualReserva);
        assertEquals(expectedReserva, actualReserva);
        verify(reservaRepository).findById(id);
    }
    @Test
    public void testObtenerReservaPorIdUsuario() {
        // Arrange
        Long userId = 1L;
        List<ReservaEntity> expectedReservas = new ArrayList<>();
        expectedReservas.add(new ReservaEntity());
        expectedReservas.add(new ReservaEntity());

        when(reservaRepository.findByUsuarioId(userId)).thenReturn(expectedReservas);

        // Act
        List<ReservaEntity> actualReservas = reservaService.obtenerReservaPorIdUsuario(userId);

        // Assert
        assertNotNull(actualReservas);
        assertEquals(expectedReservas.size(), actualReservas.size());
        assertEquals(expectedReservas, actualReservas);
        verify(reservaRepository).findByUsuarioId(userId);
    }

    @Test
    public void testObtenerReservaPorEmail() {
        // Arrange
        String email = "test@example.com";
        List<ReservaEntity> expectedReservas = new ArrayList<>();
        expectedReservas.add(new ReservaEntity());
        expectedReservas.add(new ReservaEntity());

        when(reservaRepository.findByEmail(email)).thenReturn(expectedReservas);

        // Act
        List<ReservaEntity> actualReservas = reservaService.obtenerReservaPorEmail(email);

        // Assert
        assertNotNull(actualReservas);
        assertEquals(expectedReservas.size(), actualReservas.size());
        assertEquals(expectedReservas, actualReservas);
        verify(reservaRepository).findByEmail(email);
    }
}
