package org.paloma;

import java.time.LocalDate;
import java.util.Optional;

import org.paloma.modelo.Escritor;
import org.paloma.modelo.Libro;
import org.paloma.modelo.Resena;
import org.paloma.modelo.Usuario;
import org.paloma.repositorio.EscritorRepositorio;
import org.paloma.repositorio.LibroRepositorio;
import org.paloma.repositorio.ResenaRepositorio;
import org.paloma.repositorio.UsuarioRepositorio;

/**
 * Punto de entrada de la aplicación y demostración de las operaciones disponibles
 * en los repositorios de libros, usuarios, escritores y reseñas.
 */
public class Main {

    /**
     * Ejecuta ejemplos de consulta, inserción, actualización y eliminación.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        LibroRepositorio libros = new LibroRepositorio();
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        EscritorRepositorio escritores = new EscritorRepositorio();
        ResenaRepositorio resenas = new ResenaRepositorio();

        titulo("COUNT / FIND ALL");
        System.out.println("Libros:     " + libros.count());
        System.out.println("Usuarios:   " + usuarios.count());
        System.out.println("Escritores: " + escritores.count());
        System.out.println("Reseñas:    " + resenas.count());
        libros.findAll().forEach(System.out::println);

        
        titulo("FIND BY ID / EXISTS");
        System.out.println("findById(1): " + libros.findById(1));
        System.out.println("findById(9999): " + libros.findById(9999));
        Optional<Usuario> u = usuarios.findByIdOptional(1);
        System.out.println("findByIdOptional(1): " + u.map(Usuario::getNombre).orElse("no existe"));
        System.out.println("existsById(1): " + escritores.existsById(1));
        System.out.println("existsById(9999): " + escritores.existsById(9999));

        // ---------- SAVE (INSERT) ----------
        titulo("SAVE - INSERT");
        Libro libro = new Libro(0, "978-0000000001", "Libro de prueba", "Test", LocalDate.of(2024, 1, 1));
        libros.save(libro);
        System.out.println("Libro insertado: " + libro);

        Usuario usuario = new Usuario(0, "Usuario Prueba", "prueba@example.com", null);
        usuarios.save(usuario);
        System.out.println("Usuario insertado: " + usuario);

        Escritor escritor = new Escritor(0, "Escritor Prueba", "Biografía de prueba");
        escritores.save(escritor);
        System.out.println("Escritor insertado: " + escritor);

        Resena resena = new Resena(0, "Reseña de prueba", 5, usuario, libro);
        resenas.save(resena);
        System.out.println("Reseña insertada: " + resena);

        titulo("SAVE - UPDATE");
        libro.setTitulo("Libro de prueba (editado)");
        libros.save(libro);
        System.out.println("Tras actualizar: " + libros.findById(libro.getIdLibro()));

        resena.setValoracion(3);
        resenas.save(resena);
        System.out.println("Tras actualizar: " + resenas.findById(resena.getIdResena()));

        titulo("RESTRICCIONES");
        try {
            libros.save(new Libro(0, "978-0000000001", "ISBN repetido", "Test", null));
        } catch (RuntimeException e) {
            System.out.println("ISBN repetido -> " + e.getMessage());
        }
        try {
            usuarios.save(new Usuario(0, "Otro", "prueba@example.com", null));
        } catch (RuntimeException e) {
            System.out.println("Email repetido -> " + e.getMessage());
        }

        titulo("RAÍZ AGREGADA - AUTORÍA (LIBRO <-> ESCRITOR)");
        escritor.agregarLibro(libro);          // relación en ambos lados
        libros.save(libro);                    // LibroRepositorio persiste la autoría en libros.dat
        Libro recargado = libros.findById(libro.getIdLibro());
        System.out.println("Escritores del libro: " + recargado.getEscritores());
        System.out.println("Reseñas del libro:    " + recargado.getResenas());
        System.out.println("Libros del escritor:  " + escritores.findById(escritor.getIdEscritor()).getLibros());

        titulo("MÉTODOS PROPIOS");
        System.out.println("Libro.findByIsbn: " + libros.findByIsbn("978-0000000001").map(Libro::getTitulo).orElse("no existe"));
        System.out.println("Libro.findByGenero(Fantasía): " + libros.findByGenero("Fantasía").size());
        System.out.println("Libro.findByTituloContaining(Anillos): " + libros.findByTituloContaining("Anillos"));
        System.out.println("Libro.findByEscritor: " + libros.findByEscritor(escritor.getIdEscritor()));
        System.out.println("Escritor.findByNombreContaining(Tolkien): " + escritores.findByNombreContaining("Tolkien"));
        System.out.println("Escritor.findByLibro: " + escritores.findByLibro(libro.getIdLibro()));
        System.out.println("Usuario.findByEmail: " + usuarios.findByEmail("prueba@example.com").map(Usuario::getNombre).orElse("no existe"));
        System.out.println("Usuario.findByInscritoDesde(2025-06-01): " + usuarios.findByInscritoDesde(LocalDate.of(2025, 6, 1)).size());
        System.out.println("Reseña.findByLibro: " + resenas.findByLibro(libro.getIdLibro()));
        System.out.println("Reseña.findByUsuario: " + resenas.findByUsuario(usuario.getIdUsuario()));
        System.out.println("Reseña.findByValoracionMinima(5): " + resenas.findByValoracionMinima(5).size());

        // ---------- DELETE BY ID (limpieza de lo creado) ----------
        titulo("DELETE BY ID");
        resenas.deleteById(resena.getIdResena());
        libros.deleteById(libro.getIdLibro());
        usuarios.deleteById(usuario.getIdUsuario());
        escritores.deleteById(escritor.getIdEscritor());
        System.out.println("Libro existe tras borrar: " + libros.existsById(libro.getIdLibro()));
        System.out.println("Libros:     " + libros.count());
        System.out.println("Usuarios:   " + usuarios.count());
        System.out.println("Escritores: " + escritores.count());
        System.out.println("Reseñas:    " + resenas.count());
    }

    /** Imprime un encabezado para separar las secciones de la demostración. */
    private static void titulo(String texto) {
        System.out.println("\n===== " + texto + " =====");
    }
}