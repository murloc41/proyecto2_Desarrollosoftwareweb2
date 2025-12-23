package com.instituto.compendium.controller;

import com.instituto.compendium.model.Paciente;
import com.instituto.compendium.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    // GET todos
    @GetMapping
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    // GET por ID
    @GetMapping("/{id}")
    public Paciente obtenerPorId(@PathVariable Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    // POST crear
    @PostMapping
    public Paciente crear(@RequestBody Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    // PUT actualizar
    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @RequestBody Paciente pacienteActualizado) {
        Paciente paciente = pacienteRepository.findById(id).orElse(null);
        if (paciente != null) {
            paciente.setNombre(pacienteActualizado.getNombre());
            paciente.setRut(pacienteActualizado.getRut());
            paciente.setPiso(pacienteActualizado.getPiso());
            paciente.setTurno(pacienteActualizado.getTurno());
            return pacienteRepository.save(paciente);
        }
        return null;
    }

    // DELETE eliminar
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteRepository.deleteById(id);
    }
}