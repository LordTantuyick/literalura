package com.daniel.literalura.controlador;

import com.daniel.literalura.modelos.Autor;
import com.daniel.literalura.modelos.Libro;
import com.daniel.literalura.servicio.ServicioAutores;
import com.daniel.literalura.servicio.ServicioLibros;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.net.URISyntaxException;

@Component
public class ControladorConsola implements CommandLineRunner {

    @Autowired
    private ServicioLibros servicioLibros;

    @Autowired
    private ServicioAutores servicioAutores;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            mostrarMenu();
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir la nueva línea

                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo(scanner);
                        break;
                    case 2:
                        listarLibros();
                        break;
                    case 3:
                        listarAutores();
                        break;
                    case 4:
                        listarAutoresVivosEnAnio(scanner);
                        break;
                    case 5:
                        listarLibrosPorIdioma(scanner);
                        break;
                    case 6:
                        System.out.println("Saliendo de la aplicación...");
                        return;
                    default:
                        System.out.println("Opción inválida. Por favor, ingrese un número entre 1 y 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpiar el búfer del scanner
            } catch (IOException | InterruptedException | URISyntaxException e) {
                System.err.println("Error al comunicarse con la API: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n--- LiterAlura ---");
        System.out.println("1. Buscar libro por título");
        System.out.println("2. Listar libros registrados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Listar autores vivos en un determinado año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("6. Salir");
        System.out.print("Ingrese una opción: ");
    }

    @Transactional
    private void buscarLibroPorTitulo(Scanner scanner) throws IOException, InterruptedException, URISyntaxException {
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();
        Libro libro = servicioLibros.buscarLibroPorTitulo(titulo);
        if (libro != null) {
            System.out.println("Libro encontrado:");
            // Persistir el autor primero para obtener su ID
            Autor autor = libro.getAutor();
            autor = servicioAutores.guardarAutor(autor);
            libro.setAutor(autor);
            libro = servicioLibros.guardarLibro(libro);
            System.out.println(libro);
        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    private void listarLibros() {
        List<Libro> libros = servicioLibros.listarLibros();
        if (!libros.isEmpty()) {
            System.out.println("Libros registrados:");
            libros.forEach(System.out::println);
        } else {
            System.out.println("No hay libros registrados.");
        }
    }

    private void listarAutores() {
        List<Autor> autores = servicioAutores.listarAutores();
        if (!autores.isEmpty()) {
            System.out.println("Autores registrados:");
            autores.forEach(System.out::println);
        } else {
            System.out.println("No hay autores registrados.");
        }
    }

    private void listarAutoresVivosEnAnio(Scanner scanner) {
        System.out.print("Ingrese el año: ");
        int anio = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea
        List<Autor> autores = servicioAutores.autoresVivosEnAnio(anio);
        if (!autores.isEmpty()) {
            System.out.println("Autores vivos en el año " + anio + ":");
            autores.forEach(System.out::println);
        } else {
            System.out.println("No se encontraron autores vivos en el año " + anio + ".");
        }
    }

    private void listarLibrosPorIdioma(Scanner scanner) {
        System.out.print("Ingrese el idioma: ");
        String idioma = scanner.nextLine();
        List<Libro> libros = servicioLibros.librosPorIdioma(idioma);
        if (!libros.isEmpty()) {
            System.out.println("Libros en " + idioma + ":");
            libros.forEach(System.out::println);
        } else {
            System.out.println("No se encontraron libros en " + idioma + ".");
        }
    }
}