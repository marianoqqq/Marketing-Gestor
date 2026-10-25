package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.response.ArchivoResponse;
import com.inmomarketing.api.enums.TipoArchivo;
import com.inmomarketing.api.exception.ResourceNotFoundException;
import com.inmomarketing.api.mapper.ArchivoMapper;
import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.model.PropiedadEntity;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.ArchivoRepository;
import com.inmomarketing.api.repository.PropiedadRepository;
import com.inmomarketing.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArchivoServiceImpl implements ArchivoService {

    private static final List<String> FORMATOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    private final ArchivoRepository archivoRepository;
    private final PropiedadRepository propiedadRepository;
    private final UserRepository userRepository;
    private final ArchivoMapper archivoMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public List<ArchivoResponse> subirFotos(Long propiedadId, List<MultipartFile> fotos, String userEmail) {
        PropiedadEntity propiedad = getPropiedadDelUsuario(propiedadId, userEmail);

        List<ArchivoResponse> resultado = new ArrayList<>();
        for (MultipartFile foto : fotos) {
            if (foto.isEmpty() || !FORMATOS_PERMITIDOS.contains(foto.getContentType())) {
                throw new IllegalArgumentException("Solo se aceptan imágenes JPG, PNG o WEBP");
            }

            String extension = obtenerExtension(foto.getOriginalFilename(), foto.getContentType());
            String nombreArchivo = "propiedad_" + propiedad.getId() + "_" + UUID.randomUUID() + extension;
            String ruta;
            try {
                ruta = storageService.guardarArchivo("fotos", nombreArchivo, foto);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la foto: " + e.getMessage(), e);
            }

            ArchivoEntity archivo = ArchivoEntity.builder()
                    .ruta(ruta)
                    .nombreOriginal(foto.getOriginalFilename())
                    .contentType(foto.getContentType())
                    .tipo(TipoArchivo.FOTO_PROPIEDAD)
                    .propiedad(propiedad)
                    .build();
            resultado.add(archivoMapper.toResponse(archivoRepository.save(archivo)));
        }
        return resultado;
    }

    private String obtenerExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if ("image/png".equalsIgnoreCase(contentType)) return ".png";
        if ("image/webp".equalsIgnoreCase(contentType)) return ".webp";
        return ".jpg";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivoResponse> getFotos(Long propiedadId, String userEmail) {
        PropiedadEntity propiedad = getPropiedadDelUsuario(propiedadId, userEmail);
        return archivoRepository.findByPropiedadIdAndTipo(propiedad.getId(), TipoArchivo.FOTO_PROPIEDAD)
                .stream().map(archivoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoEntity getArchivo(Long id, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return archivoRepository.findByIdAndPropiedadInmobiliariaId(id, inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Archivo", "id", id));
    }

    private InmobiliariaEntity getInmobiliariaDelUsuario(String userEmail) {
        UserEntity user = userRepository.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        if (user.getInmobiliaria() == null) {
            throw new IllegalArgumentException("El usuario no pertenece a ninguna inmobiliaria");
        }
        return user.getInmobiliaria();
    }

    private PropiedadEntity getPropiedadDelUsuario(Long propiedadId, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        return propiedadRepository.findByIdAndInmobiliariaId(propiedadId, inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", "id", propiedadId));
    }
}