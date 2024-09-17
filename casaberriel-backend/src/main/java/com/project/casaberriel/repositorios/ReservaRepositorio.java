package com.project.casaberriel.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.casaberriel.model.reservas.ReservaEntity;

@Repository
public interface ReservaRepositorio extends JpaRepository<ReservaEntity, Long> {
	// Métodos personalizados si es necesario

	List<ReservaEntity> findAll();

	 /**
     * Encuentra las reservas que se superponen con un rango de fechas.
     * 
     * @param fechaEntrada La fecha de inicio del rango.
     * @param fechaSalida    La fecha de fin del rango.
     * @return Una lista de reservas que se superponen con el rango dado.
     */
    @Query(value="SELECT * FROM reservas r WHERE " +
           "(r.fechaEntrada <= :fechaSalida AND r.fechaSalida >= :fechaEntrada)", nativeQuery=true)
    List<ReservaEntity> findReservasSuperpuestas(LocalDate fechaEntrada, LocalDate fechaSalida);

}