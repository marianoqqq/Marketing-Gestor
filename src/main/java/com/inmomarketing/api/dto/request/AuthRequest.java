package com.inmomarketing.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotNull(message = "El email debe ser obligatorio")
    private String email;

    @NotNull(message = "La contraseña es obligatoria")
    private String password;


}
