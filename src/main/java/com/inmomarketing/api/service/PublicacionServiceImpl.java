package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.PublicacionRequest;
import com.inmomarketing.api.dto.response.PublicacionResponse;
import com.inmomarketing.api.enums.EstadoPublicacion;
import com.inmomarketing.api.exception.ResourceNotFoundException;
import com.inmomarketing.api.mapper.PublicacionMapper;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.model.PropiedadEntity;
import com.inmomarketing.api.model.PublicacionEntity;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.PropiedadRepository;
import com.inmomarketing.api.repository.PublicacionRepository;
import com.inmomarketing.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final PropiedadRepository propiedadRepository;
    private final UserRepository userRepository;
    private final PublicacionMapper publicacionMapper;
    private final ChatModel chatModel;

    @Override
    @Transactional
    public PublicacionResponse create(PublicacionRequest request, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        PropiedadEntity propiedad = propiedadRepository.findByIdAndInmobiliariaId(request.getPropiedadId(), inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", "id", request.getPropiedadId()));

        PublicacionEntity publicacion = publicacionMapper.toEntity(request);
        publicacion.setPropiedad(propiedad);
        publicacion.setEstado(EstadoPublicacion.BORRADOR);

        PublicacionEntity saved = publicacionRepository.save(publicacion);
        return publicacionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionResponse> getAll(String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return publicacionRepository.findByPropiedadInmobiliariaId(inmobiliaria.getId())
                .stream().map(publicacionMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PublicacionResponse getById(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);
        return publicacionMapper.toResponse(publicacion);
    }

    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);
        publicacionRepository.delete(publicacion);
    }

    @Override
    @Transactional
    public PublicacionResponse generarTexto(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);

        if (publicacion.getEstado() == EstadoPublicacion.PUBLICADA) {
            throw new IllegalArgumentException("No se puede volver a generar una publicación ya publicada");
        }

        try {
            String prompt = armarPrompt(publicacion);
            String texto = chatModel.call(prompt);
            publicacion.setTexto(texto);
            publicacion.setEstado(EstadoPublicacion.LISTA);
        } catch (Exception e) {
            publicacion.setEstado(EstadoPublicacion.ERROR);
        }

        PublicacionEntity saved = publicacionRepository.save(publicacion);
        return publicacionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PublicacionResponse marcarPublicada(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);

        if (publicacion.getEstado() != EstadoPublicacion.LISTA) {
            throw new IllegalArgumentException("La publicación debe estar en estado LISTA para ser marcada como publicada");
        }

        publicacion.setEstado(EstadoPublicacion.PUBLICADA);
        publicacion.setPublicadaEn(LocalDateTime.now());

        PublicacionEntity saved = publicacionRepository.save(publicacion);
        return publicacionMapper.toResponse(saved);
    }

    private InmobiliariaEntity getInmobiliariaDelUsuario(String userEmail) {
        UserEntity user = userRepository.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        if (user.getInmobiliaria() == null) {
            throw new IllegalArgumentException("El usuario no pertenece a ninguna inmobiliaria");
        }
        return user.getInmobiliaria();
    }

    private PublicacionEntity getPublicacion(Long id, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return publicacionRepository.findByIdAndPropiedadInmobiliariaId(id, inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Publicación", "id", id));
    }

    private String armarPrompt(PublicacionEntity publicacion) {
        PropiedadEntity p = publicacion.getPropiedad();
        StringBuilder sb = new StringBuilder();
        sb.append("Escribí un post para ").append(publicacion.getRed())
                .append(" de esta propiedad, con emojis y hashtags.\n")
                .append("Formato: ").append(publicacion.getFormato()).append("\n")
                .append("Título: ").append(p.getTitulo()).append("\n")
                .append("Tipo: ").append(p.getTipo()).append("\n")
                .append("Operación: ").append(p.getOperacion()).append("\n")
                .append("Precio: ").append(p.getPrecio()).append("\n")
                .append("Ambientes: ").append(p.getAmbientes()).append("\n");

        if (p.getMetrosCuadrados() > 0) {
            sb.append("Metros cuadrados: ").append(p.getMetrosCuadrados()).append(" m²\n");
        }
        if (p.getDireccion() != null && !p.getDireccion().isBlank()) {
            sb.append("Dirección: ").append(p.getDireccion()).append("\n");
        }
        if (p.getDescripcion() != null && !p.getDescripcion().isBlank()) {
            sb.append("Descripción: ").append(p.getDescripcion()).append("\n");
        }
        if (publicacion.getIndicaciones() != null && !publicacion.getIndicaciones().isBlank()) {
            sb.append("Indicaciones adicionales: ").append(publicacion.getIndicaciones()).append("\n");
        }
        return sb.toString();
    }
}
