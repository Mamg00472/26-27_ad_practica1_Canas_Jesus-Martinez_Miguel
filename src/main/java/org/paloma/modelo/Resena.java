package org.paloma.modelo;

/**
 * Representa la reseña que un usuario realiza sobre un libro, con un comentario
 * y una valoración de 1 a 5.
 */
public class Resena {

    /** Identificador único de la reseña. */
    private int idResena;
    /** Texto del comentario de la reseña. */
    private String comentarios;
    /** Valoración otorgada al libro, entre 1 y 5. */
    private int valoracion; // de 1 a 5
    /** Usuario que escribió la reseña. */
    private Usuario usuario; // FK idUsuario
    /** Libro al que corresponde la reseña. */
    private Libro libro;     // FK idLibro

    /** Crea una reseña sin inicializar sus datos. */
    public Resena() {
    }

    /**
     * Crea una reseña con los datos indicados.
     *
     * @param idResena identificador de la reseña
     * @param comentarios texto del comentario
     * @param valoracion valoración del libro, entre 1 y 5
     * @param usuario usuario autor de la reseña
     * @param libro libro reseñado
     * @throws IllegalArgumentException si la valoración no está entre 1 y 5
     */
    public Resena(int idResena, String comentarios, int valoracion, Usuario usuario, Libro libro) {
        this.idResena = idResena;
        this.comentarios = comentarios;
        setValoracion(valoracion);
        this.usuario = usuario;
        this.libro = libro;
    }

    /**
     * Obtiene el identificador de la reseña.
     *
     * @return identificador de la reseña
     */
    public int getIdResena() {
        return idResena;
    }

    /**
     * Establece el identificador de la reseña.
     *
     * @param idResena nuevo identificador
     */
    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    /**
     * Obtiene el comentario de la reseña.
     *
     * @return texto del comentario
     */
    public String getComentarios() {
        return comentarios;
    }

    /**
     * Establece el comentario de la reseña.
     *
     * @param comentarios nuevo texto del comentario
     */
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Obtiene la valoración de la reseña.
     *
     * @return valoración entre 1 y 5
     */
    public int getValoracion() {
        return valoracion;
    }

    /**
     * Establece la valoración de la reseña.
     *
     * @param valoracion nueva valoración, entre 1 y 5
     * @throws IllegalArgumentException si la valoración no está entre 1 y 5
     */
    public void setValoracion(int valoracion) {
        if (valoracion < 1 || valoracion > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5");
        }
        this.valoracion = valoracion;
    }

    /**
     * Obtiene el usuario autor de la reseña.
     *
     * @return usuario autor
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario autor de la reseña.
     *
     * @param usuario nuevo usuario autor
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el libro reseñado.
     *
     * @return libro asociado
     */
    public Libro getLibro() {
        return libro;
    }

    /**
     * Establece el libro al que corresponde la reseña.
     *
     * @param libro nuevo libro asociado
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    /**
     * Devuelve una representación textual de la reseña.
     *
     * @return texto con la valoración, el comentario y las entidades asociadas
     */
    @Override
    public String toString() {
        return "Reseña id:" + idResena
                + " - valoracion:" + valoracion
                + " - comentarios:" + comentarios
                + " - usuario:" + (usuario != null ? usuario.getNombre() : "No se ha identificado el autor.")
                + " - libro:" + libro.getTitulo();
    }
}
