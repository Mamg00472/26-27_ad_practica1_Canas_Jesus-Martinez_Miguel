package org.paloma.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * Amplía las operaciones básicas del repositorio con resultados opcionales
 * y colecciones concretas.
 *
 * @param <T> tipo de entidad gestionada
 * @param <ID> tipo del identificador de la entidad
 */
public interface IRepositorioExtend<T, ID> extends IRepositorio<T, ID> {

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @return un {@link Optional} con la entidad si existe, o
     *         {@link Optional#empty()} si no se encuentra
     * @throws IllegalArgumentException si el identificador es {@code null}
     */
    Optional<T> findByIdOptional(ID id);

    /**
     * Obtiene todas las entidades como una lista.
     *
     * @return lista con todas las entidades
     */
    List<T> findAllToList();
}
