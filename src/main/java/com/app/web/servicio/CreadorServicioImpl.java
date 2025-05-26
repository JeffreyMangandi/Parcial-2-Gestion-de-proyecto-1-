package com.app.web.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Creador;
import com.app.web.repositorio.CreadorRepositorio;

@Service
public class CreadorServicioImpl implements CreadorServicio {

    @Autowired
    private CreadorRepositorio repositorio;

    @Override
    public List<Creador> listarTodosLosCreadores() {
        return repositorio.findAll();
    }

    @Override
    public Creador guardarCreador(Creador creador) {
        return repositorio.save(creador);
    }

    @Override
    public Creador obtenerCreadorPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Override
    public Creador actualizarCreador(Creador creador) {
        return repositorio.save(creador);
    }

    @Override
    public void eliminarCreador(Long id) {
        repositorio.deleteById(id);
    }
}
