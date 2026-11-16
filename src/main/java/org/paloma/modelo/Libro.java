package org.paloma.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un libro de la biblioteca, sus escritores y las reseñas asociadas.
 */
public class Libro {

    /** Identificador único del libro. */
    private int idLibro;
    /** Número ISBN del libro. */
    private String isbn;
    /** Título del libro. */
    private String titulo;
    /** Género literario del libro. */
    private String genero;
    /** Fecha de publicación del libro. */
    private LocalDate fechaPublicacion;
    /** Escritores asociados al libro. */
    private List<Escritor> escritores = new ArrayList<>();
    /** Reseñas asociadas al libro. */
    private List<Resena> resenas = new ArrayList<>();

    /** Crea un libro sin inicializar sus datos. */
    public Libro() {
    }

    /**
     * Crea un libro con los datos bibliográficos indicados.
     *
     * @param idLibro identificador del libro
     * @param isbn número ISBN
     * @param titulo título del libro
     * @param genero género literario
     * @param fechaPublicacion fecha de publicación
     */
    public Libro(int idLibro, String isbn, String titulo, String genero, LocalDate fechaPublicacion) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.genero = genero;
        this.fechaPublicacion = fechaPublicacion;
    }

    /**
     * Obtiene el identificador del libro.
     *
     * @return identificador del libro
     */
    public int getIdLibro() {
        return idLibro;
    }

    /**
     * Establece el identificador del libro.
     *
     * @param idLibro nuevo identificador
     */
    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    /**
     * Obtiene el ISBN del libro.
     *
     * @return ISBN del libro
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Establece el ISBN del libro.
     *
     * @param isbn nuevo ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Obtiene el título del libro.
     *
     * @return título del libro
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título del libro.
     *
     * @param titulo nuevo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el género literario del libro.
     *
     * @return género literario
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Establece el género literario del libro.
     *
     * @param genero nuevo género
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Obtiene la fecha de publicación del libro.
     *
     * @return fecha de publicación
     */
    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    /**
     * Establece la fecha de publicación del libro.
     *
     * @param fechaPublicacion nueva fecha de publicación
     */
    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    /**
     * Obtiene los escritores asociados al libro.
     *
     * @return lista de escritores
     */
    public List<Escritor> getEscritores() {
        return escritores;
    }

    /**
     * Establece los escritores asociados al libro.
     *
     * @param escritores nueva lista de escritores
     */
    public void setEscritores(List<Escritor> escritores) {
        this.escritores = escritores;
    }

    /**
     * Obtiene las reseñas asociadas al libro.
     *
     * @return lista de reseñas
     */
    public List<Resena> getResenas() {
        return resenas;
    }

    /**
     * Establece las reseñas asociadas al libro.
     *
     * @param resenas nueva lista de reseñas
     */
    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    /**
     * Añade una reseña al libro y establece la relación inversa en ella.
     *
     * @param resena reseña que se va a asociar
     */
    public void agregarResena(Resena resena) {
        resenas.add(resena);
        resena.setLibro(this);
    }

    /**
     * Calcula la valoración media de las reseñas asociadas.
     *
     * @return valoración media, o {@code 0.0} si el libro no tiene reseñas
     */
    public double getValoracionMedia() {
        return resenas.stream()
                .mapToInt(Resena::getValoracion)
                .average()
                .orElse(0.0);
    }

    /**
     * Devuelve una representación textual del libro.
     *
     * @return texto con los datos bibliográficos principales
     */
    @Override
    public String toString() {
        return "Libro - id:" + idLibro
                + " - ISBN:" + isbn
                + " - titulo:" + titulo
                + " - genero:" + genero
                + " - fechaPublicacion:" + fechaPublicacion;
    }
}
