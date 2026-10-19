package com.inmomarketing.api.repository;

import com.inmomarketing.api.model.PropiedadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropiedadRepository extends JpaRepository<PropiedadEntity, Long> {
    List<PropiedadEntity> findByInmobiliariaId(Long inmobiliariaId);
    Optional<PropiedadEntity> findByIdAndInmobiliariaId(Long id, Long inmobiliariaId);
}