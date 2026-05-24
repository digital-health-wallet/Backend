package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exames")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exame")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = true)
    private AgendamentoEntity agendamento;

    @Column(name = "nome_exame", columnDefinition = "TEXT")
    private String nomeExame;

    @Column(name = "data_hora_exame")
    private LocalDate dataHoraExame;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Builder.Default
    @OneToMany(mappedBy = "exame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadEntity> uploads = new ArrayList<>();
}