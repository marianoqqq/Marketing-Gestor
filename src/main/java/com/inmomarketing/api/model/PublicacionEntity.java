package com.inmomarketing.api.model;

import com.inmomarketing.api.enums.EstadoPublicacion;
import com.inmomarketing.api.enums.Formato;
import com.inmomarketing.api.enums.RedSocial;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RedSocial red;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Formato formato;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    @Column(columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPublicacion estado;

    @Column(name = "publicada_en")
    private LocalDateTime publicadaEn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propiedad_id", nullable = false)
    @ToString.Exclude
    private PropiedadEntity propiedad;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = EstadoPublicacion.BORRADOR;
        }
    }
}
