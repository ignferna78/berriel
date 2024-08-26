package com.project.casaberriel.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.casaberriel.model.Reserva;
import com.project.casaberriel.repository.ReservaRepository;

import java.util.List;

@Service
public class ReservaService {

    public ReservaService(ReservaRepository reservaRepository) {
		super();
		this.reservaRepository = reservaRepository;
	}

	@Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Reserva guardarReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
}
