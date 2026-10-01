package com.project.casaberriel.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.project.casaberriel.model.usuarios.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
	@NonNull
	List<Rol> findAll();
}
