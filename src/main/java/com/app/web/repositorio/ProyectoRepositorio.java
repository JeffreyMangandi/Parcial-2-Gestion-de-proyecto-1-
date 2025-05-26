package com.app.web.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entidad.Proyecto;

@Repository
public interface ProyectoRepositorio extends JpaRepository<Proyecto, Long> {

}