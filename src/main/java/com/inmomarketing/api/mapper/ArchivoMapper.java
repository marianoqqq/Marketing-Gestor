package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.response.ArchivoResponse;
import com.inmomarketing.api.model.ArchivoEntity;
import org.springframework.stereotype.Component;

@Component
public class ArchivoMapper {

    public ArchivoResponse toResponse(ArchivoEntity archivo) {
        if (archivo == null) {
            return null;
        }
        return new ArchivoResponse(
                archivo.getId(),
                archivo.getTipo(),
                archivo.getNombreOriginal(),
                archivo.getContentType(),
                "/api/archivos/" + archivo.getId()
        );
    }
}