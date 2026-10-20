package com.inmomarketing.api.model;

import com.inmomarketing.api.enums.TipoArchivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "archivos")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombreGuardado;

    @Column(length = 255)
    private String nombreOriginal;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoArchivo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id")
    @ToString.Exclude
    private PropiedadEntity propiedad;
}