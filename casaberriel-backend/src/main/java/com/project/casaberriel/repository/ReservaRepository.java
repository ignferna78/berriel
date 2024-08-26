package com.project.casaberriel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.casaberriel.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
	// Métodos personalizados si es necesario

	List<Reserva> findAll();

}