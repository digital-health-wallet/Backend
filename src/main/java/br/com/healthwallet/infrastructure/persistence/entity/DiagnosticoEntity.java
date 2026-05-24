package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diagnostico")
@Getter
@Setter
@Builder // Adicionado
@NoArgsConstructor // Adicionado para o JPA não quebrar
@AllArgsConstructor
public class DiagnosticoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagnostico")
    private Long id;

    // Relacionamento com Agendamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = false)
    private AgendamentoEntity agendamento;

    @Column(length = 50)
    private String nome;

    @Column(length = 50)
    private String cid;

    @Column(length = 200)
    private String descricao;

    @Column(name = "doenca_cronica")
    private Boolean doencaCronica;
}