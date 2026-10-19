package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.InmobiliariaRequest;
import com.inmomarketing.api.dto.response.InmobiliariaResponse;

public interface InmobiliariaService {
    InmobiliariaResponse getMiInmobiliaria(String userEmail);
    InmobiliariaResponse update(InmobiliariaRequest request, String userEmail);
}