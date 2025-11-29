package com.instituto.compendium.repository;

import com.instituto.compendium.model.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
    Optional<Plataforma> findByNombre(String nombre);
}
