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

    private static final Path DIRECTORIO = Path.of("data");

    private AlmacenArchivos() {
    }

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
