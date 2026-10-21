package com.inmomarketing.api.model;

import com.inmomarketing.api.enums.EstadoPublicacion;
import com.inmomarketing.api.enums.Formato;
import com.inmomarketing.api.enums.RedSocial;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "texto_imagen", columnDefinition = "TEXT")
    private String textoImagen;

    @Column(name = "estilo_imagen", length = 100)
    private String estiloImagen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPublicacion estado;

    @Column(name = "publicada_en")
    private LocalDateTime publicadaEn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propiedad_id", nullable = false)
    @ToString.Exclude
    private PropiedadEntity propiedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagen_generada_id")
    @ToString.Exclude
    private ArchivoEntity imagenGenerada;

    @ManyToMany
    @JoinTable(name = "publicacion_fotos",
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "archivo_id"))
    @ToString.Exclude
    @Builder.Default
    private List<ArchivoEntity> fotos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = EstadoPublicacion.BORRADOR;
        }
    }
}
