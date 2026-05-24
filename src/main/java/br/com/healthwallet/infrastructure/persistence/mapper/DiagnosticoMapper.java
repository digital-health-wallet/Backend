package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.healthwallet.infrastructure.persistence.entity.DiagnosticoEntity;
import br.com.healthwallet.infrastructure.persistence.repository.JpaAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiagnosticoMapper {

    private final JpaAgendamentoRepository agendamentoJpaRepository;

    public Diagnostico toDomain(DiagnosticoEntity entity) {
        Diagnostico domain = new Diagnostico();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setCid(entity.getCid());
        domain.setDescricao(entity.getDescricao());
        domain.setDoencaCronica(entity.getDoencaCronica());
        if (entity.getAgendamento() != null) {
            domain.setIdAgendamento(entity.getAgendamento().getId());
        }
        return domain;
    }

    public DiagnosticoEntity toEntity(Diagnostico domain) {
        AgendamentoEntity agendamento = null;
        if (domain.getIdAgendamento() != null) {
            agendamento = agendamentoJpaRepository.getReferenceById(domain.getIdAgendamento());
        }

        return DiagnosticoEntity.builder()
                .id(domain.getId())
                .agendamento(agendamento)
                .nome(domain.getNome())
                .cid(domain.getCid())
                .descricao(domain.getDescricao())
                .doencaCronica(domain.getDoencaCronica() != null ? domain.getDoencaCronica() : false)
                .build();
    }
}