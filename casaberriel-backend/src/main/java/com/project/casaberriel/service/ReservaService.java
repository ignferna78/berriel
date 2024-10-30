package com.project.casaberriel.service;

import java.util.List;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;


public interface ReservaService {

	List<ReservaEntity> listarReservas();

	

	public ReservaEntity obtenerReservaPorId(Long id);

	public void eliminarReserva(Long id);

	public boolean comprobarDisponibilidad(ReservaForm fecha);

	ReservaEntity guardarReserva(ReservaEntity reserva, ReservaForm fecha, String username);



	List<ReservaEntity> obtenerReservaPorEmail(String email);


}
