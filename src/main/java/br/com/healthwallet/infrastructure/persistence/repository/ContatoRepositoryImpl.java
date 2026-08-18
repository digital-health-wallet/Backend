package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Contato;
import br.com.healthwallet.domain.repository.ContatoRepository;
import br.com.healthwallet.infrastructure.persistence.entity.ContatoEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.ContatoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContatoRepositoryImpl implements ContatoRepository {
    private final ContatoJpaRepository jpaRepository;
    private final ContatoMapper mapper;

    @Override
    public List<Contato> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Contato> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Contato salvar(Contato contato) {
        ContatoEntity entity = mapper.toEntity(contato);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}
