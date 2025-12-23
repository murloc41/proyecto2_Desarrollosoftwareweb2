package com.instituto.compendium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "medicamentos")
public class Medicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;
    
    @NotNull(message = "La dosis en mg no puede ser nula")
    @Min(value = 1, message = "La dosis debe ser mayor a 0")
    @Column(nullable = false)
    private Integer dosisMg;
    
    @NotBlank(message = "El tipo no puede estar vacío")
    @Column(nullable = false)
    private String tipo;
    
    @NotNull(message = "El indicador de uso delicado no puede ser nulo")
    @Column(nullable = false)
    private Boolean usoDelicado;

    // Constructores
    public Medicamento() {}
    
    public Medicamento(String nombre, Integer dosisMg, String tipo, Boolean usoDelicado) {
        this.nombre = nombre;
        this.dosisMg = dosisMg;
        this.tipo = tipo;
        this.usoDelicado = usoDelicado;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Integer getDosisMg() { return dosisMg; }
    public void setDosisMg(Integer dosisMg) { this.dosisMg = dosisMg; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Boolean getUsoDelicado() { return usoDelicado; }
    public void setUsoDelicado(Boolean usoDelicado) { this.usoDelicado = usoDelicado; }
}
