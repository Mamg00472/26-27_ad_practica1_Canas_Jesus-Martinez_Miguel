package org.paloma.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.paloma.modelo.Libro;
import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.persistencia.AlmacenArchivos;

/**
 * Repositorio para consultar y gestionar reseñas persistidas en
 * {@code data/resenas.dat}.
 */
public class ResenaRepositorio implements IRepositorioExtend<Resena, Integer> {

    /** Nombre del archivo usado para almacenar las reseñas. */
    private static final String ARCHIVO = "resenas.dat";

    /**
     * Cuenta las reseñas almacenadas.
     *
     * @return cantidad de reseñas
     */
    @Override
    public long count() {
        return registros().size();
    }

    /**
     * Elimina todas las reseñas que tengan el identificador indicado.
     *
     * @param id identificador de la reseña
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public void deleteById(Integer id) {
        validarId(id);
        List<ResenaDato> resenas = registros();
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).id() == id) {
                resenas.remove(i);
            }
        }
        guardar(resenas);
    }

    /** Elimina todas las reseñas almacenadas. */
    @Override
    public void deleteAll() {
        guardar(new ArrayList<>());
    }

    /**
     * Comprueba si existe una reseña con el identificador indicado.
     *
     * @param id identificador de la reseña
     * @return {@code true} si existe una reseña con ese identificador;
     *         {@code false} en caso contrario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public boolean existsById(Integer id) {
        validarId(id);
        for (ResenaDato resena : registros()) {
            if (resena.id() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca una reseña por su identificador.
     *
     * @param id identificador de la reseña
     * @return la reseña encontrada o {@code null} si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Resena findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

    /**
     * Busca una reseña por su identificador.
     *
     * @param id identificador de la reseña
     * @return un {@link Optional} con la reseña si existe, o vacío si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Optional<Resena> findByIdOptional(Integer id) {
        validarId(id);
        for (ResenaDato resena : registros()) {
            if (resena.id() == id) {
                return Optional.of(mapear(resena));
            }
        }
        return Optional.empty();
    }

    /**
     * Obtiene todas las reseñas almacenadas como una lista.
     *
     * @return lista de reseñas
     */
    @Override
    public List<Resena> findAllToList() {
        List<Resena> resultado = new ArrayList<>();
        for (ResenaDato resena : registros()) {
            resultado.add(mapear(resena));
        }
        return resultado;
    }

    /**
     * Obtiene todas las reseñas almacenadas.
     *
     * @return iterable con todas las reseñas
     */
    @Override
    public Iterable<Resena> findAll() {
        return findAllToList();
    }

    /**
     * Guarda una reseña nueva o actualiza una reseña existente.
     *
     * @param <S> tipo concreto de reseña
     * @param entity reseña que se desea guardar
     * @return la misma entidad guardada, con su identificador asignado si era nueva
     * @throws IllegalArgumentException si la entidad, sus referencias o su
     *                                  valoración no son válidas, o si se intenta
     *                                  actualizar una reseña inexistente
     */
    @Override
    public <S extends Resena> S save(S entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        if (entity.getUsuario() == null || entity.getLibro() == null) {
            throw new IllegalArgumentException("La reseña necesita usuario y libro");
        }
        if (entity.getValoracion() < 1 || entity.getValoracion() > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5");
        }
        if (!new UsuarioRepositorio().existsById(entity.getUsuario().getIdUsuario())) {
            throw new IllegalArgumentException("El usuario de la reseña debe guardarse antes que la reseña");
        }
        if (!new LibroRepositorio().existsById(entity.getLibro().getIdLibro())) {
            throw new IllegalArgumentException("El libro de la reseña debe guardarse antes que la reseña");
        }

        List<ResenaDato> resenas = registros();
        boolean esNueva = entity.getIdResena() == 0;
        boolean existe = false;
        int mayorId = 0;
        for (ResenaDato resena : resenas) {
            if (resena.id() == entity.getIdResena()) {
                existe = true;
            }
            if (resena.id() > mayorId) {
                mayorId = resena.id();
            }
        }
        if (!esNueva && !existe) {
            throw new IllegalArgumentException("No existe ninguna reseña con id " + entity.getIdResena());
        }
        if (esNueva) {
            entity.setIdResena(mayorId + 1);
        }

        ResenaDato dato = new ResenaDato(entity.getIdResena(), entity.getComentarios(), entity.getValoracion(),
                entity.getUsuario().getIdUsuario(), entity.getLibro().getIdLibro());
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).id() == dato.id()) {
                resenas.remove(i);
            }
        }
        resenas.add(dato);
        guardar(resenas);
        return entity;
    }

    /**
     * Busca las reseñas asociadas a un libro.
     *
     * @param idLibro identificador del libro
     * @return lista de reseñas asociadas al libro
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    public List<Resena> findByLibro(Integer idLibro) {
        validarId(idLibro);
        List<Resena> resultado = new ArrayList<>();
        for (ResenaDato resena : registros()) {
            if (resena.idLibro() == idLibro) {
                resultado.add(mapear(resena));
            }
        }
        return resultado;
    }

    /**
     * Busca las reseñas escritas por un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de reseñas escritas por el usuario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    public List<Resena> findByUsuario(Integer idUsuario) {
        validarId(idUsuario);
        List<Resena> resultado = new ArrayList<>();
        for (ResenaDato resena : registros()) {
            if (resena.idUsuario() == idUsuario) {
                resultado.add(mapear(resena));
            }
        }
        return resultado;
    }

    /**
     * Busca las reseñas cuya valoración sea igual o superior a la indicada.
     *
     * @param valoracion valoración mínima, entre 1 y 5
     * @return lista de reseñas que cumplen el criterio
     * @throws IllegalArgumentException si la valoración no está entre 1 y 5
     */
    public List<Resena> findByValoracionMinima(int valoracion) {
        if (valoracion < 1 || valoracion > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5");
        }
        List<Resena> resultado = new ArrayList<>();
        for (ResenaDato resena : registros()) {
            if (resena.valoracion() >= valoracion) {
                resultado.add(mapear(resena));
            }
        }
        return resultado;
    }

    /**
     * Elimina todas las reseñas escritas por el usuario indicado.
     *
     * @param idUsuario identificador del usuario
     */
    void eliminarPorUsuario(int idUsuario) {
        List<ResenaDato> resenas = registros();
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).idUsuario() == idUsuario) {
                resenas.remove(i);
            }
        }
        guardar(resenas);
    }

    /**
     * Elimina todas las reseñas asociadas al libro indicado.
     *
     * @param idLibro identificador del libro
     */
    void eliminarPorLibro(int idLibro) {
        List<ResenaDato> resenas = registros();
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).idLibro() == idLibro) {
                resenas.remove(i);
            }
        }
        guardar(resenas);
    }

    /**
     * Convierte los datos almacenados en una reseña con sus entidades asociadas.
     *
     * @param dato datos persistidos de la reseña
     * @return reseña reconstruida con su usuario y libro
     * @throws IllegalStateException si el usuario o el libro referenciado no existe
     */
    private Resena mapear(ResenaDato dato) {
        Usuario usuario = new UsuarioRepositorio().findSimpleById(dato.idUsuario());
        Libro libro = new LibroRepositorio().findSimpleById(dato.idLibro());
        if (usuario == null || libro == null) {
            throw new IllegalStateException("La reseña " + dato.id() + " referencia un usuario o libro inexistente");
        }
        return new Resena(dato.id(), dato.comentarios(), dato.valoracion(), usuario, libro);
    }

    /**
     * Lee del almacenamiento los datos de las reseñas.
     *
     * @return lista de datos de reseñas almacenadas
     */
    private List<ResenaDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, ResenaDato.class);
    }

    /**
     * Persiste los datos de las reseñas en el archivo configurado.
     *
     * @param resenas datos de reseñas que se van a guardar
     */
    private void guardar(List<ResenaDato> resenas) {
        AlmacenArchivos.guardarLista(ARCHIVO, resenas);
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
     * Representación serializable de una reseña y sus referencias por
     * identificador.
     *
     * @param id identificador de la reseña
     * @param comentarios texto de la reseña
     * @param valoracion valoración otorgada
     * @param idUsuario identificador del usuario autor
     * @param idLibro identificador del libro reseñado
     */
    private record ResenaDato(int id, String comentarios, int valoracion, int idUsuario, int idLibro)
            implements java.io.Serializable {
    }
}
