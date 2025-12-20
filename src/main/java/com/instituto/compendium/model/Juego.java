package com.instituto.compendium.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.HashSet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Entity
public class Juego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no debe superar los 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String descripcion;

    private String imagen;
    
    private Double calificacion = 0.0;

    @Column(name = "total_valoraciones")
    private Integer totalValoraciones = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "juego_categorias",
        joinColumns = @JoinColumn(name = "juego_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "juego_plataformas",
        joinColumns = @JoinColumn(name = "juego_id"),
        inverseJoinColumns = @JoinColumn(name = "plataforma_id")
    )
    private Set<Plataforma> plataformas = new HashSet<>();

    // Métodos para compatibilidad con código existente
    @Deprecated
    public String getNombre() {
        return titulo;
    }

    @Deprecated
    public void setNombre(String nombre) {
        this.titulo = nombre;
    }

    @Deprecated
    public Double getRating() {
        return calificacion;
    }

    @Deprecated
    public void setRating(Double rating) {
        this.calificacion = rating;
    }

    @Deprecated
    public String getGenero() {
        if (categorias == null || categorias.isEmpty()) return null;
        return categorias.iterator().next().getNombre();
    }

    @Deprecated
    public void setGenero(String genero) {
        // Se ignora, usar categorias directamente
    }

    @Deprecated
    public Categoria getCategoria() {
        if (categorias == null || categorias.isEmpty()) return null;
        return categorias.iterator().next();
    }

    @Deprecated
    public void setCategoria(Categoria cat) {
        if (cat != null) {
            categorias.clear();
            categorias.add(cat);
        }
    }

    @Deprecated
    public String getPlataforma() {
        if (plataformas == null || plataformas.isEmpty()) return null;
        return plataformas.iterator().next().getNombre();
    }

    @Deprecated
    public void setPlataforma(String plat) {
        // Se ignora, usar plataformas directamente
    }
}