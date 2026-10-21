package com.inmomarketing.api.dto.request;

import com.inmomarketing.api.enums.Formato;
import com.inmomarketing.api.enums.RedSocial;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionRequest {

    @NotNull(message = "El id de la propiedad es obligatorio")
    private Long propiedadId;

    @NotNull(message = "La red social es obligatoria")
    private RedSocial red;

    @NotNull(message = "El formato es obligatorio")
    private Formato formato;

    private String indicaciones;
}
