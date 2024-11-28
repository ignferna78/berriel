package com.project.casaberriel.service.Impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
	SimpleDateFormat formatter = new SimpleDateFormat(FORMATO);

	@Override
	public List<ReservaEntity> listarReservas() {
		return reservaRepository.findAll();
	}

	@Override
	public ReservaEntity guardarReserva(ReservaEntity reserva, ReservaForm fecha, String email,boolean cancelada, boolean modificada) throws MessagingException {
	    Date desde = Utils.obtenerFechaFormateada(fecha.getFechaEntrada());
	    Date hasta = Utils.obtenerFechaFormateada(fecha.getFechaSalida());
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
	    long totalDias = Utils.calculateTotalDays(fechaEntrada, fechaSalida);
	    double precioTotal = Utils.calculateTotalPrice(fechaEntrada, fechaSalida, reserva.getPrecioPorDia());
	    reserva.setPrecioTotal(precioTotal);
	    reserva.setPrecioPorDia(80.0);
	    if(totalDias==0) {reserva.setPrecioTotal(reserva.getPrecioPorDia());}
	    LOGGER.info("Precio total: " + reserva.getPrecioTotal());
	    ReservaEntity savedReserva = reservaRepository.save(reserva);
	    emailService.sendReservationConfirmation(savedReserva,cancelada,modificada);
	    return savedReserva;
	}


	@Override
	public ReservaEntity obtenerReservaPorId(Long id) {
		return reservaRepository.findById(id).orElse(null);
	}
	@Override
	public List<ReservaEntity> obtenerReservaPorIdUsuario(Long userId) {
	    return reservaRepository.findByUsuarioId(userId); // Suponiendo que existe este método en el repositorio
	}

	@Override
	public List<ReservaEntity> obtenerReservaPorEmail(String email) {
	    return reservaRepository.findByEmail(email);
	}

	@Override
	public ReservaEntity eliminarReserva(Long id, String email, boolean cancelada, boolean modificada) {
	    // Primero obtenemos la reserva antes de eliminarla
	    ReservaEntity reserva = reservaRepository.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada para el id: " + id));
	    // Ahora eliminamos la reserva
	    reservaRepository.deleteById(id);
	    try {
			emailService.sendReservationConfirmation(reserva,true,modificada);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	    // Retornamos la reserva eliminada
	    return reserva;
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
		Date desde = Utils.obtenerFechaFormateada(fecha.getFechaEntrada());
		Date hasta = Utils.obtenerFechaFormateada(fecha.getFechaSalida());

		// Obtener las reservas que se superpongan con el rango de fechas dado.
		List<ReservaEntity> reservasSuperpuestas = reservaRepository.findReservasSuperpuestas(desde, hasta);

		// Si no hay reservas que se superpongan, hay disponibilidad.
		return reservasSuperpuestas.isEmpty();
	}
	
	@Override
	public boolean comprobarDisponibilidadEdicion(ReservaForm fecha,Long reservaId) {
		Date desde = Utils.obtenerFechaFormateada(fecha.getFechaEntrada());
		Date hasta = Utils.obtenerFechaFormateada(fecha.getFechaSalida());
		  if (reservaId == null) {
		        System.out.println("El ID de la reserva es nulo");
		        return false;  
		    }
		// Obtener las reservas que se superpongan con el rango de fechas dado.
		List<ReservaEntity> reservasSuperpuestas = reservaRepository.findReservasSuperpuestasEdit(desde, hasta,reservaId);
		System.out.println("Número de reservas superpuestas: " + reservasSuperpuestas.size());
		// Si no hay reservas que se superpongan, hay disponibilidad.
		return reservasSuperpuestas.isEmpty();
	}

	

}
