package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import br.com.healthwallet.infrastructure.persistence.entity.ProfissionalEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.ProfissionalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfissionalRepositoryImpl implements ProfissionalRepository{
    private final ProfissionalJpaRepository jpaRepository;
    private final ProfissionalMapper mapper;

    @Override
    public List<Profissional> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Profissional> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Profissional> buscarPorNome(String nomeProfissional) {
        return jpaRepository.findByNomeProfissionalIgnoreCase(nomeProfissional).map(mapper::toDomain);
    }

    @Override
    public Profissional salvar(Profissional profissional) {
        ProfissionalEntity entity = mapper.toEntity(profissional);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}
