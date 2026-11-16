package org.paloma.repositorio;

/**
 * Define las operaciones básicas de persistencia para entidades identificadas
 * por un tipo de clave.
 *
 * @param <T> tipo de entidad gestionada
 * @param <ID> tipo del identificador de la entidad
 */
public interface IRepositorio<T, ID> {
    /**
     * Cuenta las entidades almacenadas.
     *
     * @return número de entidades
     */
    long count();

    /**
     * Elimina la entidad con el identificador indicado. Si no se encuentra,
     * no realiza ninguna acción.
     *
     * @param id identificador de la entidad. No debe ser nulo
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    void deleteById(ID id);

    /**
     * Elimina todas las entidades del repositorio.
     */
    void deleteAll();

    /**
     * Comprueba si existe una entidad con el identificador indicado.
     *
     * @param id identificador de la entidad
     * @return {@code true} si existe la entidad; {@code false} en caso contrario
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    boolean existsById(ID id);

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @return entidad encontrada o {@code null} si no existe
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    T findById(ID id);

    /**
     * Obtiene todas las entidades del repositorio.
     *
     * @return iterable con todas las entidades
     */
    Iterable<T> findAll();

    /**
     * Guarda una entidad nueva o actualiza una entidad existente.
     *
     * @param <S> tipo concreto de entidad
     * @param entity entidad que se desea guardar; no debe ser {@code null}
     * @return entidad guardada
     * @throws IllegalArgumentException si la entidad no puede guardarse
     */
    <S extends T> S save(S entity);
}