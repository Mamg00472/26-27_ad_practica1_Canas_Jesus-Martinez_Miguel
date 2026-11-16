package org.paloma.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un escritor y los libros que tiene asociados mediante autoría.
 */
public class Escritor {

    /** Identificador único del escritor. */
    private int idEscritor;
    /** Nombre del escritor. */
    private String nombre;
    /** Biografía del escritor. */
    private String biografia;
    /** Libros asociados al escritor. */
    private List<Libro> libros = new ArrayList<>();

    /** Crea un escritor sin inicializar sus datos. */
    public Escritor() {
    }

    /**
     * Crea un escritor con los datos indicados.
     *
     * @param idEscritor identificador del escritor
     * @param nombre nombre del escritor
     * @param biografia biografía del escritor
     */
    public Escritor(int idEscritor, String nombre, String biografia) {
        this.idEscritor = idEscritor;
        this.nombre = nombre;
        this.biografia = biografia;
    }

    /**
     * Obtiene el identificador del escritor.
     *
     * @return identificador del escritor
     */
    public int getIdEscritor() {
        return idEscritor;
    }

    /**
     * Establece el identificador del escritor.
     *
     * @param idEscritor nuevo identificador
     */
    public void setIdEscritor(int idEscritor) {
        this.idEscritor = idEscritor;
    }

    /**
     * Obtiene el nombre del escritor.
     *
     * @return nombre del escritor
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del escritor.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la biografía del escritor.
     *
     * @return biografía del escritor
     */
    public String getBiografia() {
        return biografia;
    }

    /**
     * Establece la biografía del escritor.
     *
     * @param biografia nueva biografía
     */
    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    /**
     * Obtiene los libros asociados al escritor.
     *
     * @return lista de libros
     */
    public List<Libro> getLibros() {
        return libros;
    }

    /**
     * Establece los libros asociados al escritor.
     *
     * @param libros nueva lista de libros
     */
    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    /**
     * Asocia un libro al escritor y mantiene la relación en ambos lados.
     *
     * @param libro libro cuya autoría se va a registrar
     */
    public void agregarLibro(Libro libro) {
        if (!libros.contains(libro)) {
            libros.add(libro);
        }
        if (!libro.getEscritores().contains(this)) {
            libro.getEscritores().add(this);
        } 
    }

    /**
     * Devuelve una representación textual del escritor.
     *
     * @return texto con los datos principales del escritor
     */
    @Override
    public String toString() {
        return "Escritor - id :" + idEscritor
                + " - nombre: '" + nombre
                + " - biografia:'" + biografia;
    }
}
