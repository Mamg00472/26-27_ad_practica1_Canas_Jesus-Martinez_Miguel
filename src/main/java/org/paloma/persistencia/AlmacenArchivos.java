package org.paloma.persistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y lee listas de objetos en ficheros dentro de la carpeta {@code data}.
 */
public class AlmacenArchivos {

    /** Directorio en el que se guardan los archivos de datos. */
    private static final Path DIRECTORIO = Path.of("data");

    /** Evita la creación de instancias de esta clase de utilidades. */
    private AlmacenArchivos() {
    }

    /**
     * Lee una lista serializada del directorio de datos.
     *
     * @param <T> tipo de los elementos que se devolverán
     * @param nombre nombre del archivo que se leerá
     * @param tipo clase usada para comprobar y convertir los elementos leídos
     * @return elementos leídos; lista vacía si el archivo no existe
     * @throws IllegalStateException si el archivo no puede leerse o sus datos
     *                               no pueden deserializarse
     * @throws ClassCastException si un elemento no corresponde al tipo indicado
     */
    public static <T> List<T> leerLista(String nombre, Class<T> tipo) {
        Path archivo = DIRECTORIO.resolve(nombre);
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            List<?> registros = (List<?>) entrada.readObject();
            List<T> resultado = new ArrayList<>();

            for (Object registro : registros) {
                resultado.add(tipo.cast(registro));
            }
            return resultado;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo leer el fichero " + archivo, e);
        }
    }

    /**
     * Guarda una copia de la lista en un archivo del directorio de datos.
     *
     * @param nombre nombre del archivo en el que se guardarán los datos
     * @param registros registros que se serializarán
     * @throws IllegalStateException si el directorio o el archivo no pueden
     *                               crearse o escribirse
     */
    public static void guardarLista(String nombre, List<?> registros) {
        Path archivo = DIRECTORIO.resolve(nombre);
        try {
            Files.createDirectories(DIRECTORIO);
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(archivo))) {
                salida.writeObject(new ArrayList<>(registros));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el fichero " + archivo, e);
        }
    }
}
