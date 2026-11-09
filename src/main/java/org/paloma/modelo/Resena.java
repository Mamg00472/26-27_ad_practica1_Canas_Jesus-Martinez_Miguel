package org.paloma.modelo;

/**
 * Representa la reseña que un usuario realiza sobre un libro, con un comentario
 * y una valoración de 1 a 5.
 */
public class Resena {

    private int idResena;
    private String comentarios;
    private int valoracion; // de 1 a 5
    private Usuario usuario; // FK idUsuario
    private Libro libro;     // FK idLibro

    public Resena() {
    }

    public Resena(int idResena, String comentarios, int valoracion, Usuario usuario, Libro libro) {
        this.idResena = idResena;
        this.comentarios = comentarios;
        setValoracion(valoracion);
        this.usuario = usuario;
        this.libro = libro;
    }

    public int getIdResena() {
        return idResena;
    }

    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        if (valoracion < 1 || valoracion > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5");
        }
        this.valoracion = valoracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    @Override
    public String toString() {
        return "Reseña id:" + idResena
                + " - valoracion:" + valoracion
                + " - comentarios:" + comentarios
                + " - usuario:" + (usuario != null ? usuario.getNombre() : "No se ha identificado el autor.")
                + " - libro:" + libro.getTitulo();
    }
}
