package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.infrastructure.persistence.entity.PacienteEntity;
import br.com.healthwallet.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    public Paciente toDomain(PacienteEntity entity) {
        if (entity == null) return null;

        Paciente domain = new Paciente();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setCpf(entity.getCpf());
        domain.setDataNascimento(entity.getDataNascimento());
        domain.setTipoSanguineo(entity.getTipoSanguineo());
        domain.setCodigoEmergencia(entity.getCodigoEmergencia());
        domain.setFichaEmergencialAtiva(entity.getFichaEmergencialAtiva());

        if (entity.getUsuario() != null) {
            domain.setIdUsuario(entity.getUsuario().getId());
        }

        return domain;
    }

    public PacienteEntity toEntity(Paciente domain) {
        if (domain == null) return null;

        UsuarioEntity usuarioEntity = null;
        if (domain.getIdUsuario() != null) {
            usuarioEntity = UsuarioEntity.builder()
                    .id(domain.getIdUsuario())
                    .build();
        }

        return PacienteEntity.builder()
                .id(domain.getId())
                .usuario(usuarioEntity)
                .nome(domain.getNome())
                .cpf(domain.getCpf())
                .dataNascimento(domain.getDataNascimento())
                .tipoSanguineo(domain.getTipoSanguineo())
                .codigoEmergencia(domain.getCodigoEmergencia())
                .fichaEmergencialAtiva(domain.getFichaEmergencialAtiva())
                .build();
    }
}