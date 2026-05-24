package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Exame;
import br.com.healthwallet.domain.repository.ExameRepository;
import br.com.healthwallet.infrastructure.persistence.entity.ExameEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.ExameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExameRepositoryImpl implements ExameRepository {

    private final ExameJpaRepository jpaRepository;
    private final ExameMapper mapper;

    @Override
    public Exame salvar(Exame exame) {
        ExameEntity entity = mapper.toEntity(exame);
        ExameEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Exame> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Exame> buscarPorAgendamento(Long idAgendamento) {
        return jpaRepository.findByAgendamentoId(idAgendamento).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exame> buscarAvulsos() {
        return jpaRepository.findByAgendamentoIsNull().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}