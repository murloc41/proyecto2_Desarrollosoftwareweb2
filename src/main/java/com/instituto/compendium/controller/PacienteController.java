package com.instituto.compendium.controller;

import com.instituto.compendium.model.Paciente;
import com.instituto.compendium.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:3000", "capacitor://localhost"})
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    // GET todos
    @GetMapping
    public ResponseEntity<List<Paciente>> obtenerTodos() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        return ResponseEntity.ok(pacientes);
    }

    // GET por ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        return paciente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST crear
    @PostMapping
    public ResponseEntity<Paciente> crear(@Valid @RequestBody Paciente paciente) {
        try {
            Paciente pacienteGuardado = pacienteRepository.save(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // PUT actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @Valid @RequestBody Paciente pacienteActualizado) {
        Optional<Paciente> pacienteExistente = pacienteRepository.findById(id);
        
        if (pacienteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Paciente paciente = pacienteExistente.get();
        if (pacienteActualizado.getNombre() != null) {
            paciente.setNombre(pacienteActualizado.getNombre());
        }
        if (pacienteActualizado.getRut() != null) {
            paciente.setRut(pacienteActualizado.getRut());
        }
        if (pacienteActualizado.getPiso() != null) {
            paciente.setPiso(pacienteActualizado.getPiso());
        }
        if (pacienteActualizado.getTurno() != null) {
            paciente.setTurno(pacienteActualizado.getTurno());
        }
        
        Paciente pacienteActualizadoGuardado = pacienteRepository.save(paciente);
        return ResponseEntity.ok(pacienteActualizadoGuardado);
    }

    // DELETE eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!pacienteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pacienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}