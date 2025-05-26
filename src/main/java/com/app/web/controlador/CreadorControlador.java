package com.app.web.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.web.entidad.Creador;
import com.app.web.servicio.CreadorServicio;
import com.app.web.servicio.ProyectoServicio;

@Controller
public class CreadorControlador {
    
    @Autowired
    private CreadorServicio servicio;
    
    @Autowired
    private ProyectoServicio proyectoServicio;
    
    @GetMapping({"/creadores", "/creadores/listar"})
    public String listarCreadores(Model modelo) {
        List<Creador> creadores = servicio.listarTodosLosCreadores();
        
        // 🔍 DEBUGGING - Agregar logs para verificar los datos
        System.out.println("=== 🔍 DEBUG CREADORES ===");
        System.out.println("📊 Total creadores en BD: " + creadores.size());
        
        if (creadores.isEmpty()) {
            System.out.println("⚠️  ¡NO HAY CREADORES EN LA BASE DE DATOS!");
            System.out.println("   Verifica que tengas datos insertados en la tabla 'creadores'");
        } else {
            System.out.println("✅ Creadores encontrados:");
            for (Creador c : creadores) {
                System.out.println("   - ID: " + c.getId() + 
                                 " | Nombre: " + c.getNombres() + " " + c.getApellidos() + 
                                 " | Email: " + c.getCorreo() + 
                                 " | Rol: " + c.getRol());
                if (c.getProyecto() != null) {
                    System.out.println("     Proyecto: " + c.getProyecto().getTitulo());
                } else {
                    System.out.println("     ⚠️  Sin proyecto asignado");
                }
            }
        }
        System.out.println("========================");
        
        modelo.addAttribute("creadores", creadores);
        
        // Calcular estadísticas para el template
        if (creadores != null && !creadores.isEmpty()) {
            // Contar proyectos únicos asignados
            long proyectosUnicos = creadores.stream()
                .filter(c -> c.getProyecto() != null && c.getProyecto().getTitulo() != null)
                .map(c -> c.getProyecto().getTitulo())
                .distinct()
                .count();
            
            // Contar roles únicos
            long rolesUnicos = creadores.stream()
                .filter(c -> c.getRol() != null)
                .map(c -> c.getRol())
                .distinct()
                .count();
            
            modelo.addAttribute("proyectosUnicos", proyectosUnicos);
            modelo.addAttribute("rolesUnicos", rolesUnicos);
            
            System.out.println("📊 Estadísticas calculadas:");
            System.out.println("- Total creadores: " + creadores.size());
            System.out.println("- Proyectos únicos: " + proyectosUnicos);
            System.out.println("- Roles únicos: " + rolesUnicos);
        } else {
            modelo.addAttribute("proyectosUnicos", 0);
            modelo.addAttribute("rolesUnicos", 0);
            System.out.println("📊 No hay creadores, estadísticas en 0");
        }
        
        // 🔧 CAMBIO PRINCIPAL: usar minúscula
        return "creadores";  // ← CAMBIADO de "Creadores" a "creadores"
    }
    
    // 🔍 Método de debugging para verificar datos
    @GetMapping("/creadores/debug")
    @ResponseBody
    public String debugCreadores() {
        List<Creador> creadores = servicio.listarTodosLosCreadores();
        StringBuilder sb = new StringBuilder();
        sb.append("=== 🔍 DEBUG CREADORES ===\n");
        sb.append("📊 Total en BD: ").append(creadores.size()).append("\n\n");
        
        if (creadores.isEmpty()) {
            sb.append("⚠️  ¡NO HAY DATOS EN LA TABLA 'creadores'!\n");
            sb.append("Ejecuta este SQL para insertar datos de prueba:\n\n");
            sb.append("INSERT INTO creadores (nombres, apellidos, correo, telefono, rol, proyecto_id, fecha_vinculacion) VALUES\n");
            sb.append("('Jose Manuel', 'Rivas Rivas', 'ejemplo@gmail.com', '78999999', 'Diseñador,Tecnico', 3, '2025-04-17'),\n");
            sb.append("('Isaac Lemus', 'Rodriguez Ramos', 'ramos@hotmail.com', '78900987', 'consultor, tecnico, administrador', 2, '2025-04-03');\n");
        } else {
            for (Creador c : creadores) {
                sb.append("🔹 ID: ").append(c.getId()).append("\n");
                sb.append("   Nombre: ").append(c.getNombres()).append(" ").append(c.getApellidos()).append("\n");
                sb.append("   Email: ").append(c.getCorreo()).append("\n");
                sb.append("   Teléfono: ").append(c.getTelefono()).append("\n");
                sb.append("   Rol: ").append(c.getRol()).append("\n");
                sb.append("   Proyecto: ").append(c.getProyecto() != null ? c.getProyecto().getTitulo() : "❌ Sin proyecto").append("\n");
                sb.append("   Fecha vinculación: ").append(c.getFechaVinculacion()).append("\n");
                sb.append("---\n");
            }
        }
        
        return sb.toString();
    }
    
    @GetMapping("/creadores/nuevo")
    public String mostrarFormularioDeRegistrarCreador(Model modelo) {
        Creador creador = new Creador();
        modelo.addAttribute("creador", creador);
        modelo.addAttribute("proyectos", proyectoServicio.listarTodosLosProyectos());
        return "crear_creador";
    }
    
    @PostMapping("/creadores")
    public String guardarCreador(@ModelAttribute("creador") Creador creador) {
        System.out.println("💾 Guardando creador: " + creador.getNombres() + " " + creador.getApellidos());
        Creador guardado = servicio.guardarCreador(creador);
        System.out.println("✅ Creador guardado con ID: " + guardado.getId());
        return "redirect:/creadores";
    }
    
    @GetMapping("/creadores/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model modelo) {
        Creador creador = servicio.obtenerCreadorPorId(id);
        if (creador != null) {
            modelo.addAttribute("creador", creador);
            modelo.addAttribute("proyectos", proyectoServicio.listarTodosLosProyectos());
            return "editar_creador";
        } else {
            System.out.println("❌ No se encontró creador con ID: " + id);
            return "redirect:/creadores";
        }
    }
    
    @PostMapping("/creadores/editar/{id}")
    public String actualizarCreador(@PathVariable Long id, @ModelAttribute("creador") Creador creador, Model modelo) {
        Creador creadorExistente = servicio.obtenerCreadorPorId(id);
        if (creadorExistente != null) {
            creadorExistente.setId(id);
            creadorExistente.setNombres(creador.getNombres());
            creadorExistente.setApellidos(creador.getApellidos());
            creadorExistente.setCorreo(creador.getCorreo());
            creadorExistente.setTelefono(creador.getTelefono());
            creadorExistente.setRol(creador.getRol());
            creadorExistente.setFechaVinculacion(creador.getFechaVinculacion());
            creadorExistente.setProyecto(creador.getProyecto());
            
            servicio.actualizarCreador(creadorExistente);
            System.out.println("✅ Creador actualizado: " + creadorExistente.getNombres());
        } else {
            System.out.println("❌ No se pudo actualizar, creador no encontrado con ID: " + id);
        }
        return "redirect:/creadores";
    }
    
    @GetMapping("/creadores/eliminar/{id}")
    public String eliminarCreador(@PathVariable Long id) {
        Creador creador = servicio.obtenerCreadorPorId(id);
        if (creador != null) {
            System.out.println("🗑️  Eliminando creador: " + creador.getNombres() + " " + creador.getApellidos());
            servicio.eliminarCreador(id);
            System.out.println("✅ Creador eliminado exitosamente");
        } else {
            System.out.println("❌ No se encontró creador para eliminar con ID: " + id);
        }
        return "redirect:/creadores";
    }
}