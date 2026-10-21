package com.inmomarketing.api.repository;

import com.inmomarketing.api.enums.TipoArchivo;
import com.inmomarketing.api.model.ArchivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoRepository extends JpaRepository<ArchivoEntity, Long> {
    List<ArchivoEntity> findByPropiedadIdAndTipo(Long propiedadId, TipoArchivo tipo);
    Optional<ArchivoEntity> findByIdAndPropiedadInmobiliariaId(Long id, Long inmobiliariaId);
    List<ArchivoEntity> findByIdInAndPropiedadId(List<Long> ids, Long propiedadId);
}