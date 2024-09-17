package com.project.casaberriel.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.repositorios.ReservaRepositorio;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {

    public ReservaService(ReservaRepositorio reservaRepository) {
		super();
		this.reservaRepository = reservaRepository;
	}

	@Autowired
    private ReservaRepositorio reservaRepository;

    public List<ReservaEntity> listarReservas() {
        return reservaRepository.findAll();
    }

    public ReservaEntity guardarReserva(ReservaEntity reserva) {
        return reservaRepository.save(reserva);
    }

    public ReservaEntity obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
    

    /**
     * Comprueba si hay disponibilidad entre las fechas proporcionadas.
     *
     * @param fechaEntrada La fecha de inicio de la comprobación.
     * @param fechaSalida    La fecha de fin de la comprobación.
     * @return true si hay disponibilidad, false si no hay disponibilidad.
     */
    public boolean comprobarDisponibilidad(LocalDate fechaEntrada, LocalDate fechaSalida) {
        // Obtener las reservas que se superpongan con el rango de fechas dado.
        List<ReservaEntity> reservasSuperpuestas = reservaRepository.findReservasSuperpuestas(fechaEntrada, fechaSalida);

        // Si no hay reservas que se superpongan, hay disponibilidad.
        return reservasSuperpuestas.isEmpty();
    }
    
}
