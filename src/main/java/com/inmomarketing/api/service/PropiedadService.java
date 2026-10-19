package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.PropiedadRequest;
import com.inmomarketing.api.dto.response.PropiedadResponse;

import java.util.List;

public interface PropiedadService {
    PropiedadResponse create(PropiedadRequest request, String userEmail);
    PropiedadResponse getById(Long id, String userEmail);
    List<PropiedadResponse> getAll(String userEmail);
    PropiedadResponse update(Long id, PropiedadRequest request, String userEmail);
    void delete(Long id, String userEmail);
}