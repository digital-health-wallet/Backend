package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Exame;
import br.com.healthwallet.domain.model.Upload;
import br.com.healthwallet.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.healthwallet.infrastructure.persistence.entity.ExameEntity;
import br.com.healthwallet.infrastructure.persistence.entity.UploadEntity;
import br.com.healthwallet.infrastructure.persistence.repository.JpaAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExameMapper {

    private final JpaAgendamentoRepository agendamentoJpaRepository;

    public Exame toDomain(ExameEntity entity) {
        Exame domain = new Exame();
        domain.setId(entity.getId());
        domain.setNomeExame(entity.getNomeExame());
        domain.setDataHoraExame(entity.getDataHoraExame());
        domain.setObservacoes(entity.getObservacoes());
        domain.setAtivo(entity.getAtivo());
        domain.setIdPaciente(entity.getIdPaciente());

        if (entity.getAgendamento() != null) {
            domain.setIdAgendamento(entity.getAgendamento().getId());
        }

        if (entity.getUploads() != null) {
            List<Upload> domainUploads = entity.getUploads().stream()
                    .map(this::uploadToDomain)
                    .collect(Collectors.toList());
            domain.setUploads(domainUploads);
        }

        return domain;
    }

    public ExameEntity toEntity(Exame domain) {
        AgendamentoEntity agendamento = null;
        if (domain.getIdAgendamento() != null) {
            agendamento = agendamentoJpaRepository.getReferenceById(domain.getIdAgendamento());
        }

        ExameEntity entity = ExameEntity.builder()
                .id(domain.getId())
                .agendamento(agendamento)
                .nomeExame(domain.getNomeExame())
                .dataHoraExame(domain.getDataHoraExame())
                .observacoes(domain.getObservacoes())
                .ativo(domain.getAtivo() != null ? domain.getAtivo() : true)
                .idPaciente(domain.getIdPaciente())
                .uploads(new ArrayList<>())
                .build();

        if (domain.getUploads() != null) {
            List<UploadEntity> uploadEntities = domain.getUploads().stream()
                    .map(upload -> {
                        UploadEntity ue = uploadToEntity(upload);
                        ue.setExame(entity);
                        return ue;
                    })
                    .collect(Collectors.toList());
            entity.setUploads(uploadEntities);
        }

        return entity;
    }

    private Upload uploadToDomain(UploadEntity entity) {
        Upload domain = new Upload();
        domain.setId(entity.getId());
        domain.setBase64(entity.getBase64());
        if (entity.getExame() != null) {
            domain.setIdExame(entity.getExame().getId());
        }
        if (entity.getReceita() != null) {
            domain.setIdReceita(entity.getReceita().getId());
        }
        return domain;
    }

    private UploadEntity uploadToEntity(Upload domain) {
        return UploadEntity.builder()
                .id(domain.getId())
                .base64(domain.getBase64())
                .build();
    }
}