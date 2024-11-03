package com.project.casaberriel.service.Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.casaberriel.dto.ReservaDto;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.ReservaRepositorio;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.ReservaService;
import com.project.casaberriel.utils.Utils;

@Service
public class ReservaServiceImpl implements ReservaService {

	@Autowired
	private ReservaRepositorio reservaRepository;
	
	@Autowired
	private UsuarioRepositorio usuarioRepository;
	
	@Autowired
	private IEmailService emailService;

	private static final String FORMATO = "dd/MM/yyyy";
	SimpleDateFormat formatter = new SimpleDateFormat(FORMATO);

	@Override
	public List<ReservaEntity> listarReservas() {
		return reservaRepository.findAll();
	}

	@Override
	public ReservaEntity guardarReserva(ReservaEntity reserva, ReservaForm fecha, String email,boolean cancelada, boolean modificada) throws MessagingException {
	    Date desde = obtenerFechaFormateada(fecha.getFechaEntrada());
	    Date hasta = obtenerFechaFormateada(fecha.getFechaSalida());
	    reserva.setFechaEntrada(desde);
	    reserva.setFechaSalida(hasta);
	    
	    // Obtén el usuario autenticado desde el username y asignarlo a la reserva
	    Usuario usuario = usuarioRepository.findByEmail(email);
	    if (usuario != null) {
	        reserva.setUsuario(usuario);
	    } else {
	        throw new IllegalArgumentException("Usuario no encontrado");
	    }
	    LocalDate fechaEntrada = LocalDate.parse(fecha.getFechaEntrada(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    LocalDate fechaSalida = LocalDate.parse(fecha.getFechaSalida(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    double precioTotal = Utils.calculateTotalPrice(fechaEntrada, fechaSalida, reserva.getPrecioPorDia());
	    reserva.setPrecioTotal(precioTotal);
	    reserva.setPrecioPorDia(80.0);
	    ReservaEntity savedReserva = reservaRepository.save(reserva);
	    emailService.sendReservationConfirmation(savedReserva,cancelada,modificada);
	    return savedReserva;
	}


	@Override
	public ReservaEntity obtenerReservaPorId(Long id) {
		return reservaRepository.findById(id).orElse(null);
	}

	@Override
	public List<ReservaEntity> obtenerReservaPorEmail(String email) {
	    return reservaRepository.findByEmail(email);
	}

	@Override
	public void eliminarReserva(Long id) {
		reservaRepository.deleteById(id);

	}

	/**
	 * Comprueba si hay disponibilidad entre las fechas proporcionadas.
	 *
	 * @param fechaEntrada La fecha de inicio de la comprobación.
	 * @param fechaSalida  La fecha de fin de la comprobación.
	 * @return true si hay disponibilidad, false si no hay disponibilidad.
	 */
	@Override
	public boolean comprobarDisponibilidad(ReservaForm fecha) {
		Date desde = obtenerFechaFormateada(fecha.getFechaEntrada());
		Date hasta = obtenerFechaFormateada(fecha.getFechaSalida());

		// Obtener las reservas que se superpongan con el rango de fechas dado.
		List<ReservaEntity> reservasSuperpuestas = reservaRepository.findReservasSuperpuestas(desde, hasta);

		// Si no hay reservas que se superpongan, hay disponibilidad.
		return reservasSuperpuestas.isEmpty();
	}

	// Método extraído para formatear fechas
	private Date obtenerFechaFormateada(String fecha) {
		if (fecha != null && !fecha.isBlank()) {
			try {
				return Utils.obtenerDate(fecha, FORMATO);
			} catch (ParseException e) {
				e.getMessage();
			}
		}
		return null;
	}

}
