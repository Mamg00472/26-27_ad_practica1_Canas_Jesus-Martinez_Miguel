package org.paloma.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a una persona usuaria de la biblioteca y las reseñas que ha escrito.
 */
public class Usuario {

    /** Identificador único del usuario. */
    private int idUsuario;
    /** Nombre del usuario. */
    private String nombre;
    /** Dirección de correo electrónico del usuario. */
    private String email;
    /** Fecha en que el usuario se inscribió. */
    private LocalDate fechaInscripcion;
    /** Reseñas escritas por este usuario. */
    private List<Resena> resenas = new ArrayList<>();

    /** Crea un usuario sin inicializar sus datos. */
    public Usuario() {
    }

    /**
     * Crea un usuario con los datos indicados.
     *
     * @param idUsuario identificador del usuario
     * @param nombre nombre del usuario
     * @param email dirección de correo electrónico
     * @param fechaInscripcion fecha de inscripción
     */
    public Usuario(int idUsuario, String nombre, String email, LocalDate fechaInscripcion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return identificador del usuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUsuario nuevo identificador
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección de correo electrónico del usuario.
     *
     * @return dirección de correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece la dirección de correo electrónico del usuario.
     *
     * @param email nueva dirección de correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la fecha de inscripción del usuario.
     *
     * @return fecha de inscripción
     */
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    /**
     * Establece la fecha de inscripción del usuario.
     *
     * @param fechaInscripcion nueva fecha de inscripción
     */
    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * Obtiene las reseñas escritas por el usuario.
     *
     * @return lista de reseñas del usuario
     */
    public List<Resena> getResenas() {
        return resenas;
    }

    /**
     * Establece las reseñas escritas por el usuario.
     *
     * @param resenas nueva lista de reseñas
     */
    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    /**
     * Añade una reseña al usuario y establece la relación inversa en ella.
     *
     * @param resena reseña que se va a asociar
     */
    public void agregarResena(Resena resena) {
        resenas.add(resena);
        resena.setUsuario(this);
    }

    /**
     * Devuelve una representación textual del usuario.
     *
     * @return texto con los datos principales del usuario
     */
    @Override
    public String toString() {
        return "Usuario - id:" + idUsuario
                + " - nombre:" + nombre
                + " - email:" + email
                + " - fechaInscripcion:" + fechaInscripcion;
    }
}
