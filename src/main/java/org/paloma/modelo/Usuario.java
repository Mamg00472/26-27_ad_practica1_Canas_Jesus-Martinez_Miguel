package org.paloma.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a una persona usuaria de la biblioteca y las reseñas que ha escrito.
 */
public class Usuario {

    private int idUsuario;
    private String nombre;
    private String email;
    private LocalDate fechaInscripcion;
    private List<Resena> resenas = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String email, LocalDate fechaInscripcion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.fechaInscripcion = fechaInscripcion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public List<Resena> getResenas() {
        return resenas;
    }

    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    public void agregarResena(Resena resena) {
        resenas.add(resena);
        resena.setUsuario(this);
    }

    @Override
    public String toString() {
        return "Usuario - id:" + idUsuario
                + " - nombre:" + nombre
                + " - email:" + email
                + " - fechaInscripcion:" + fechaInscripcion;
    }
}
