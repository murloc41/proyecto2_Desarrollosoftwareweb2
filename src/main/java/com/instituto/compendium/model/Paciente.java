package com.instituto.compendium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El RUT no puede estar vacío")
    @Pattern(regexp = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}[-]?[0-9K]$", message = "El RUT debe tener formato válido (ej: 12.345.678-9)")
    @Column(nullable = false, unique = true)
    private String rut;
    
    @NotNull(message = "El piso no puede ser nulo")
    @Min(value = 1, message = "El piso debe ser mayor a 0")
    @Max(value = 20, message = "El piso no puede ser mayor a 20")
    @Column(nullable = false)
    private Integer piso;
    
    @NotNull(message = "El turno no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    
    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
    
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
}