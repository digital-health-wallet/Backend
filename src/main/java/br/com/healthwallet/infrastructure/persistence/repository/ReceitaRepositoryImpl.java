package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.infrastructure.persistence.entity.ReceitaEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.ReceitaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReceitaRepositoryImpl implements ReceitaRepository {

    private final ReceitaJpaRepository jpaRepository;
    private final ItemReceitaJpaRepository itemReceitaJpaRepository;
    private final ReceitaMapper mapper;

    @Override
    public Receita salvar(Receita receita) {
        ReceitaEntity entity = mapper.toEntity(receita);
        ReceitaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Receita> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Receita> buscarPorAgendamento(Long idAgendamento) {
        return jpaRepository.findByAgendamentoId(idAgendamento).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Receita> buscarAvulsas() {
        return jpaRepository.findByAgendamentoIsNull().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemReceita> buscarItensUsoContinuoPorPaciente(Long idPaciente) {
        return itemReceitaJpaRepository.findUsoContinuoPorPaciente(idPaciente).stream()
                .map(mapper::itemToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}