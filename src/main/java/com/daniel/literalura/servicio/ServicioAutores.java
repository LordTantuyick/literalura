package com.daniel.literalura.servicio;

import com.daniel.literalura.modelos.Autor;
import com.daniel.literalura.repositorio.AutorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioAutores {

    @Autowired
    private AutorRepositorio autorRepositorio;

    public List<Autor> listarAutores() {
        return autorRepositorio.findAll();
    }

    public Autor guardarAutor(Autor autor) {
        return autorRepositorio.save(autor);
    }

    public List<Autor> autoresVivosEnAnio(int anio) {
        return autorRepositorio.autoresVivosEnAnio(anio);
    }
}