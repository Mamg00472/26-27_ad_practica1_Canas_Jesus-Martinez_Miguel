package org.paloma.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.persistencia.AlmacenArchivos;

/**
 * Repositorio para consultar y gestionar usuarios persistidos en
 * {@code data/usuarios.dat}.
 */
public class UsuarioRepositorio implements IRepositorioExtend<Usuario, Integer> {

    /** Nombre del archivo usado para almacenar los usuarios. */
    private static final String ARCHIVO = "usuarios.dat";

    /**
     * Cuenta los usuarios almacenados.
     *
     * @return cantidad de usuarios
     */
    @Override
    public long count() {
        return registros().size();
    }

    /**
     * Elimina el usuario indicado y las reseñas que haya escrito.
     *
     * @param id identificador del usuario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public void deleteById(Integer id) {
        validarId(id);
        new ResenaRepositorio().eliminarPorUsuario(id);
        List<UsuarioDato> usuarios = registros();
        for (int i = usuarios.size() - 1; i >= 0; i--) {
            if (usuarios.get(i).id() == id) {
                usuarios.remove(i);
            }
        }
        guardar(usuarios);
    }

    /**
     * Elimina todos los usuarios y todas las reseñas asociadas.
     */
    @Override
    public void deleteAll() {
        new ResenaRepositorio().deleteAll();
        guardar(new ArrayList<>());
    }

    /**
     * Comprueba si existe un usuario con el identificador indicado.
     *
     * @param id identificador del usuario
     * @return {@code true} si existe; {@code false} en caso contrario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public boolean existsById(Integer id) {
        validarId(id);
        for (UsuarioDato usuario : registros()) {
            if (usuario.id() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado o {@code null} si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Usuario findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return un {@link Optional} con el usuario si existe, o vacío si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    @Override
    public Optional<Usuario> findByIdOptional(Integer id) {
        validarId(id);
        for (UsuarioDato usuario : registros()) {
            if (usuario.id() == id) {
                return Optional.of(mapear(usuario));
            }
        }
        return Optional.empty();
    }

    /**
     * Obtiene todos los usuarios almacenados como una lista.
     *
     * @return lista de usuarios
     */
    @Override
    public List<Usuario> findAllToList() {
        List<Usuario> resultado = new ArrayList<>();
        for (UsuarioDato usuario : registros()) {
            resultado.add(mapear(usuario));
        }
        return resultado;
    }

    /**
     * Obtiene todos los usuarios almacenados.
     *
     * @return iterable con todos los usuarios
     */
    @Override
    public Iterable<Usuario> findAll() {
        return findAllToList();
    }

    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     *
     * @param <S> tipo concreto de usuario
     * @param entity usuario que se desea guardar
     * @return la misma entidad guardada, con su identificador y fecha de
     *         inscripción asignados cuando corresponda
     * @throws IllegalArgumentException si la entidad no es válida, el email ya
     *                                  está registrado o se intenta actualizar
     *                                  un usuario inexistente
     */
    @Override
    public <S extends Usuario> S save(S entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        if (entity.getNombre() == null || entity.getEmail() == null) {
            throw new IllegalArgumentException("El nombre y el email no pueden ser nulos");
        }

        List<UsuarioDato> usuarios = registros();
        boolean esNuevo = entity.getIdUsuario() == 0;
        boolean existe = false;
        boolean emailRepetido = false;
        for (UsuarioDato usuario : usuarios) {
            if (usuario.id() == entity.getIdUsuario()) {
                existe = true;
            }
            if (usuario.id() != entity.getIdUsuario()
                    && usuario.email().equalsIgnoreCase(entity.getEmail())) {
                emailRepetido = true;
            }
        }
        if (!esNuevo && !existe) {
            throw new IllegalArgumentException("No existe ningún usuario con id " + entity.getIdUsuario());
        }
        if (emailRepetido) {
            throw new IllegalArgumentException("Ya existe un usuario con email " + entity.getEmail());
        }

        if (esNuevo) {
            entity.setIdUsuario(siguienteId(usuarios));
        }
        LocalDate fecha = entity.getFechaInscripcion() != null ? entity.getFechaInscripcion() : LocalDate.now();
        entity.setFechaInscripcion(fecha);

        UsuarioDato dato = new UsuarioDato(entity.getIdUsuario(), entity.getNombre(), entity.getEmail(), fecha);
        reemplazar(usuarios, dato);
        guardar(usuarios);
        return entity;
    }

    /**
     * Busca un usuario por su email, sin distinguir mayúsculas y minúsculas.
     *
     * @param email email que se desea buscar
     * @return un {@link Optional} con el usuario si existe, o vacío si no existe
     * @throws IllegalArgumentException si el email es {@code null}
     */
    public Optional<Usuario> findByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo");
        }
        for (UsuarioDato usuario : registros()) {
            if (usuario.email().equalsIgnoreCase(email)) {
                return Optional.of(mapear(usuario));
            }
        }
        return Optional.empty();
    }

    /**
     * Busca los usuarios inscritos en la fecha indicada o posteriormente.
     *
     * @param fecha fecha mínima de inscripción
     * @return lista de usuarios que cumplen el criterio
     * @throws IllegalArgumentException si la fecha es {@code null}
     */
    public List<Usuario> findByInscritoDesde(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        List<Usuario> resultado = new ArrayList<>();
        for (UsuarioDato usuario : registros()) {
            if (usuario.fechaInscripcion() != null && !usuario.fechaInscripcion().isBefore(fecha)) {
                resultado.add(mapear(usuario));
            }
        }
        return resultado;
    }

    /**
     * Busca un usuario sin cargar sus reseñas asociadas.
     *
     * @param id identificador del usuario
     * @return usuario encontrado o {@code null} si no existe
     */
    Usuario findSimpleById(int id) {
        for (UsuarioDato usuario : registros()) {
            if (usuario.id() == id) {
                return new Usuario(usuario.id(), usuario.nombre(), usuario.email(),
                        usuario.fechaInscripcion());
            }
        }
        return null;
    }

    /**
     * Convierte los datos almacenados en un usuario con sus reseñas.
     *
     * @param dato datos persistidos del usuario
     * @return usuario reconstruido
     */
    private Usuario mapear(UsuarioDato dato) {
        Usuario usuario = new Usuario(dato.id(), dato.nombre(), dato.email(), dato.fechaInscripcion());
        for (Resena resena : new ResenaRepositorio().findByUsuario(dato.id())) {
            usuario.agregarResena(resena);
        }
        return usuario;
    }

    /**
     * Lee del almacenamiento los datos de los usuarios.
     *
     * @return lista de datos de usuarios almacenados
     */
    private List<UsuarioDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, UsuarioDato.class);
    }

    /**
     * Persiste los datos de los usuarios en el archivo configurado.
     *
     * @param usuarios datos de usuarios que se van a guardar
     */
    private void guardar(List<UsuarioDato> usuarios) {
        AlmacenArchivos.guardarLista(ARCHIVO, usuarios);
    }

    /**
     * Calcula el siguiente identificador disponible.
     *
     * @param usuarios usuarios cuyos identificadores se deben considerar
     * @return identificador mayor existente más uno
     */
    private int siguienteId(List<UsuarioDato> usuarios) {
        int mayorId = 0;
        for (UsuarioDato usuario : usuarios) {
            if (usuario.id() > mayorId) {
                mayorId = usuario.id();
            }
        }
        return mayorId + 1;
    }

    /**
     * Sustituye los datos de un usuario por los nuevos datos, o los añade si
     * todavía no estaban presentes.
     *
     * @param usuarios lista de datos que se actualizará
     * @param nuevo datos del usuario que se va a insertar o reemplazar
     */
    private void reemplazar(List<UsuarioDato> usuarios, UsuarioDato nuevo) {
        for (int i = usuarios.size() - 1; i >= 0; i--) {
            if (usuarios.get(i).id() == nuevo.id()) {
                usuarios.remove(i);
            }
        }
        usuarios.add(nuevo);
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
     * Representación serializable de un usuario y sus datos persistidos.
     *
     * @param id identificador del usuario
     * @param nombre nombre del usuario
     * @param email email del usuario
     * @param fechaInscripcion fecha de inscripción
     */
    private record UsuarioDato(int id, String nombre, String email, LocalDate fechaInscripcion)
            implements java.io.Serializable {
    }
}
