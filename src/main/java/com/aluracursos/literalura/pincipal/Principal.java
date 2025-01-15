package com.aluracursos.literalura.pincipal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumirAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Principal {
    private ConsumirAPI consumoApi = new ConsumirAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    Scanner teclado = new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    public Principal(LibroRepository repository, AutorRepository autorRepository) {
        this.libroRepository = repository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    1 - Buscar libro
                    2 - Filtrar libros registrados
                    3 - Listar autores registrados
                    4 - Buscar autor vivos en determinado año
                    5 - Buscar libros por idoma
                    0 - Salir 
                    
                    """;
            System.out.println("Escoge una opción");
            System.out.println(menu);

            var json = consumoApi.obtenerDatos("https://gutendex.com/books/?search=pride");
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarYGuardarLibro();
                    break;
                case 2:
                    mostrarLibros();
                    break;
                case 3:
                    mostrarAutores();
                    break;
                case 4 :
                    buscarAutoresPorAnio();
                    break;
                case 5:
                    buscarLibroPorIdioma();
                    break;
                case 0 :
                    break;
                default:
                    System.out.println("La opcion ingresada no es valida");
            }
            if (opcion == 0){
                break;
            }
        }
    }

    public void buscarYGuardarLibro() {
        System.out.println("Ingrese el nombre del libro: ");
        var buscarLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos("https://gutendex.com/books/?search=" + buscarLibro.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        List<DatosLibros> librosEncontrados = datos.resultado();

        if (!librosEncontrados.isEmpty()) {
            DatosLibros datosLibros = librosEncontrados.get(0); // Tomar el primer libro encontrado
            System.out.println("----libro----");
            System.out.println("Nombre: " + librosEncontrados.get(0).titulo());
            System.out.println("Autor: " + librosEncontrados.get(0).autor());
            System.out.println("Idiomas: " + librosEncontrados.get(0).idiomas());
            System.out.println("Descargas: " + librosEncontrados.get(0).descargas());

            // Verificar si el libro ya existe en la base de datos
            if (libroRepository.findByTitulo(datosLibros.titulo()).isEmpty()) {
                // Crear la entidad Libro
                Libro libro = new Libro();
                libro.setTitulo(datosLibros.titulo());
                libro.setIdiomas(datosLibros.idiomas());
                libro.setDescargas(datosLibros.descargas());

                // Guardar el libro primero
                libro = libroRepository.save(libro);

                // Guardar autores
                List<Autor> autores = new ArrayList<>();
                for (DatosAuto datosAuto : datosLibros.autor()) {
                    Autor autor = new Autor();
                    autor.setNombre(datosAuto.nombre());
                    autor.setNacimiento(datosAuto.nacimiento());
                    autor.setFallecimiento(datosAuto.fallecimiento());
                    autor.setLibro(libro); // Asociar al libro ya guardado
                    autores.add(autor);
                }

                autorRepository.saveAll(autores);

                System.out.println("Libro guardado con éxito: " + libro.getTitulo());
            } else {
                System.out.println("El libro ya existe en la base de datos.");
            }
        } else {
            System.out.println("No se encontraron libros con ese nombre.");
        }
    }
    @Transactional
    public void mostrarLibros() {
        List<Libro> libros = libroRepository.findAll();
        for (Libro libro : libros) {
            System.out.println("---------- Libro guardado ----------");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Idiomas: " + String.join(", ", libro.getIdiomas()));
            System.out.println("Descargas: " + libro.getDescargas());
            System.out.println("-------------------------------------");
        }
    }

    @Transactional
    public void mostrarAutores() {
        List<Autor> autores = autorRepository.findAll();
        for (Autor autor : autores) {
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("-------------");
        }
    }


    @Transactional
    public void buscarAutoresPorAnio() {
        System.out.println("Ingrese el año de nacimiento del autor: ");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autores = autorRepository.findByNacimiento(anio);

        if (!autores.isEmpty()) {
            System.out.println("Autores encontrados:");
            for (Autor autor : autores) {
                System.out.println("Autor: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getNacimiento());
                System.out.println("Fallecimiento: " + autor.getFallecimiento());
                System.out.println("-------------");
            }
        } else {
            System.out.println("No se encontraron autores nacidos en el año " + anio);
        }
    }

    public void buscarLibroPorIdioma() {
        // Crear un objeto Scanner para capturar el input del usuario
        Scanner scanner = new Scanner(System.in);

        // Pedir al usuario que ingrese el idioma
        System.out.println("Por favor ingresa el idioma para buscar libros:");
        String idioma = scanner.nextLine(); // Leer el idioma ingresado

        // Buscar libros por el idioma ingresado
        List<Libro> libros = libroRepository.findByIdiomas(idioma);

        // Mostrar los resultados
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros para el idioma: " + idioma);
        } else {
            System.out.println("Libros encontrados para el idioma " + idioma + ":");
            for (Libro libro : libros) {
                System.out.println(libro.getTitulo() + " por " + libro.getAutor());
            }
        }
    }
}