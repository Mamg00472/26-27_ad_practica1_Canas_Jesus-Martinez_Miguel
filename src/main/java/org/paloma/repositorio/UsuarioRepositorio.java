package org.paloma.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.persistencia.AlmacenArchivos;

/** Repositorio de usuarios, guardados en {@code data/usuarios.dat}. */
public class UsuarioRepositorio implements IRepositorioExtend<Usuario, Integer> {

    private static final String ARCHIVO = "usuarios.dat";

    @Override
    public long count() {
        return registros().size();
    }

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

    @Override
    public void deleteAll() {
        new ResenaRepositorio().deleteAll();
        guardar(new ArrayList<>());
    }

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

    @Override
    public Usuario findById(Integer id) {
        return findByIdOptional(id).orElse(null);
    }

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

    @Override
    public List<Usuario> findAllToList() {
        List<Usuario> resultado = new ArrayList<>();
        for (UsuarioDato usuario : registros()) {
            resultado.add(mapear(usuario));
        }
        return resultado;
    }

    @Override
    public Iterable<Usuario> findAll() {
        return findAllToList();
    }

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

    /** Devuelve el usuario con ese email o {@link Optional#empty()} si no existe. */
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

    /** Devuelve los usuarios inscritos en la fecha indicada o posteriormente. */
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

    Usuario findSimpleById(int id) {
        for (UsuarioDato usuario : registros()) {
            if (usuario.id() == id) {
                return new Usuario(usuario.id(), usuario.nombre(), usuario.email(),
                        usuario.fechaInscripcion());
            }
        }
        return null;
    }

    private Usuario mapear(UsuarioDato dato) {
        Usuario usuario = new Usuario(dato.id(), dato.nombre(), dato.email(), dato.fechaInscripcion());
        for (Resena resena : new ResenaRepositorio().findByUsuario(dato.id())) {
            usuario.agregarResena(resena);
        }
        return usuario;
    }

    private List<UsuarioDato> registros() {
        return AlmacenArchivos.leerLista(ARCHIVO, UsuarioDato.class);
    }

    private void guardar(List<UsuarioDato> usuarios) {
        AlmacenArchivos.guardarLista(ARCHIVO, usuarios);
    }

    private int siguienteId(List<UsuarioDato> usuarios) {
        int mayorId = 0;
        for (UsuarioDato usuario : usuarios) {
            if (usuario.id() > mayorId) {
                mayorId = usuario.id();
            }
        }
        return mayorId + 1;
    }

    private void reemplazar(List<UsuarioDato> usuarios, UsuarioDato nuevo) {
        for (int i = usuarios.size() - 1; i >= 0; i--) {
            if (usuarios.get(i).id() == nuevo.id()) {
                usuarios.remove(i);
            }
        }
        usuarios.add(nuevo);
    }

    private void validarId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
    }

    private record UsuarioDato(int id, String nombre, String email, LocalDate fechaInscripcion)
            implements java.io.Serializable {
    }
}
