package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.infrastructure.persistence.entity.AlergiaEntity;
import br.com.healthwallet.infrastructure.persistence.entity.PacienteEntity;
import org.springframework.stereotype.Component;

@Component
public class AlergiaMapper {
    public Alergia toDomain(AlergiaEntity entity) {
        if (entity == null) return null;

        Alergia domain = new Alergia();
        domain.setId(entity.getId());
        domain.setTipo(entity.getTipo());
        domain.setDescricao(entity.getDescricao());

        if (entity.getPaciente() != null) {
            domain.setIdPaciente(entity.getPaciente().getId());
        }

        return domain;
    }

    public AlergiaEntity toEntity(Alergia domain) {
        if (domain == null) return null;

        PacienteEntity pacienteEntity = null;
        if (domain.getIdPaciente() != null) {
            pacienteEntity = PacienteEntity.builder()
                    .id(domain.getIdPaciente())
                    .build();
        }

        return AlergiaEntity.builder()
                .id(domain.getId())
                .paciente(pacienteEntity)
                .tipo(domain.getTipo())
                .descricao(domain.getDescricao())
                .build();
    }

}
