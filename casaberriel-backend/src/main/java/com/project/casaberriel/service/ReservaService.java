package com.project.casaberriel.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.repositorios.ReservaRepositorio;

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
    
    
}
