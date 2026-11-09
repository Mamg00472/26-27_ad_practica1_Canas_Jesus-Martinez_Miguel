package org.paloma.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un escritor y los libros que tiene asociados mediante autoría.
 */
public class Escritor {

    private int idEscritor;
    private String nombre;
    private String biografia;
    private List<Libro> libros = new ArrayList<>();

    public Escritor() {
    }

    public Escritor(int idEscritor, String nombre, String biografia) {
        this.idEscritor = idEscritor;
        this.nombre = nombre;
        this.biografia = biografia;
    }

    public int getIdEscritor() {
        return idEscritor;
    }

    public void setIdEscritor(int idEscritor) {
        this.idEscritor = idEscritor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    /** Registra la autoría en ambos lados (equivale a una fila en AUTORIA). */
    public void agregarLibro(Libro libro) {
        if (!libros.contains(libro)) {
            libros.add(libro);
        }
        if (!libro.getEscritores().contains(this)) {
            libro.getEscritores().add(this);
        } 
    }

    @Override
    public String toString() {
        return "Escritor - id :" + idEscritor
                + " - nombre: '" + nombre
                + " - biografia:'" + biografia;
    }
}
