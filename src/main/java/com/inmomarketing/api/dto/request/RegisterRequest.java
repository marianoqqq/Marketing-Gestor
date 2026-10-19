package com.inmomarketing.api.dto.request;

import com.inmomarketing.api.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de mail no es correcto")
        String email,

        @NotBlank(message = "La password es obligatoria")
        @Size(min = 4, message = "La password debe tener al menos 4 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        UserRole role
) {
}
