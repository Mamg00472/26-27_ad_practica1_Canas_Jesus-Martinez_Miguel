package org.paloma.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.paloma.modelo.Escritor;
import org.paloma.persistencia.AlmacenArchivos;

/**
 * Repositorio para consultar y gestionar escritores persistidos en
 * {@code data/escritores.dat}.
 */
public class EscritorRepositorio implements IRepositorioExtend<Escritor, Integer> {

    /** Nombre del archivo usado para almacenar los escritores. */
    private static final String ARCHIVO = "escritores.dat";

    /**
     * Cuenta los escritores almacenados.
     *
     * @return cantidad de escritores
     */
    @Override
    public long count() {
        return registros().size();
    }

    /**
     * Elimina el escritor indicado y quita su asociación de los libros.
     *
     * @param id identificador del escritor
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public void deleteById(Integer id) {
        validarId(id);
        new LibroRepositorio().quitarEscritor(id);
        List<EscritorDato> escritores = registros();
        for (int i = escritores.size() - 1; i >= 0; i--) {
            if (escritores.get(i).id() == id) {
                escritores.remove(i);
            }
        }
        guardar(escritores);
    }

    /** Elimina todos los escritores y sus asociaciones con libros. */
    @Override
    public void deleteAll() {
        new LibroRepositorio().limpiarEscritores();
        guardar(new ArrayList<>());
    }

    /**
     * Comprueba si existe un escritor con el identificador indicado.
     *
     * @param id identificador del escritor
     * @return {@code true} si existe; {@code false} en caso contrario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public boolean existsById(Integer id) {
        validarId(id);
        for (EscritorDato escritor : registros()) {
            if (escritor.id() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca un escritor por su identificador.
     *
     * @param id identificador del escritor
     * @return el escritor encontrado o {@code null} si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Escritor findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

    /**
     * Busca un escritor por su identificador.
     *
     * @param id identificador del escritor
     * @return un {@link Optional} con el escritor si existe, o vacío si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Optional<Escritor> findByIdOptional(Integer id) {
        validarId(id);
        for (EscritorDato escritor : registros()) {
            if (escritor.id() == id) {
                return Optional.of(mapear(escritor));
            }
        }
        return Optional.empty();
    }

    /**
     * Obtiene todos los escritores almacenados como una lista.
     *
     * @return lista de escritores
     */
    @Override
    public List<Escritor> findAllToList() {
        List<Escritor> resultado = new ArrayList<>();
        for (EscritorDato escritor : registros()) {
            resultado.add(mapear(escritor));
        }
        return resultado;
    }

    /**
     * Obtiene todos los escritores almacenados.
     *
     * @return iterable con todos los escritores
     */
    @Override
    public Iterable<Escritor> findAll() {
        return findAllToList();
    }

    /**
     * Guarda un escritor nuevo o actualiza uno existente.
     *
     * @param <S> tipo concreto de escritor
     * @param entity escritor que se desea guardar
     * @return la misma entidad guardada, con su identificador asignado si era nuevo
     * @throws IllegalArgumentException si la entidad no es válida o se intenta
     *                                  actualizar un escritor inexistente
     */
    @Override
    public <S extends Escritor> S save(S entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        if (entity.getNombre() == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }

        List<EscritorDato> escritores = registros();
        boolean esNuevo = entity.getIdEscritor() == 0;
        boolean existe = false;
        for (EscritorDato escritor : escritores) {
            if (escritor.id() == entity.getIdEscritor()) {
                existe = true;
                break;
            }
        }
        if (!esNuevo && !existe) {
            throw new IllegalArgumentException("No existe ningún escritor con id " + entity.getIdEscritor());
        }
        if (esNuevo) {
            int mayorId = 0;
            for (EscritorDato escritor : escritores) {
                if (escritor.id() > mayorId) {
                    mayorId = escritor.id();
                }
            }
            entity.setIdEscritor(mayorId + 1);
        }

        EscritorDato dato = new EscritorDato(entity.getIdEscritor(), entity.getNombre(), entity.getBiografia());
        for (int i = escritores.size() - 1; i >= 0; i--) {
            if (escritores.get(i).id() == dato.id()) {
                escritores.remove(i);
            }
        }
        escritores.add(dato);
        guardar(escritores);
        return entity;
    }

    /**
     * Busca los escritores cuyo nombre contiene el texto indicado, sin
     * distinguir mayúsculas y minúsculas.
     *
     * @param texto texto que debe aparecer en el nombre
     * @return lista de escritores que cumplen el criterio
     * @throws IllegalArgumentException si el texto es {@code null}
     */
    public List<Escritor> findByNombreContaining(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto no puede ser nulo");
        }
        List<Escritor> resultado = new ArrayList<>();
        String buscado = texto.toLowerCase(Locale.ROOT);
        for (EscritorDato escritor : registros()) {
            if (escritor.nombre().toLowerCase(Locale.ROOT).contains(buscado)) {
                resultado.add(mapear(escritor));
            }
        }
        return resultado;
    }

    /**
     * Busca los escritores asociados a un libro.
     *
     * @param idLibro identificador del libro
     * @return lista de escritores asociados al libro
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    public List<Escritor> findByLibro(Integer idLibro) {
        validarId(idLibro);
        return new LibroRepositorio().findEscritores(idLibro);
    }

    /**
     * Busca escritores por sus identificadores sin cargar sus libros asociados.
     *
     * @param ids identificadores de los escritores que se desean buscar
     * @return escritores encontrados, en el orden de almacenamiento
     */
    List<Escritor> findByIds(List<Integer> ids) {
        List<Escritor> resultado = new ArrayList<>();
        for (EscritorDato escritor : registros()) {
            if (ids.contains(escritor.id())) {
                resultado.add(new Escritor(escritor.id(), escritor.nombre(), escritor.biografia()));
            }
        }
        return resultado;
    }

    /**
     * Convierte los datos almacenados en un escritor con sus libros asociados.
     *
     * @param dato datos persistidos del escritor
     * @return escritor reconstruido con sus libros
     */
    private Escritor mapear(EscritorDato dato) {
        Escritor escritor = new Escritor(dato.id(), dato.nombre(), dato.biografia());
        escritor.getLibros().addAll(new LibroRepositorio().findByEscritor(dato.id()));
        return escritor;
    }

    /**
     * Lee del almacenamiento los datos de los escritores.
     *
     * @return lista de datos de escritores almacenados
     */
    private List<EscritorDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, EscritorDato.class);
    }

    /**
     * Persiste los datos de los escritores en el archivo configurado.
     *
     * @param escritores datos de escritores que se van a guardar
     */
    private void guardar(List<EscritorDato> escritores) {
        AlmacenArchivos.guardarLista(ARCHIVO, escritores);
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
     * Representación serializable de un escritor y sus datos persistidos.
     *
     * @param id identificador del escritor
     * @param nombre nombre del escritor
     * @param biografia biografía del escritor
     */
    private record EscritorDato(int id, String nombre, String biografia) implements java.io.Serializable {
    }
}
