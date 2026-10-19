package com.inmomarketing.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InmobiliariaRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String telefono;

    @Size(max = 15, message = "El Instagram no puede superar los 15 caracteres")
    private String instagram;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String direccion;

    private String logoUrl;
}