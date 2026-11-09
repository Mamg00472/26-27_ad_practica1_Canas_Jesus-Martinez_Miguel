package org.paloma.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.paloma.modelo.Libro;
import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.persistencia.AlmacenArchivos;

/** Repositorio de reseñas, guardadas en {@code data/resenas.dat}. */
public class ResenaRepositorio implements IRepositorioExtend<Resena, Integer> {

    private static final String ARCHIVO = "resenas.dat";

    @Override
    public long count() {
        return registros().size();
    }

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

    @Override
    public void deleteAll() {
        guardar(new ArrayList<>());
    }

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

    @Override
    public Resena findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

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

    @Override
    public List<Resena> findAllToList() {
        List<Resena> resultado = new ArrayList<>();
        for (ResenaDato resena : registros()) {
            resultado.add(mapear(resena));
        }
        return resultado;
    }

    @Override
    public Iterable<Resena> findAll() {
        return findAllToList();
    }

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

    /** Devuelve las reseñas de un libro. */
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

    /** Devuelve las reseñas escritas por un usuario. */
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

    /** Devuelve las reseñas con una valoración mayor o igual que la indicada (1 a 5). */
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

    void eliminarPorUsuario(int idUsuario) {
        List<ResenaDato> resenas = registros();
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).idUsuario() == idUsuario) {
                resenas.remove(i);
            }
        }
        guardar(resenas);
    }

    void eliminarPorLibro(int idLibro) {
        List<ResenaDato> resenas = registros();
        for (int i = resenas.size() - 1; i >= 0; i--) {
            if (resenas.get(i).idLibro() == idLibro) {
                resenas.remove(i);
            }
        }
        guardar(resenas);
    }

    private Resena mapear(ResenaDato dato) {
        Usuario usuario = new UsuarioRepositorio().findSimpleById(dato.idUsuario());
        Libro libro = new LibroRepositorio().findSimpleById(dato.idLibro());
        if (usuario == null || libro == null) {
            throw new IllegalStateException("La reseña " + dato.id() + " referencia un usuario o libro inexistente");
        }
        return new Resena(dato.id(), dato.comentarios(), dato.valoracion(), usuario, libro);
    }

    private List<ResenaDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, ResenaDato.class);
    }

    private void guardar(List<ResenaDato> resenas) {
        AlmacenArchivos.guardarLista(ARCHIVO, resenas);
    }

    private void validarId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }

    private record ResenaDato(int id, String comentarios, int valoracion, int idUsuario, int idLibro)
            implements java.io.Serializable {
    }
}
