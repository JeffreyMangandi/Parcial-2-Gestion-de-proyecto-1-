package com.app.web.servicio;

import java.util.List;

import com.app.web.entidad.Creador;

public interface CreadorServicio {

    public List<Creador> listarTodosLosCreadores();
    
    public Creador guardarCreador(Creador creador);
    
    public Creador obtenerCreadorPorId(Long id);
    
    public Creador actualizarCreador(Creador creador);
    
    public void eliminarCreador(Long id);
}
