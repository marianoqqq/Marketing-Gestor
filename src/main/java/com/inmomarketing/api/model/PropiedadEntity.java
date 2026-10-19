package com.inmomarketing.api.model;

import com.inmomarketing.api.enums.EstadoPropiedad;
import com.inmomarketing.api.enums.TipoOperacion;
import com.inmomarketing.api.enums.TipoPropiedad;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "propiedades")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropiedadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPropiedad tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoOperacion operacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPropiedad estado;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;

    private String direccion;
    private int ambientes;
    private double metrosCuadrados;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inmobiliaria_id", nullable = false)
    @ToString.Exclude
    private InmobiliariaEntity inmobiliaria;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = EstadoPropiedad.DISPONIBLE;
        }
    }
}