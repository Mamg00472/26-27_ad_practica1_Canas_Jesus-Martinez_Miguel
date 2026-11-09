package org.paloma.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.paloma.modelo.Escritor;
import org.paloma.persistencia.AlmacenArchivos;

/** Repositorio de escritores, guardados en {@code data/escritores.dat}. */
public class EscritorRepositorio implements IRepositorioExtend<Escritor, Integer> {

    private static final String ARCHIVO = "escritores.dat";

    @Override
    public long count() {
        return registros().size();
    }

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

    @Override
    public void deleteAll() {
        new LibroRepositorio().limpiarEscritores();
        guardar(new ArrayList<>());
    }

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

    @Override
    public Escritor findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

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

    @Override
    public List<Escritor> findAllToList() {
        List<Escritor> resultado = new ArrayList<>();
        for (EscritorDato escritor : registros()) {
            resultado.add(mapear(escritor));
        }
        return resultado;
    }

    @Override
    public Iterable<Escritor> findAll() {
        return findAllToList();
    }

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

    /** Devuelve los escritores cuyo nombre contiene el texto indicado. */
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

    /** Devuelve los escritores asociados a un libro. */
    public List<Escritor> findByLibro(Integer idLibro) {
        validarId(idLibro);
        return new LibroRepositorio().findEscritores(idLibro);
    }

    List<Escritor> findByIds(List<Integer> ids) {
        List<Escritor> resultado = new ArrayList<>();
        for (EscritorDato escritor : registros()) {
            if (ids.contains(escritor.id())) {
                resultado.add(new Escritor(escritor.id(), escritor.nombre(), escritor.biografia()));
            }
        }
        return resultado;
    }

    private Escritor mapear(EscritorDato dato) {
        Escritor escritor = new Escritor(dato.id(), dato.nombre(), dato.biografia());
        escritor.getLibros().addAll(new LibroRepositorio().findByEscritor(dato.id()));
        return escritor;
    }

    private List<EscritorDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, EscritorDato.class);
    }

    private void guardar(List<EscritorDato> escritores) {
        AlmacenArchivos.guardarLista(ARCHIVO, escritores);
    }

    private void validarId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }

    private record EscritorDato(int id, String nombre, String biografia) implements java.io.Serializable {
    }
}
