package com.daniel.literalura.repositorio;

import com.daniel.literalura.modelos.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.anoNacimiento <= :anio AND (a.anoFallecimiento IS NULL OR a.anoFallecimiento >= :anio)")
    List<Autor> autoresVivosEnAnio(int anio);
}