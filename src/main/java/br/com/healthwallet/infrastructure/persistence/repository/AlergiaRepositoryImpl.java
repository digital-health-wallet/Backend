package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.infrastructure.persistence.entity.AlergiaEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.AlergiaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlergiaRepositoryImpl implements AlergiaRepository {
    private final AlergiaJpaRepository jpaRepository;
    private final AlergiaMapper mapper;

    @Override
    public List<Alergia> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Alergia> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Alergia> buscarPorPaciente(Long idPaciente) {
        return jpaRepository.findByPacienteId(idPaciente).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Alergia salvar(Alergia alergia) {
        AlergiaEntity entity = mapper.toEntity(alergia);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}
