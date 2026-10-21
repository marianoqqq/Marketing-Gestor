package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.PublicacionRequest;
import com.inmomarketing.api.dto.response.PublicacionResponse;
import com.inmomarketing.api.dto.response.SugerenciaTextoResponse;

import java.util.List;

public interface PublicacionService {
    PublicacionResponse create(PublicacionRequest request, String userEmail);
    List<PublicacionResponse> getAll(String userEmail);
    PublicacionResponse getById(Long id, String userEmail);
    void delete(Long id, String userEmail);
    PublicacionResponse generarTexto(Long id, String userEmail);
    PublicacionResponse marcarPublicada(Long id, String userEmail);
    SugerenciaTextoResponse sugerirTextoImagen(Long id, String userEmail);
    PublicacionResponse actualizarTextoImagen(Long id, String textoImagen, String estiloImagen, String userEmail);
}
