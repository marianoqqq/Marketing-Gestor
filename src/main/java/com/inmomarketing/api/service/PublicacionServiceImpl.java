package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.PublicacionRequest;
import com.inmomarketing.api.dto.response.PublicacionResponse;
import com.inmomarketing.api.dto.response.SugerenciaTextoResponse;
import com.inmomarketing.api.enums.EstadoPublicacion;
import com.inmomarketing.api.enums.TipoArchivo;
import com.inmomarketing.api.exception.ResourceNotFoundException;
import com.inmomarketing.api.mapper.PublicacionMapper;
import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.model.PropiedadEntity;
import com.inmomarketing.api.model.PublicacionEntity;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.ArchivoRepository;
import com.inmomarketing.api.repository.PropiedadRepository;
import com.inmomarketing.api.repository.PublicacionRepository;
import com.inmomarketing.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final PropiedadRepository propiedadRepository;
    private final ArchivoRepository archivoRepository;
    private final UserRepository userRepository;
    private final PublicacionMapper publicacionMapper;
    private final ChatModel chatModel;
    private final OpenAiImageService openAiImageService;
    private final StorageService storageService;

    @Override
    @Transactional
    public PublicacionResponse create(PublicacionRequest request, String userEmail) {
        InmobiliariaEntity inmobiliaria = getInmobiliariaDelUsuario(userEmail);
        PropiedadEntity propiedad = propiedadRepository.findByIdAndInmobiliariaId(request.getPropiedadId(), inmobiliaria.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", "id", request.getPropiedadId()));

        PublicacionEntity publicacion = publicacionMapper.toEntity(request);
        publicacion.setPropiedad(propiedad);
        publicacion.setEstado(EstadoPublicacion.BORRADOR);

        if (request.getFotoIds() != null && !request.getFotoIds().isEmpty()) {
            List<ArchivoEntity> fotos = archivoRepository.findByIdInAndPropiedadId(request.getFotoIds(), propiedad.getId());
            if (fotos.size() != request.getFotoIds().size()) {
                throw new IllegalArgumentException("Alguna de las fotos no existe o no pertenece a la propiedad");
            }
            publicacion.setFotos(fotos);
        }

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

    @Override
    @Transactional
    public SugerenciaTextoResponse sugerirTextoImagen(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);
        PropiedadEntity p = publicacion.getPropiedad();

        String prompt = "Sos un redactor publicitario inmobiliario experto. "
                + "Generá UN SOLO título o frase de gancho publicitario ultra-corto (máximo 8 palabras) "
                + "para colocar impreso sobre la imagen publicitaria de la siguiente propiedad:\n"
                + "Título: " + p.getTitulo() + "\n"
                + "Tipo: " + p.getTipo() + "\n"
                + "Operación: " + p.getOperacion() + "\n"
                + "Precio: " + p.getPrecio() + "\n"
                + "Ambientes: " + p.getAmbientes() + "\n"
                + (p.getDireccion() != null ? "Dirección: " + p.getDireccion() + "\n" : "")
                + "Respondé ÚNICAMENTE con la frase de gancho, sin comillas, sin explicaciones ni saludos. Ejemplo: '¡Gran Oportunidad! 2 Ambientes Luminoso'.";

        String sugerencia = chatModel.call(prompt);
        if (sugerencia != null) {
            sugerencia = sugerencia.trim().replace("\"", "");
        }

        publicacion.setTextoImagen(sugerencia);
        publicacionRepository.save(publicacion);

        return new SugerenciaTextoResponse(publicacion.getId(), sugerencia);
    }

    @Override
    @Transactional
    public PublicacionResponse actualizarTextoImagen(Long id, String textoImagen, String estiloImagen, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);
        if (textoImagen != null) {
            publicacion.setTextoImagen(textoImagen);
        }
        if (estiloImagen != null) {
            publicacion.setEstiloImagen(estiloImagen);
        }
        PublicacionEntity saved = publicacionRepository.save(publicacion);
        return publicacionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PublicacionResponse generarImagen(Long id, String userEmail) {
        PublicacionEntity publicacion = getPublicacion(id, userEmail);
        if (publicacion.getEstado() == EstadoPublicacion.PUBLICADA) {
            throw new IllegalArgumentException("La publicación ya fue publicada");
        }
        if (publicacion.getFotos().isEmpty()) {
            throw new IllegalArgumentException("Tenés que elegir al menos una foto");
        }

        String nombreArchivo = "publicacion_" + publicacion.getId() + "_" + UUID.randomUUID() + ".png";
        String ruta;
        try {
            byte[] imagen = openAiImageService.generarImagen(publicacion.getFotos(), armarPromptImagen(publicacion));
            ruta = storageService.guardarBytes("generadas", nombreArchivo, "image/png", imagen);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al generar la imagen: " + e.getMessage());
        }

        ArchivoEntity archivo = ArchivoEntity.builder()
                .ruta(ruta)
                .nombreOriginal(nombreArchivo)
                .contentType("image/png")
                .tipo(TipoArchivo.IMAGEN)
                .propiedad(publicacion.getPropiedad())
                .build();
        publicacion.setImagenGenerada(archivoRepository.save(archivo));

        PublicacionEntity saved = publicacionRepository.save(publicacion);
        return publicacionMapper.toResponse(saved);
    }

    private String armarPromptImagen(PublicacionEntity publicacion) {
        PropiedadEntity p = publicacion.getPropiedad();
        StringBuilder sb = new StringBuilder();
        sb.append("Hacé una imagen cuadrada para ").append(publicacion.getRed())
                .append(" con las fotos de esta propiedad. Mejorá la luz y los colores pero no cambies nada de la propiedad (ni muebles, ni ventanas, ni ambientes).\n")
                .append("Poné este texto: ").append(p.getOperacion()).append(" - ")
                .append(p.getAmbientes()).append(" ambientes - $").append(p.getPrecio().intValue()).append("\n");

        if (publicacion.getTextoImagen() != null) {
            sb.append("Título: ").append(publicacion.getTextoImagen()).append("\n");
        } else {
            sb.append("Inventá un título corto\n");
        }
        if (publicacion.getEstiloImagen() != null) {
            sb.append("Estilo: ").append(publicacion.getEstiloImagen()).append("\n");
        }
        if (publicacion.getIndicaciones() != null && !publicacion.getIndicaciones().isBlank()) {
            sb.append("Indicaciones: ").append(publicacion.getIndicaciones()).append("\n");
        }
        return sb.toString();
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
        if (publicacion.getEstiloImagen() != null && !publicacion.getEstiloImagen().isBlank()) {
            sb.append("Estilo visual preferido para la imagen: ").append(publicacion.getEstiloImagen()).append("\n");
        }
        if (publicacion.getTextoImagen() != null && !publicacion.getTextoImagen().isBlank()) {
            sb.append("Texto o gancho que irá sobre la imagen: \"").append(publicacion.getTextoImagen()).append("\"\n");
        }
        return sb.toString();
    }
}
