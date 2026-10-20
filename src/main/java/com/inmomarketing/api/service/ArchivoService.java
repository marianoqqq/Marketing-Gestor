package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.response.ArchivoResponse;
import com.inmomarketing.api.model.ArchivoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchivoService {
    List<ArchivoResponse> subirFotos(Long propiedadId, List<MultipartFile> fotos, String userEmail);
    List<ArchivoResponse> getFotos(Long propiedadId, String userEmail);
    ArchivoEntity getArchivo(Long id, String userEmail);
}