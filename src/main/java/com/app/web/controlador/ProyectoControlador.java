package com.app.web.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.app.web.entidad.Proyecto;
import com.app.web.servicio.ProyectoServicio;

@Controller
public class ProyectoControlador {

    @Autowired
    private ProyectoServicio servicio;

    @GetMapping({"/proyectos", "/proyectos/listar"})
    public String listarProyectos(Model modelo) {
        modelo.addAttribute("proyectos", servicio.listarTodosLosProyectos());
        return "proyectos"; // nos retorna al archivo proyectos.html
    }

    @GetMapping("/proyectos/nuevo")
    public String mostrarFormularioDeRegistrarProyecto(Model modelo) {
        Proyecto proyecto = new Proyecto();
        modelo.addAttribute("proyecto", proyecto);
        return "crear_proyecto";
    }

    @PostMapping("/proyectos")
    public String guardarProyecto(@ModelAttribute("proyecto") Proyecto proyecto) {
        servicio.guardarProyecto(proyecto);
        return "redirect:/proyectos";
    }

    @GetMapping("/proyectos/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("proyecto", servicio.obtenerProyectoPorId(id));
        return "editar_proyecto";
    }

    @PostMapping("/proyectos/{id}")
    public String actualizarProyecto(@PathVariable Long id, @ModelAttribute("proyecto") Proyecto proyecto,
            Model modelo) {
        Proyecto proyectoExistente = servicio.obtenerProyectoPorId(id);
        proyectoExistente.setId(id);
        proyectoExistente.setTitulo(proyecto.getTitulo());
        proyectoExistente.setDescripcion(proyecto.getDescripcion());
        proyectoExistente.setEnlaceGithub(proyecto.getEnlaceGithub());
        proyectoExistente.setFechaPublicacion(proyecto.getFechaPublicacion());
        proyectoExistente.setTecnologias(proyecto.getTecnologias());

        servicio.actualizarProyecto(proyectoExistente);
        return "redirect:/proyectos";
    }

    @GetMapping("/proyectos/{id}")
    public String eliminarProyecto(@PathVariable Long id) {
        servicio.eliminarProyecto(id);
        return "redirect:/proyectos";
    }
}