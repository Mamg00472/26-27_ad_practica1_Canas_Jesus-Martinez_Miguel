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
     * Devuelve la entidad T con identificador id.
     *
     * @param id    Identificador de la entidad
     * @return      Entidad que tiene como identificador id u Optional#empty() si no se encuentra
     *
     * @throws IllegalArgumentException En caso de ser id nulo
     */
    Optional<T> findByIdOptional(ID id);

    /**
     * Devuelve todas las instancias de tipo T
     *
     * @return    todas las instancias.
     * */
    List<T> findAllToList();
}
