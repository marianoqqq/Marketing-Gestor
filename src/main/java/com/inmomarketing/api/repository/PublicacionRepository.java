package com.inmomarketing.api.repository;

import com.inmomarketing.api.model.PublicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicacionRepository extends JpaRepository<PublicacionEntity, Long> {
    List<PublicacionEntity> findByPropiedadInmobiliariaId(Long inmobiliariaId);
    Optional<PublicacionEntity> findByIdAndPropiedadInmobiliariaId(Long id, Long inmobiliariaId);
}
