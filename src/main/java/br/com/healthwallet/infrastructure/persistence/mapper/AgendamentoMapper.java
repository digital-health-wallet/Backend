package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.healthwallet.infrastructure.persistence.entity.PacienteEntity;
import br.com.healthwallet.infrastructure.persistence.entity.ProfissionalEntity;
import br.com.healthwallet.infrastructure.persistence.repository.ProfissionalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgendamentoMapper {

    private final ProfissionalJpaRepository profissionalJpaRepository;

    public Agendamento toDomain(AgendamentoEntity entity) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(entity.getId());
        agendamento.setIdPaciente(entity.getPaciente().getId());
        agendamento.setEspecialidade(entity.getEspecialidade());
        agendamento.setNomeClinica(entity.getNomeClinica());
        agendamento.setMotivoConsulta(entity.getMotivoConsulta());
        agendamento.setTipoConsulta(entity.getTipoConsulta());
        agendamento.setDataAgendamento(entity.getDataAgendamento());
        agendamento.setHoraAgendamento(entity.getHoraAgendamento());
        agendamento.setHoraFim(entity.getHoraFim());
        agendamento.setStatus(entity.getStatus());
        agendamento.setFavorito(entity.getFavorito());
        agendamento.setArquivado(entity.getArquivado());
        agendamento.setGoogleEventId(entity.getGoogleEventId());
        if (entity.getProfissional() != null) {
            agendamento.setIdProfissional(entity.getProfissional().getId());
            agendamento.setNomeProfissional(entity.getProfissional().getNomeProfissional());
        }
        return agendamento;
    }

    public AgendamentoEntity toEntity(Agendamento agendamento) {
        PacienteEntity paciente = new PacienteEntity();
        paciente.setId(agendamento.getIdPaciente());

        ProfissionalEntity profissional = null;
        if (agendamento.getIdProfissional() != null) {
            profissional = profissionalJpaRepository.getReferenceById(agendamento.getIdProfissional());
        }

        return AgendamentoEntity.builder()
                .id(agendamento.getId())
                .paciente(paciente)
                .profissional(profissional)
                .especialidade(agendamento.getEspecialidade())
                .nomeClinica(agendamento.getNomeClinica())
                .motivoConsulta(agendamento.getMotivoConsulta())
                .tipoConsulta(agendamento.getTipoConsulta())
                .dataAgendamento(agendamento.getDataAgendamento())
                .horaAgendamento(agendamento.getHoraAgendamento())
                .horaFim(agendamento.getHoraFim())
                .status(agendamento.getStatus())
                .favorito(agendamento.getFavorito() != null ? agendamento.getFavorito() : false)
                .arquivado(agendamento.getArquivado() != null ? agendamento.getArquivado() : false)
                .googleEventId(agendamento.getGoogleEventId())
                .build();
    }
}