package com.inmomarketing.api.dto.response;

import com.inmomarketing.api.enums.TipoArchivo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoResponse {
    private Long id;
    private TipoArchivo tipo;
    private String nombreOriginal;
    private String contentType;
    private String url;
}