package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.PropiedadRequest;
import com.inmomarketing.api.dto.response.PropiedadResponse;
import com.inmomarketing.api.exception.ResourceNotFoundException;
import com.inmomarketing.api.mapper.PropiedadMapper;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.model.PropiedadEntity;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.PropiedadRepository;
import com.inmomarketing.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PropiedadServiceImpl implements PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final PropiedadMapper propiedadMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PropiedadResponse create(PropiedadRequest request, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        PropiedadEntity propiedad = propiedadMapper.toEntity(request);
        propiedad.setInmobiliaria(inmobiliaria);
        PropiedadEntity saved = propiedadRepository.save(propiedad);
        return propiedadMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PropiedadResponse getById(Long id, String userEmail) {
        PropiedadEntity propiedad = getPropiedadDelUsuario(id, userEmail);
        return propiedadMapper.toResponse(propiedad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropiedadResponse> getAll(String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return propiedadRepository.findByInmobiliariaId(inmobiliaria.getId())
                .stream().map(propiedadMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public PropiedadResponse update(Long id, PropiedadRequest request, String userEmail) {
        PropiedadEntity propiedad = getPropiedadDelUsuario(id, userEmail);

        propiedad.setTitulo(request.getTitulo());
        propiedad.setTipo(request.getTipo());
        propiedad.setOperacion(request.getOperacion());
        if (request.getEstado() != null) {
            propiedad.setEstado(request.getEstado());
        }
        propiedad.setPrecio(request.getPrecio());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setAmbientes(request.getAmbientes());
        propiedad.setMetrosCuadrados(request.getMetrosCuadrados());
        propiedad.setDescripcion(request.getDescripcion());

        PropiedadEntity updated = propiedadRepository.save(propiedad);
        return propiedadMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        PropiedadEntity propiedad = getPropiedadDelUsuario(id, userEmail);
        propiedadRepository.delete(propiedad);
    }

    private InmobiliariaEntity getInmobiliariaDelUsuario(String userEmail) {
        UserEntity user = userRepository.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        if (user.getInmobiliaria() == null) {
            throw new IllegalArgumentException("El usuario no pertenece a ninguna inmobiliaria");
        }
        return user.getInmobiliaria();
    }

    private PropiedadEntity getPropiedadDelUsuario(Long id, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return propiedadRepository.findByIdAndInmobiliariaId(id, inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", "id", id));
    }
}