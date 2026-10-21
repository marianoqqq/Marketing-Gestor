package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.request.PublicacionRequest;
import com.inmomarketing.api.dto.response.PublicacionResponse;
import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.model.PublicacionEntity;
import org.springframework.stereotype.Component;

@Component
public class PublicacionMapper {

    public PublicacionEntity toEntity(PublicacionRequest request) {
        if (request == null) {
            return null;
        }
        PublicacionEntity publicacion = new PublicacionEntity();
        publicacion.setRed(request.getRed());
        publicacion.setFormato(request.getFormato());
        publicacion.setIndicaciones(request.getIndicaciones());
        publicacion.setTextoImagen(request.getTextoImagen());
        publicacion.setEstiloImagen(request.getEstiloImagen());
        return publicacion;
    }

    public PublicacionResponse toResponse(PublicacionEntity entity) {
        if (entity == null) {
            return null;
        }
        Long propiedadId = entity.getPropiedad() != null ? entity.getPropiedad().getId() : null;
        return new PublicacionResponse(
                entity.getId(),
                propiedadId,
                entity.getRed(),
                entity.getFormato(),
                entity.getIndicaciones(),
                entity.getTexto(),
                entity.getTextoImagen(),
                entity.getEstiloImagen(),
                entity.getEstado(),
                entity.getPublicadaEn(),
                entity.getFotos().stream().map(ArchivoEntity::getId).toList(),
                entity.getImagenGenerada() != null ? entity.getImagenGenerada().getId() : null
        );
    }
}
