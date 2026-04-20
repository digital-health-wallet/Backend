package br.com.healthwallet.infrastructure.persistence.entity;


import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.model.enums.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private PacienteEntity paciente;

    @Column(nullable = false, length = 70)
    private String especialidade;

    @Column(name = "nome_clinica", length = 100)
    private String nomeClinica;

    @Column(name = "motivo_consulta", columnDefinition = "TEXT")
    private String motivoConsulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_consulta", length = 20)
    private TipoConsulta tipoConsulta;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento;

    @Column(name = "hora_agendamento", nullable = false)
    private LocalTime horaAgendamento;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private StatusAgendamento status;

    @Column(nullable = false)
    private Boolean favorito;

    @Column(nullable = false)
    private Boolean arquivado;

    @Column(name = "google_event_id", length = 128)
    private String googleEventId;
}
