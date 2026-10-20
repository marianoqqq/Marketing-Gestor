package com.inmomarketing.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de mail no es correcto")
        String email,

        @NotBlank(message = "La password es obligatoria")
        @Size(min = 4, message = "La password debe tener al menos 4 caracteres")
        String password,

        @NotBlank(message = "El nombre de la inmobiliaria es obligatorio")
        @Size(max = 100, message = "El nombre de la inmobiliaria no puede superar los 100 caracteres")
        String nombreInmobiliaria
) {
}