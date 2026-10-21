package com.inmomarketing.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SugerenciaTextoResponse {
    private Long publicacionId;
    private String textoSugerido;
}
