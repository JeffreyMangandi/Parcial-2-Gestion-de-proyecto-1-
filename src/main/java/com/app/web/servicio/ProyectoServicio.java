package com.app.web.servicio;

import java.util.List;

import com.app.web.entidad.Proyecto;

public interface ProyectoServicio {

    public List<Proyecto> listarTodosLosProyectos();
    
    public Proyecto guardarProyecto(Proyecto proyecto);
    
    public Proyecto obtenerProyectoPorId(Long id);
    
    public Proyecto actualizarProyecto(Proyecto proyecto);
    
    public void eliminarProyecto(Long id);
}
