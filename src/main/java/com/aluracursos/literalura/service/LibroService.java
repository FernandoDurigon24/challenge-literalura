package com.aluracursos.literalura.service;

import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public Libro guardarLibro(Libro libro) {
        // Verificar si ya existe un libro con el mismo título
        Optional<Libro> libroExistente = libroRepository.findByTitulo(libro.getTitulo());

        if (libroExistente.isPresent()) {
            throw new RuntimeException("El libro con el título '" + libro.getTitulo() + "' ya existe.");
        }

        // Guardar el nuevo libro si no existe
        return libroRepository.save(libro);
    }
}