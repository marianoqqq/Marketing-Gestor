package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.InmobiliariaRequest;
import com.inmomarketing.api.dto.response.InmobiliariaResponse;
import com.inmomarketing.api.exception.ResourceNotFoundException;
import com.inmomarketing.api.mapper.InmobiliariaMapper;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.InmobiliariaRepository;
import com.inmomarketing.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InmobiliariaServiceImpl implements InmobiliariaService {

    private final InmobiliariaRepository inmobiliariaRepository;
    private final InmobiliariaMapper inmobiliariaMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public InmobiliariaResponse getMiInmobiliaria(String userEmail) {
        return inmobiliariaMapper.toResponse(getInmobiliariaDelUsuario(userEmail));
    }

    @Override
    @Transactional
    public InmobiliariaResponse update(InmobiliariaRequest request, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);

        inmobiliaria.setNombre(request.getNombre());
        inmobiliaria.setTelefono(request.getTelefono());
        inmobiliaria.setInstagram(request.getInstagram());
        inmobiliaria.setDireccion(request.getDireccion());
        inmobiliaria.setLogoUrl(request.getLogoUrl());

        InmobiliariaEntity updated = inmobiliariaRepository.save(inmobiliaria);
        return inmobiliariaMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InmobiliariaResponse> getAll() {
        return inmobiliariaRepository.findAll()
                .stream()
                .map(inmobiliariaMapper::toResponse)
                .toList();
    }

    private InmobiliariaEntity getInmobiliariaDelUsuario(String userEmail) {
        UserEntity user = userRepository.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        if (user.getInmobiliaria() == null) {
            throw new IllegalArgumentException("El usuario no pertenece a ninguna inmobiliaria");
        }
        return user.getInmobiliaria();
    }
}