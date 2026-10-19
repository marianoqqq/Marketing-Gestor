package com.inmomarketing.api.dto.request;

import com.inmomarketing.api.enums.EstadoPropiedad;
import com.inmomarketing.api.enums.TipoOperacion;
import com.inmomarketing.api.enums.TipoPropiedad;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadRequest {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String titulo;

    @NotNull(message = "El tipo es obligatorio")
    private TipoPropiedad tipo;

    @NotNull(message = "La operación es obligatoria")
    private TipoOperacion operacion;

    private EstadoPropiedad estado;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String direccion;

    @Min(value = 1, message = "Debe tener al menos 1 ambiente")
    private int ambientes;

    @Positive(message = "Los metros cuadrados deben ser mayores a 0")
    private double metrosCuadrados;

    private String descripcion;
}