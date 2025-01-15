package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @OneToMany(mappedBy = "libro", fetch = FetchType.EAGER)
    private List<Autor> autor;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> idiomas;
    private  double descargas;

    public Libro() {

    }
    public List<Autor> getAutores() {
        return autor;
    }

    public void setAutores(List<Autor> autores) {
        this.autor = autores;
    }
    public Libro(DatosLibros datosLibros){
        this.titulo = datosLibros.titulo();
        this.idiomas = datosLibros.idiomas();
        this.descargas = datosLibros.descargas();
    }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutor() {
        return autor;
    }

    public void setAutor(List<Autor> autor) {
        this.autor = autor;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public double getDescargas() {
        return descargas;
    }

    public void setDescargas(double descargas) {
        this.descargas = descargas;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "titulo='" + titulo + '\'' +
                ", autor=" + (autor != null ? autor.size() : 0) + " autores" + // Imprime la cantidad de autores
                ", idiomas=" + (idiomas != null ? idiomas.size() : 0) + " idiomas" + // Imprime la cantidad de idiomas
                ", descargas=" + descargas +
                '}';
    }
}
