package org.paloma.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un libro de la biblioteca, sus escritores y las reseñas asociadas.
 */
public class Libro {

    private int idLibro;
    private String isbn;
    private String titulo;
    private String genero;
    private LocalDate fechaPublicacion;
    private List<Escritor> escritores = new ArrayList<>();
    private List<Resena> resenas = new ArrayList<>();

    public Libro() {
    }

    public Libro(int idLibro, String isbn, String titulo, String genero, LocalDate fechaPublicacion) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.genero = genero;
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public List<Escritor> getEscritores() {
        return escritores;
    }

    public void setEscritores(List<Escritor> escritores) {
        this.escritores = escritores;
    }

    public List<Resena> getResenas() {
        return resenas;
    }

    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    /** Añade una reseña a este libro y mantiene la relación bidireccional. */
    public void agregarResena(Resena resena) {
        resenas.add(resena);
        resena.setLibro(this);
    }

    /** Valoración media de las reseñas (0 si no tiene ninguna). */
    public double getValoracionMedia() {
        return resenas.stream()
                .mapToInt(Resena::getValoracion)
                .average()
                .orElse(0.0);
    }

    @Override
    public String toString() {
        return "Libro - id:" + idLibro
                + " - ISBN:" + isbn
                + " - titulo:" + titulo
                + " - genero:" + genero
                + " - fechaPublicacion:" + fechaPublicacion;
    }
}
