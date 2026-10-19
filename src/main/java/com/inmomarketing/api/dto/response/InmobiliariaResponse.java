package com.inmomarketing.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InmobiliariaResponse {
    private Long id;
    private String nombre;
    private String telefono;
    private String instagram;
    private String direccion;
    private String logoUrl;
}