package com.inmomarketing.api.dto.response;

import com.inmomarketing.api.enums.EstadoPropiedad;
import com.inmomarketing.api.enums.TipoOperacion;
import com.inmomarketing.api.enums.TipoPropiedad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadResponse {
    private Long id;
    private String titulo;
    private TipoPropiedad tipo;
    private TipoOperacion operacion;
    private EstadoPropiedad estado;
    private BigDecimal precio;
    private String direccion;
    private int ambientes;
    private double metrosCuadrados;
    private String descripcion;
    private Long inmobiliariaId;
}