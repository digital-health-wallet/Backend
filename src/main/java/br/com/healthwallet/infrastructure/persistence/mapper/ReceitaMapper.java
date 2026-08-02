package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.model.Upload;
import br.com.healthwallet.infrastructure.persistence.entity.*;
import br.com.healthwallet.infrastructure.persistence.repository.JpaAgendamentoRepository;
import br.com.healthwallet.infrastructure.persistence.repository.MedicamentoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReceitaMapper {

    private final JpaAgendamentoRepository agendamentoJpaRepository;
    private final MedicamentoJpaRepository medicamentoJpaRepository;

    public Receita toDomain(ReceitaEntity entity) {
        Receita domain = new Receita();
        domain.setId(entity.getId());
        domain.setDataEmissao(entity.getDataEmissao());
        domain.setOrientacoesGerais(entity.getOrientacoesGerais());

        if (entity.getAgendamento() != null) {
            domain.setIdAgendamento(entity.getAgendamento().getId());
        }

        if (entity.getItens() != null) {
            List<ItemReceita> domainItens = entity.getItens().stream()
                    .map(this::itemToDomain)
                    .collect(Collectors.toList());
            domain.setItens(domainItens);
        }

        if (entity.getUploads() != null) {
            List<Upload> domainUploads = entity.getUploads().stream()
                    .map(this::uploadToDomain)
                    .collect(Collectors.toList());
            domain.setUploads(domainUploads);
        }

        return domain;
    }

    public ReceitaEntity toEntity(Receita domain) {
        AgendamentoEntity agendamento = null;
        if (domain.getIdAgendamento() != null) {
            agendamento = agendamentoJpaRepository.getReferenceById(domain.getIdAgendamento());
        }

        ReceitaEntity entity = ReceitaEntity.builder()
                .id(domain.getId())
                .agendamento(agendamento)
                .dataEmissao(domain.getDataEmissao())
                .orientacoesGerais(domain.getOrientacoesGerais())
                .itens(new ArrayList<>())
                .uploads(new ArrayList<>())
                .build();

        if (domain.getItens() != null) {
            List<ItemReceitaEntity> itemEntities = domain.getItens().stream()
                    .map(item -> {
                        ItemReceitaEntity ie = itemToEntity(item);
                        ie.setReceita(entity); // Vínculo bidirecional JPA
                        return ie;
                    })
                    .collect(Collectors.toList());
            entity.setItens(itemEntities);
        }

        if (domain.getUploads() != null) {
            List<UploadEntity> uploadEntities = domain.getUploads().stream()
                    .map(upload -> {
                        UploadEntity ue = uploadToEntity(upload);
                        ue.setReceita(entity);
                        return ue;
                    })
                    .collect(Collectors.toList());
            entity.setUploads(uploadEntities);
        }

        return entity;
    }

    public ItemReceita itemToDomain(ItemReceitaEntity entity) {
        ItemReceita domain = new ItemReceita();
        domain.setId(entity.getId());
        domain.setPosologia(entity.getPosologia());
        domain.setUsoContinuo(entity.getUsoContinuo());
        domain.setIdReceita(entity.getReceita().getId());

        if (entity.getMedicamento() != null) {
            domain.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        }
        return domain;
    }

    private ItemReceitaEntity itemToEntity(ItemReceita domain) {
        MedicamentoEntity medicamentoEntity = null;
        if (domain.getMedicamento() != null) {
            if (domain.getMedicamento().getId() != null) {
                // Medicamento existente -> Usa referência JPA
                medicamentoEntity = medicamentoJpaRepository.getReferenceById(domain.getMedicamento().getId());
            } else {
                medicamentoEntity = medicamentoToEntity(domain.getMedicamento());
            }
        }

        return ItemReceitaEntity.builder()
                .id(domain.getId())
                .posologia(domain.getPosologia())
                .usoContinuo(domain.getUsoContinuo() != null ? domain.getUsoContinuo() : false)
                .medicamento(medicamentoEntity)
                .build();
    }

    private Medicamento medicamentoToDomain(MedicamentoEntity entity) {
        Medicamento domain = new Medicamento();
        domain.setId(entity.getId());
        domain.setNomeMedicamento(entity.getNomeMedicamento());
        domain.setLaboratorio(entity.getLaboratorio());
        domain.setFeedback(entity.getFeedback());
        return domain;
    }

    private MedicamentoEntity medicamentoToEntity(Medicamento domain) {
        return MedicamentoEntity.builder()
                .id(domain.getId())
                .nomeMedicamento(domain.getNomeMedicamento())
                .laboratorio(domain.getLaboratorio())
                .feedback(domain.getFeedback())
                .build();
    }

    private Upload uploadToDomain(UploadEntity entity) {
        Upload domain = new Upload();
        domain.setId(entity.getId());
        domain.setBase64(entity.getBase64());
        return domain;
    }

    private UploadEntity uploadToEntity(Upload domain) {
        return UploadEntity.builder()
                .id(domain.getId())
                .base64(domain.getBase64())
                .build();
    }
}