package com.project.casaberriel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.casaberriel.model.usuarios.Rol;
import com.project.casaberriel.repositorios.RolRepository;

@Service
public class RolService {

	@Autowired
    private RolRepository rolRepository;

    public List<Rol> obtenerTodosRoles() {
        return rolRepository.findAll();
    }
}
