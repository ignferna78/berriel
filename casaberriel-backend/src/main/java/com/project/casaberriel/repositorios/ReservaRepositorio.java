package com.project.casaberriel.repositorios;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.casaberriel.model.reservas.ReservaEntity;

@Repository
public interface ReservaRepositorio extends JpaRepository<ReservaEntity, Long> {
	// Métodos personalizados si es necesario

	List<ReservaEntity> findAll();
	List<ReservaEntity> findByEmail(String email);

	/**
	 * Encuentra las reservas que se superponen con un rango de fechas.
	 * 
	 * @param desde La fecha de inicio del rango.
	 * @param hasta La fecha de fin del rango.
	 * @return Una lista de reservas que se superponen con el rango dado.
	 */
	@Query(value = "SELECT * FROM reservas r WHERE "
			+ "(r.fecha_entrada <= :fechaSalida AND r.fecha_salida >= :fechaEntrada)", nativeQuery = true)
	List<ReservaEntity> findReservasSuperpuestas(@Param("fechaEntrada") Date desde, @Param("fechaSalida") Date hasta);

}