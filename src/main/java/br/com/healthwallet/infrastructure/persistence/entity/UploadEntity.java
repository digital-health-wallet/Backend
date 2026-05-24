package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "uploads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_upload")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exame")
    private ExameEntity exame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receita")
    private ReceitaEntity receita;

    @Column(name = "base_64", columnDefinition = "TEXT", nullable = false)
    private String base64;
}