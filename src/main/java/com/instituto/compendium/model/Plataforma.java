package com.instituto.compendium.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Plataforma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private String descripcion;
}