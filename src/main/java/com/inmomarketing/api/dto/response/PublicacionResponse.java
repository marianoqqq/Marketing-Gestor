package com.inmomarketing.api.dto.response;

import com.inmomarketing.api.enums.EstadoPublicacion;
import com.inmomarketing.api.enums.Formato;
import com.inmomarketing.api.enums.RedSocial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionResponse {
    private Long id;
    private Long propiedadId;
    private RedSocial red;
    private Formato formato;
    private String indicaciones;
    private String texto;
    private String textoImagen;
    private String estiloImagen;
    private EstadoPublicacion estado;
    private LocalDateTime publicadaEn;
    private List<Long> fotoIds;
    private Long imagenGeneradaId;
}
