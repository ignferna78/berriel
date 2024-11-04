package com.project.casaberriel.service;

import java.util.List;

import javax.mail.MessagingException;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;

public interface ReservaService {

	List<ReservaEntity> listarReservas();

	public ReservaEntity obtenerReservaPorId(Long id);

	public ReservaEntity eliminarReserva(Long id, String email, boolean cancelada, boolean modificada);

	public boolean comprobarDisponibilidad(ReservaForm fecha);

	ReservaEntity guardarReserva(ReservaEntity reserva, ReservaForm fecha, String username, boolean cancelada,
			boolean modificada) throws MessagingException;

	List<ReservaEntity> obtenerReservaPorEmail(String email);

}
