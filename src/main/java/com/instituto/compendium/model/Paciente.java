package com.instituto.compendium.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String rut;
    
    @Column(nullable = false)
    private Integer piso;
    
    @Column(nullable = false)
    private String turno;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    
    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
    
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}