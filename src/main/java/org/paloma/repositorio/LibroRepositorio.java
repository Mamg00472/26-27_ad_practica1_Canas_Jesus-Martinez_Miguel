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

/** Repositorio de libros, guardados en {@code data/libros.dat}. */
public class LibroRepositorio implements IRepositorioExtend<Libro, Integer> {

    private static final String ARCHIVO = "libros.dat";

    @Override
    public long count() {
        return registros().size();
    }

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

    @Override
    public void deleteAll() {
        new ResenaRepositorio().deleteAll();
        guardar(new ArrayList<>());
    }

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

    @Override
    public Libro findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

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

    @Override
    public List<Libro> findAllToList() {
        List<Libro> resultado = new ArrayList<>();
        for (LibroDato libro : registros()) {
            resultado.add(mapear(libro));
        }
        return resultado;
    }

    @Override
    public Iterable<Libro> findAll() {
        return findAllToList();
    }

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

    /** Devuelve el libro con ese ISBN o {@link Optional#empty()} si no existe. */
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

    /** Devuelve los libros de un género. */
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

    /** Devuelve los libros cuyo título contiene el texto indicado, sin distinguir mayúsculas. */
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

    /** Devuelve los libros asociados a un escritor. */
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

    List<Escritor> findEscritores(Integer idLibro) {
        validarId(idLibro);
        for (LibroDato libro : registros()) {
            if (libro.id() == idLibro) {
                return new EscritorRepositorio().findByIds(libro.idsEscritores());
            }
        }
        return new ArrayList<>();
    }

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

    void limpiarEscritores() {
        List<LibroDato> libros = registros();
        List<LibroDato> actualizados = new ArrayList<>();
        for (LibroDato libro : libros) {
            actualizados.add(new LibroDato(libro.id(), libro.isbn(), libro.titulo(), libro.genero(),
                    libro.fechaPublicacion(), new ArrayList<>()));
        }
        guardar(actualizados);
    }

    Libro findSimpleById(int id) {
        for (LibroDato libro : registros()) {
            if (libro.id() == id) {
                return new Libro(libro.id(), libro.isbn(), libro.titulo(), libro.genero(),
                        libro.fechaPublicacion());
            }
        }
        return null;
    }

    private Libro mapear(LibroDato dato) {
        Libro libro = new Libro(dato.id(), dato.isbn(), dato.titulo(), dato.genero(), dato.fechaPublicacion());
        libro.getEscritores().addAll(new EscritorRepositorio().findByIds(dato.idsEscritores()));
        for (Resena resena : new ResenaRepositorio().findByLibro(dato.id())) {
            libro.agregarResena(resena);
        }
        return libro;
    }

    private List<LibroDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, LibroDato.class);
    }

    private void guardar(List<LibroDato> libros) {
        AlmacenArchivos.guardarLista(ARCHIVO, libros);
    }

    private void reemplazar(List<LibroDato> libros, LibroDato nuevo) {
        for (int i = libros.size() - 1; i >= 0; i--) {
            if (libros.get(i).id() == nuevo.id()) {
                libros.remove(i);
            }
        }
        libros.add(nuevo);
    }

    private void validarId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }

    private record LibroDato(int id, String isbn, String titulo, String genero, LocalDate fechaPublicacion,
            List<Integer> idsEscritores) implements java.io.Serializable {
    }
}
