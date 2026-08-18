package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Contato;
import br.com.healthwallet.infrastructure.persistence.entity.ContatoEntity;
import org.springframework.stereotype.Component;

@Component
public class ContatoMapper {

    public Contato toDomain(ContatoEntity entity) {
        if (entity == null) return null;
        Contato domain = new Contato();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setParentesco(entity.getParentesco());
        domain.setTelefone(entity.getTelefone());
        domain.setEmail(entity.getEmail());
        return domain;
    }

    public ContatoEntity toEntity(Contato domain) {
        if (domain == null) return null;
        return ContatoEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .parentesco(domain.getParentesco())
                .telefone(domain.getTelefone())
                .email(domain.getEmail())
                .build();
    }
}
