package com.daniel.literalura.servicio;

import com.daniel.literalura.modelos.Autor;
import com.daniel.literalura.modelos.Libro;
import com.daniel.literalura.repositorio.LibroRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ServicioLibros {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @PersistenceContext
    private EntityManager entityManager;

    public Libro buscarLibroPorTitulo(String titulo) throws IOException, InterruptedException, URISyntaxException {
        // Codificar la URL
        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String url = "https://gutendex.com/books/?search=" + tituloCodificado;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());
        JsonNode resultsNode = jsonNode.get("results");

        if (resultsNode.isArray() && resultsNode.size() > 0) {
            JsonNode libroNode = resultsNode.get(0);
            String tituloLibro = libroNode.get("title").asText();
            JsonNode autorNode = libroNode.get("authors").get(0); // Considerando solo el primer autor
            String nombreAutor = autorNode.get("name").asText();
            int anoNacimiento = autorNode.get("birth_year").asInt(0); // Usando asInt(0) para obtener 0 si el valor es nulo
            int anoFallecimiento = autorNode.get("death_year").asInt(0); // Usando asInt(0) para obtener 0 si el valor es nulo
            String[] idiomas = objectMapper.readValue(libroNode.get("languages").toString(), String[].class);
            int numeroDescargas = libroNode.get("download_count").asInt();

            Autor autor = new Autor(nombreAutor, anoNacimiento, anoFallecimiento);
            Libro libro = new Libro(tituloLibro, autor, idiomas, numeroDescargas);

            return libro;
        } else {
            return null; // o lanzar una excepción
        }
    }

    public List<Libro> listarLibros() {
        return libroRepositorio.findAll();
    }

    @Transactional
    public Libro guardarLibro(Libro libro) {
        // Asegurar que el autor esté asociado a la sesión de Hibernate
        libro.setAutor(entityManager.merge(libro.getAutor()));
        return libroRepositorio.save(libro);
    }

    public List<Libro> librosPorIdioma(String idioma) {
        return libroRepositorio.librosPorIdioma(idioma);
    }
}