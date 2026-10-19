package com.inmomarketing.api.repository;

import com.inmomarketing.api.model.InmobiliariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InmobiliariaRepository extends JpaRepository<InmobiliariaEntity, Long> {
}