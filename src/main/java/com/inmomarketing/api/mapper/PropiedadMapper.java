package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.request.PropiedadRequest;
import com.inmomarketing.api.dto.response.PropiedadResponse;
import com.inmomarketing.api.model.PropiedadEntity;
import org.springframework.stereotype.Component;

@Component
public class PropiedadMapper {

    public PropiedadEntity toEntity(PropiedadRequest request) {
        if (request == null) {
            return null;
        }
        PropiedadEntity propiedad = new PropiedadEntity();
        propiedad.setTitulo(request.getTitulo());
        propiedad.setTipo(request.getTipo());
        propiedad.setOperacion(request.getOperacion());
        propiedad.setEstado(request.getEstado());
        propiedad.setPrecio(request.getPrecio());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setAmbientes(request.getAmbientes());
        propiedad.setMetrosCuadrados(request.getMetrosCuadrados());
        propiedad.setDescripcion(request.getDescripcion());
        return propiedad;
    }

    public PropiedadResponse toResponse(PropiedadEntity propiedad) {
        if (propiedad == null) {
            return null;
        }
        return new PropiedadResponse(
                propiedad.getId(),
                propiedad.getTitulo(),
                propiedad.getTipo(),
                propiedad.getOperacion(),
                propiedad.getEstado(),
                propiedad.getPrecio(),
                propiedad.getDireccion(),
                propiedad.getAmbientes(),
                propiedad.getMetrosCuadrados(),
                propiedad.getDescripcion(),
                propiedad.getInmobiliaria().getId()
        );
    }
}