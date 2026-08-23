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

    // Relacionamento com Agendamento (nulo para diagnósticos avulsos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = true)
    private AgendamentoEntity agendamento;

    // Vínculo direto com o paciente: preenchido sempre, inclusive quando o diagnóstico
    // é avulso (sem agendamento).
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(length = 50)
    private String nome;

    @Column(length = 50)
    private String cid;

    @Column(length = 200)
    private String descricao;

    @Column(name = "doenca_cronica")
    private Boolean doencaCronica;

    @Builder.Default
    @Column(name = "ativo")
    private Boolean ativo = true;
}