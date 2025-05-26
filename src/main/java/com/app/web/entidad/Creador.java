package com.app.web.entidad;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "creadores")
public class Creador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "telefono", nullable = true, length = 20)
    private String telefono;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_vinculacion", nullable = false)
    private LocalDate fechaVinculacion;

    public Creador() {}

    public Creador(Long id, String nombres, String apellidos, String correo, String telefono, String rol, Proyecto proyecto, LocalDate fechaVinculacion) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.proyecto = proyecto;
        this.fechaVinculacion = fechaVinculacion;
    }

    public Creador(String nombres, String apellidos, String correo, String telefono, String rol, Proyecto proyecto, LocalDate fechaVinculacion) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.proyecto = proyecto;
        this.fechaVinculacion = fechaVinculacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public LocalDate getFechaVinculacion() { return fechaVinculacion; }
    public void setFechaVinculacion(LocalDate fechaVinculacion) { this.fechaVinculacion = fechaVinculacion; }

    @Override
    public String toString() {
        return "Creador [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos + ", correo=" + correo + ", telefono=" + telefono + ", rol=" + rol + ", proyecto=" + proyecto + ", fechaVinculacion=" + fechaVinculacion + "]";
    }
}