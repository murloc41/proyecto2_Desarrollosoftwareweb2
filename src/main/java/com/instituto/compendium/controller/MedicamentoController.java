package com.instituto.compendium.controller;

import com.instituto.compendium.model.Medicamento;
import com.instituto.compendium.repository.MedicamentoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:3000", "capacitor://localhost"})
public class MedicamentoController {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    // GET todos
    @GetMapping
    public ResponseEntity<List<Medicamento>> obtenerTodos() {
        List<Medicamento> medicamentos = medicamentoRepository.findAll();
        return ResponseEntity.ok(medicamentos);
    }

    // GET por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> obtenerPorId(@PathVariable Long id) {
        Optional<Medicamento> medicamento = medicamentoRepository.findById(id);
        return medicamento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST crear
    @PostMapping
    public ResponseEntity<Medicamento> crear(@Valid @RequestBody Medicamento medicamento) {
        try {
            Medicamento medicamentoGuardado = medicamentoRepository.save(medicamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // PUT actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> actualizar(@PathVariable Long id, @Valid @RequestBody Medicamento medicamentoActualizado) {
        Optional<Medicamento> medicamentoExistente = medicamentoRepository.findById(id);
        
        if (medicamentoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Medicamento medicamento = medicamentoExistente.get();
        if (medicamentoActualizado.getNombre() != null) {
            medicamento.setNombre(medicamentoActualizado.getNombre());
        }
        if (medicamentoActualizado.getDosisMg() != null) {
            medicamento.setDosisMg(medicamentoActualizado.getDosisMg());
        }
        if (medicamentoActualizado.getTipo() != null) {
            medicamento.setTipo(medicamentoActualizado.getTipo());
        }
        if (medicamentoActualizado.getUsoDelicado() != null) {
            medicamento.setUsoDelicado(medicamentoActualizado.getUsoDelicado());
        }
        
        Medicamento medicamentoActualizadoGuardado = medicamentoRepository.save(medicamento);
        return ResponseEntity.ok(medicamentoActualizadoGuardado);
    }

    // DELETE eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!medicamentoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        medicamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
