package com.project.casaberriel.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.casaberriel.model.reservas.ReservaEntity;

@Repository
public interface ReservaRepositorio extends JpaRepository<ReservaEntity, Long> {
	// Métodos personalizados si es necesario

	List<ReservaEntity> findAll();

}