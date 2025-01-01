package com.daniel.literalura.repositorio;

import com.daniel.literalura.modelos.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Long> {

    @Query(value = "SELECT l.* FROM libro l, unnest(l.idiomas) AS idioma WHERE idioma = :idioma", nativeQuery = true)
    List<Libro> librosPorIdioma(String idioma);
}