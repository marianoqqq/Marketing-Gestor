package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.request.InmobiliariaRequest;
import com.inmomarketing.api.dto.response.InmobiliariaResponse;
import com.inmomarketing.api.model.InmobiliariaEntity;
import org.springframework.stereotype.Component;

@Component
public class InmobiliariaMapper {

    public InmobiliariaEntity toEntity(InmobiliariaRequest request) {
        if (request == null) {
            return null;
        }
        InmobiliariaEntity inmobiliaria = new InmobiliariaEntity();
        inmobiliaria.setNombre(request.getNombre());
        inmobiliaria.setTelefono(request.getTelefono());
        inmobiliaria.setInstagram(request.getInstagram());
        inmobiliaria.setDireccion(request.getDireccion());
        inmobiliaria.setLogoUrl(request.getLogoUrl());
        return inmobiliaria;
    }

    public InmobiliariaResponse toResponse(InmobiliariaEntity inmobiliaria) {
        if (inmobiliaria == null) {
            return null;
        }
        return new InmobiliariaResponse(
                inmobiliaria.getId(),
                inmobiliaria.getNombre(),
                inmobiliaria.getTelefono(),
                inmobiliaria.getInstagram(),
                inmobiliaria.getDireccion(),
                inmobiliaria.getLogoUrl()
        );
    }
}