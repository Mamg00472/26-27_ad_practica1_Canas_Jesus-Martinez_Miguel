package org.paloma.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.paloma.modelo.Escritor;
import org.paloma.modelo.Libro;
import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.persistencia.AlmacenArchivos;

/**
 * Repositorio para consultar y gestionar libros persistidos en
 * {@code data/libros.dat}.
 */
public class LibroRepositorio implements IRepositorioExtend<Libro, Integer> {

    /** Nombre del archivo usado para almacenar los libros. */
    private static final String ARCHIVO = "libros.dat";

    /**
     * Cuenta los libros almacenados.
     *
     * @return cantidad de libros
     */
    @Override
    public long count() {
        return registros().size();
    }

    /**
     * Elimina el libro indicado y las reseñas asociadas.
     *
     * @param id identificador del libro
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public void deleteById(Integer id) {
        validarId(id);
        new ResenaRepositorio().eliminarPorLibro(id);
        List<LibroDato> libros = registros();
        for (int i = libros.size() - 1; i >= 0; i--) {
            if (libros.get(i).id() == id) {
                libros.remove(i);
            }
        }
        guardar(libros);
    }

    /** Elimina todos los libros y las reseñas asociadas a ellos. */
    @Override
    public void deleteAll() {
        new ResenaRepositorio().deleteAll();
        guardar(new ArrayList<>());
    }

    /**
     * Comprueba si existe un libro con el identificador indicado.
     *
     * @param id identificador del libro
     * @return {@code true} si existe; {@code false} en caso contrario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public boolean existsById(Integer id) {
        validarId(id);
        for (LibroDato libro : registros()) {
            if (libro.id() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca un libro por su identificador.
     *
     * @param id identificador del libro
     * @return el libro encontrado o {@code null} si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Libro findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

    /**
     * Busca un libro por su identificador.
     *
     * @param id identificador del libro
     * @return un {@link Optional} con el libro si existe, o vacío si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Optional<Libro> findByIdOptional(Integer id) {
        validarId(id);
        for (LibroDato libro : registros()) {
            if (libro.id() == id) {
                return Optional.of(mapear(libro));
            }
        }
        return Optional.empty();
    }

    /**
     * Obtiene todos los libros almacenados como una lista.
     *
     * @return lista de libros
     */
    @Override
    public List<Libro> findAllToList() {
        List<Libro> resultado = new ArrayList<>();
        for (LibroDato libro : registros()) {
            resultado.add(mapear(libro));
        }
        return resultado;
    }

    /**
     * Obtiene todos los libros almacenados.
     *
     * @return iterable con todos los libros
     */
    @Override
    public Iterable<Libro> findAll() {
        return findAllToList();
    }

    /**
     * Guarda un libro nuevo o actualiza uno existente.
     *
     * @param <S> tipo concreto de libro
     * @param entity libro que se desea guardar
     * @return la misma entidad guardada, con su identificador asignado si era nueva
     * @throws IllegalArgumentException si la entidad no es válida, el ISBN está
     *                                  duplicado, algún escritor no está
     *                                  almacenado o se intenta actualizar un
     *                                  libro inexistente
     */
    @Override
    public <S extends Libro> S save(S entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        if (entity.getIsbn() == null || entity.getTitulo() == null) {
            throw new IllegalArgumentException("El ISBN y el título no pueden ser nulos");
        }
        if (entity.getEscritores() == null) {
            throw new IllegalArgumentException("La lista de escritores no puede ser nula");
        }

        List<LibroDato> libros = registros();
        boolean esNuevo = entity.getIdLibro() == 0;
        boolean existe = false;
        boolean isbnRepetido = false;
        for (LibroDato libro : libros) {
            if (libro.id() == entity.getIdLibro()) {
                existe = true;
            }
            if (libro.id() != entity.getIdLibro()
                    && libro.isbn().equalsIgnoreCase(entity.getIsbn())) {
                isbnRepetido = true;
            }
        }
        if (!esNuevo && !existe) {
            throw new IllegalArgumentException("No existe ningún libro con id " + entity.getIdLibro());
        }
        if (isbnRepetido) {
            throw new IllegalArgumentException("Ya existe un libro con ISBN " + entity.getIsbn());
        }

        List<Integer> idsEscritores = new ArrayList<>();
        EscritorRepositorio escritores = new EscritorRepositorio();
        for (Escritor escritor : entity.getEscritores()) {
            if (escritor.getIdEscritor() == 0 || !escritores.existsById(escritor.getIdEscritor())) {
                throw new IllegalArgumentException("El escritor '" + escritor.getNombre()
                        + "' debe guardarse antes de asignarlo a un libro");
            }
            if (idsEscritores.contains(escritor.getIdEscritor())) {
                throw new IllegalArgumentException("No se puede asignar dos veces el mismo escritor a un libro");
            }
            idsEscritores.add(escritor.getIdEscritor());
        }

        if (esNuevo) {
            int mayorId = 0;
            for (LibroDato libro : libros) {
                if (libro.id() > mayorId) {
                    mayorId = libro.id();
                }
            }
            entity.setIdLibro(mayorId + 1);
        }
        LibroDato dato = new LibroDato(entity.getIdLibro(), entity.getIsbn(), entity.getTitulo(),
                entity.getGenero(), entity.getFechaPublicacion(), idsEscritores);
        reemplazar(libros, dato);
        guardar(libros);
        return entity;
    }

    /**
     * Busca un libro por su ISBN, sin distinguir mayúsculas y minúsculas.
     *
     * @param isbn ISBN que se desea buscar
     * @return un {@link Optional} con el libro si existe, o vacío si no existe
     * @throws IllegalArgumentException si el ISBN es {@code null}
     */
    public Optional<Libro> findByIsbn(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("El ISBN no puede ser nulo");
        }
        for (LibroDato libro : registros()) {
            if (libro.isbn().equalsIgnoreCase(isbn)) {
                return Optional.of(mapear(libro));
            }
        }
        return Optional.empty();
    }

    /**
     * Busca los libros de un género, sin distinguir mayúsculas y minúsculas.
     *
     * @param genero género que se desea buscar
     * @return lista de libros del género indicado
     * @throws IllegalArgumentException si el género es {@code null}
     */
    public List<Libro> findByGenero(String genero) {
        if (genero == null) {
            throw new IllegalArgumentException("El género no puede ser nulo");
        }
        List<Libro> resultado = new ArrayList<>();
        for (LibroDato libro : registros()) {
            if (genero.equalsIgnoreCase(libro.genero())) {
                resultado.add(mapear(libro));
            }
        }
        return resultado;
    }

    /**
     * Busca los libros cuyo título contiene el texto indicado, sin distinguir
     * mayúsculas y minúsculas.
     *
     * @param texto texto que debe aparecer en el título
     * @return lista de libros que cumplen el criterio
     * @throws IllegalArgumentException si el texto es {@code null}
     */
    public List<Libro> findByTituloContaining(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto no puede ser nulo");
        }
        String buscado = texto.toLowerCase(Locale.ROOT);
        List<Libro> resultado = new ArrayList<>();
        for (LibroDato libro : registros()) {
            if (libro.titulo().toLowerCase(Locale.ROOT).contains(buscado)) {
                resultado.add(mapear(libro));
            }
        }
        return resultado;
    }

    /**
     * Busca los libros asociados al escritor indicado.
     *
     * @param idEscritor identificador del escritor
     * @return lista de libros asociados al escritor
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    public List<Libro> findByEscritor(Integer idEscritor) {
        validarId(idEscritor);
        List<Libro> resultado = new ArrayList<>();
        for (LibroDato libro : registros()) {
            if (libro.idsEscritores().contains(idEscritor)) {
                resultado.add(mapear(libro));
            }
        }
        return resultado;
    }

    /**
     * Obtiene los escritores asociados a un libro.
     *
     * @param idLibro identificador del libro
     * @return lista de escritores del libro, vacía si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    List<Escritor> findEscritores(Integer idLibro) {
        validarId(idLibro);
        for (LibroDato libro : registros()) {
            if (libro.id() == idLibro) {
                return new EscritorRepositorio().findByIds(libro.idsEscritores());
            }
        }
        return new ArrayList<>();
    }

    /**
     * Elimina de todos los libros la asociación con el escritor indicado.
     *
     * @param idEscritor identificador del escritor
     */
    void quitarEscritor(int idEscritor) {
        List<LibroDato> libros = registros();
        List<LibroDato> actualizados = new ArrayList<>();
        for (LibroDato libro : libros) {
            List<Integer> ids = new ArrayList<>(libro.idsEscritores());
            for (int i = ids.size() - 1; i >= 0; i--) {
                if (ids.get(i) == idEscritor) {
                    ids.remove(i);
                }
            }
            actualizados.add(new LibroDato(libro.id(), libro.isbn(), libro.titulo(), libro.genero(),
                    libro.fechaPublicacion(), ids));
        }
        guardar(actualizados);
    }

    /** Elimina de todos los libros sus asociaciones con escritores. */
    void limpiarEscritores() {
        List<LibroDato> libros = registros();
        List<LibroDato> actualizados = new ArrayList<>();
        for (LibroDato libro : libros) {
            actualizados.add(new LibroDato(libro.id(), libro.isbn(), libro.titulo(), libro.genero(),
                    libro.fechaPublicacion(), new ArrayList<>()));
        }
        guardar(actualizados);
    }

    /**
     * Busca un libro sin cargar sus relaciones con escritores y reseñas.
     *
     * @param id identificador del libro
     * @return libro encontrado o {@code null} si no existe
     */
    Libro findSimpleById(int id) {
        for (LibroDato libro : registros()) {
            if (libro.id() == id) {
                return new Libro(libro.id(), libro.isbn(), libro.titulo(), libro.genero(),
                        libro.fechaPublicacion());
            }
        }
        return null;
    }

    /**
     * Convierte los datos almacenados en un libro con sus escritores y reseñas.
     *
     * @param dato datos persistidos del libro
     * @return libro reconstruido con sus relaciones
     */
    private Libro mapear(LibroDato dato) {
        Libro libro = new Libro(dato.id(), dato.isbn(), dato.titulo(), dato.genero(), dato.fechaPublicacion());
        libro.getEscritores().addAll(new EscritorRepositorio().findByIds(dato.idsEscritores()));
        for (Resena resena : new ResenaRepositorio().findByLibro(dato.id())) {
            libro.agregarResena(resena);
        }
        return libro;
    }

    /**
     * Lee del almacenamiento los datos de los libros.
     *
     * @return lista de datos de libros almacenados
     */
    private List<LibroDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, LibroDato.class);
    }

    /**
     * Persiste los datos de los libros en el archivo configurado.
     *
     * @param libros datos de libros que se van a guardar
     */
    private void guardar(List<LibroDato> libros) {
        AlmacenArchivos.guardarLista(ARCHIVO, libros);
    }

    /**
     * Sustituye los datos de un libro por los nuevos datos, o los añade si
     * todavía no estaban presentes.
     *
     * @param libros lista de datos que se actualizará
     * @param nuevo datos del libro que se va a insertar o reemplazar
     */
    private void reemplazar(List<LibroDato> libros, LibroDato nuevo) {
        for (int i = libros.size() - 1; i >= 0; i--) {
            if (libros.get(i).id() == nuevo.id()) {
                libros.remove(i);
            }
        }
        libros.add(nuevo);
    }

    /**
     * Comprueba que un identificador no sea nulo.
     *
     * @param id identificador que se va a validar
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    private void validarId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }

    /**
     * Representación serializable de un libro y sus referencias a escritores.
     *
     * @param id identificador del libro
     * @param isbn ISBN del libro
     * @param titulo título del libro
     * @param genero género del libro
     * @param fechaPublicacion fecha de publicación
     * @param idsEscritores identificadores de los escritores asociados
     */
    private record LibroDato(int id, String isbn, String titulo, String genero, LocalDate fechaPublicacion,
            List<Integer> idsEscritores) implements java.io.Serializable {
    }
}
