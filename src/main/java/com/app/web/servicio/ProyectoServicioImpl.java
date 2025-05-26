package com.app.web.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Proyecto;
import com.app.web.repositorio.ProyectoRepositorio;

@Service
public class ProyectoServicioImpl implements ProyectoServicio {

    @Autowired
    private ProyectoRepositorio repositorio;

    @Override
    public List<Proyecto> listarTodosLosProyectos() {
        return repositorio.findAll();
    }

    @Override
    public Proyecto guardarProyecto(Proyecto proyecto) {
        return repositorio.save(proyecto);
    }

    @Override
    public Proyecto obtenerProyectoPorId(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public Proyecto actualizarProyecto(Proyecto proyecto) {
        return repositorio.save(proyecto);
    }

    @Override
    public void eliminarProyecto(Long id) {
        repositorio.deleteById(id);
    }

}