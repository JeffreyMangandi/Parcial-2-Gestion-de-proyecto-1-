package com.app.web.entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ✅ CORREGIDO: Jakarta EE en lugar de Java EE
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;
    
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;
    
    @Column(name = "enlace_github", nullable = true, length = 255)
    private String enlaceGithub;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;
    
    @Column(name = "tecnologias", nullable = true, length = 200)
    private String tecnologias;
    
    // Relación One-to-Many con Creador
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Creador> creadores = new ArrayList<>();
    
    public Proyecto() {
    }

    public Proyecto(Long id, String titulo, String descripcion, String enlaceGithub, LocalDate fechaPublicacion,
            String tecnologias) {
        super();
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.enlaceGithub = enlaceGithub;
        this.fechaPublicacion = fechaPublicacion;
        this.tecnologias = tecnologias;
    }

    public Proyecto(String titulo, String descripcion, String enlaceGithub, LocalDate fechaPublicacion,
            String tecnologias) {
        super();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.enlaceGithub = enlaceGithub;
        this.fechaPublicacion = fechaPublicacion;
        this.tecnologias = tecnologias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEnlaceGithub() {
        return enlaceGithub;
    }

    public void setEnlaceGithub(String enlaceGithub) {
        this.enlaceGithub = enlaceGithub;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getTecnologias() {
        return tecnologias;
    }

    public void setTecnologias(String tecnologias) {
        this.tecnologias = tecnologias;
    }
    
    // Getter y setter para la relación con Creador
    public List<Creador> getCreadores() {
        return creadores;
    }

    public void setCreadores(List<Creador> creadores) {
        this.creadores = creadores;
    }
    
    // Métodos helper para manejar la relación
    public void addCreador(Creador creador) {
        creadores.add(creador);
        creador.setProyecto(this);
    }
    
    public void removeCreador(Creador creador) {
        creadores.remove(creador);
        creador.setProyecto(null);
    }

    @Override
    public String toString() {
        return "Proyecto [id=" + id + ", titulo=" + titulo + ", descripcion=" + descripcion + ", enlaceGithub="
                + enlaceGithub + ", fechaPublicacion=" + fechaPublicacion + ", tecnologias=" + tecnologias + "]";
    }
}