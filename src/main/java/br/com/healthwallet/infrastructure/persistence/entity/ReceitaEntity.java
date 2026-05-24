package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receitas")
@Getter
@Setter
@Builder // Adicionado
@NoArgsConstructor // Adicionado para o JPA não quebrar
@AllArgsConstructor
public class ReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receita")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = true)
    private AgendamentoEntity agendamento;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @Column(name = "orientacoes_gerais", columnDefinition = "TEXT")
    private String orientacoesGerais;

    @Builder.Default
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemReceitaEntity> itens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadEntity> uploads = new ArrayList<>();
}