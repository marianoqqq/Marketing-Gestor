package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.response.ArchivoResponse;
import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchivoMapper {

    private final StorageService storageService;

    public ArchivoResponse toResponse(ArchivoEntity archivo) {
        if (archivo == null) {
            return null;
        }
        String publicUrl = storageService.obtenerUrlPublica(archivo.getRuta());
        String url = (publicUrl != null) ? publicUrl : ("/api/archivos/" + archivo.getId());

        return new ArchivoResponse(
                archivo.getId(),
                archivo.getTipo(),
                archivo.getNombreOriginal(),
                archivo.getContentType(),
                url
        );
    }
}